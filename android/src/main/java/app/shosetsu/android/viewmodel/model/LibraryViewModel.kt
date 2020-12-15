package app.shosetsu.android.viewmodel.model

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.ext.removeFirst
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.StartUpdateWorkerUseCase
import app.shosetsu.android.domain.usecases.load.LoadLibraryUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsHUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsPUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUITypeUseCase
import app.shosetsu.android.domain.usecases.update.UpdateBookmarkedNovelUseCase
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.viewmodel.abstracted.ILibraryViewModel
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import app.shosetsu.common.enums.InclusionState
import app.shosetsu.common.enums.InclusionState.EXCLUDE
import app.shosetsu.common.enums.InclusionState.INCLUDE
import app.shosetsu.common.enums.NovelSortType
import app.shosetsu.common.enums.NovelUIType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.luaj.vm2.ast.Str
import java.util.Locale.getDefault as LGD

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class LibraryViewModel(
	private val libraryAsCardsUseCase: LoadLibraryUseCase,
	private val updateBookmarkedNovelUseCase: UpdateBookmarkedNovelUseCase,
	private val isOnlineUseCase: IsOnlineUseCase,
	private var startUpdateWorkerUseCase: StartUpdateWorkerUseCase,
	private val reportExceptionUseCase: ReportExceptionUseCase,
	private val loadNovelUITypeUseCase: LoadNovelUITypeUseCase,
	private val loadNovelUIColumnsHUseCase: LoadNovelUIColumnsHUseCase,
	private val loadNovelUIColumnsPUseCase: LoadNovelUIColumnsPUseCase,
) : ILibraryViewModel() {
	private var novelUIType: NovelUIType = NovelUIType.fromInt(NovelCardType.default)

	private var columnP: Int = ChapterColumnsInPortait.default

	private var columnH: Int = ChapterColumnsInLandscape.default

	private val librarySourceFlow: Flow<HResult<List<ABookmarkedNovelUI>>> = libraryAsCardsUseCase()

	@ExperimentalCoroutinesApi
	override val genresFlow: LiveData<List<String>> by lazy {
		stripOutList { it.genres }
	}

	@ExperimentalCoroutinesApi
	override val tagsFlow: LiveData<List<String>> by lazy {
		stripOutList { it.tags }
	}

	@ExperimentalCoroutinesApi
	override val authorsFlow: LiveData<List<String>> by lazy {
		stripOutList { it.authors }
	}

	@ExperimentalCoroutinesApi
	override val artistsFlow: LiveData<List<String>> by lazy {
		stripOutList { it.artists }
	}
	private var _novelSortType: NovelSortType = NovelSortType.BY_TITLE
		set(value) {
			field = value
			launchIO {
				logV("state change ${novelSortTypeFlow.value}")
				novelSortTypeFlow.emit(value)
				logV("New ${novelSortTypeFlow.value}")
			}
		}
	private var _areNovelsReversed: Boolean = false
		set(value) {
			field = value
			launchIO { areNovelsReversedFlow.emit(value) }
		}

	private val _genreFilter by lazy { ArrayList<Pair<String, InclusionState>>() }
	private val _authorFilter by lazy { ArrayList<Pair<String, InclusionState>>() }
	private val _artistFilter by lazy { ArrayList<Pair<String, InclusionState>>() }
	private val _tagFilter by lazy { ArrayList<Pair<String, InclusionState>>() }

	private val novelSortTypeFlow by lazy {
		MutableStateFlow(
			_novelSortType
		)
	}
	private val areNovelsReversedFlow by lazy {
		MutableStateFlow(
			_areNovelsReversed
		)
	}
	private val genreFilterFlow by lazy {
		MutableStateFlow(
			_genreFilter
		).also {
			launchIO {
				it.collectLatest {
					logV("$it")
				}
			}
		}
	}
	private val authorFilterFlow by lazy {
		MutableStateFlow(
			_authorFilter
		)
	}
	private val artistFilterFlow by lazy {
		MutableStateFlow(
			_artistFilter
		)
	}
	private val tagFilterFlow by lazy {
		MutableStateFlow(
			_tagFilter
		)
	}

	/**
	 * This is outputed to the UI to display all the novels
	 *
	 * This also connects all the filtering as well
	 */
	override val liveData: LiveData<HResult<List<ABookmarkedNovelUI>>> by lazy {
		librarySourceFlow
			.combineArtistFilter()
			.combineAuthorFilter()
			.combineGenreFilter()
			.combineTagsFilter()
			.combineSortType()
			.combineSortReverse()
			.asIOLiveData()
	}

	init {
		launchIO {
			loadNovelUIColumnsHUseCase().collectLatest {
				columnH = it
			}
			loadNovelUIColumnsPUseCase().collectLatest {
				columnP = it
			}
			loadNovelUITypeUseCase().collectLatest {
				novelUIType = it
			}
		}
	}

	@ExperimentalCoroutinesApi
	private fun stripOutList(
		strip: (ABookmarkedNovelUI) -> List<String>
	): LiveData<List<String>> = librarySourceFlow.mapLatest { result ->
		ArrayList<String>().apply {
			result.handle { list ->
				list.forEach { ui ->
					strip(ui).forEach { key ->
						if (!contains(key.capitalize(LGD())) && key.isNotBlank()) {
							add(key.capitalize(LGD()))
						}
					}
				}
			}
		}
	}.asIOLiveData()

	private fun ArrayList<Pair<String, InclusionState>>.applyFilter(
		list: List<ABookmarkedNovelUI>,
		against: (ABookmarkedNovelUI) -> List<String>
	): List<ABookmarkedNovelUI> {
		var result = list
		forEach { (s, inclusionState) ->
			result = when (inclusionState) {
				INCLUDE ->
					result.filter { ui -> against(ui).any { g -> g.capitalize(LGD()) == s } }
				EXCLUDE ->
					result.filterNot { ui -> against(ui).any { g -> g.capitalize(LGD()) == s } }
			}
		}
		return result
	}

	/**
	 * @param flow What [Flow] to merge in updates from
	 * @param against Return a [List] of [String] to compare against
	 */
	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.applyFilterList(
		flow: Flow<ArrayList<Pair<String, InclusionState>>>,
		against: (ABookmarkedNovelUI) -> List<String>
	) = combine(flow) { novelResult, filters ->
		logV("Yo im in hell")
		novelResult.transform { list ->
			successResult(
				if (filters.isNotEmpty()) filters.applyFilter(list, against) else list
			)
		}
	}

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineGenreFilter() =
		applyFilterList(genreFilterFlow) { it.genres }

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineTagsFilter() =
		applyFilterList(tagFilterFlow) { it.tags }

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineAuthorFilter() =
		applyFilterList(authorFilterFlow) { it.authors }

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineArtistFilter() =
		applyFilterList(artistFilterFlow) { it.artists }

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineSortReverse() =
		combine(areNovelsReversedFlow) { novelResult, reversed ->
			novelResult.transform { list ->
				successResult(
					if (reversed)
						list.reversed()
					else list
				)
			}
		}

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineSortType() =
		combine(novelSortTypeFlow) { novelResult, sortType ->
			novelResult.transform { list ->
				successResult(
					when (sortType) {
						NovelSortType.BY_TITLE -> list.sortedBy { it.title }
						NovelSortType.BY_UNREAD_COUNT -> list.sortedBy { it.unread }
						NovelSortType.BY_ID -> list.sortedBy { it.id }
					}
				)
			}
		}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun getColumnsInP(): Int = columnP

	override fun getColumnsInH(): Int = columnH

	override fun getNovelUIType(): NovelUIType = novelUIType

	override fun isOnline(): Boolean = isOnlineUseCase()

	override fun startUpdateManager() {
		startUpdateWorkerUseCase(true)
	}

	override fun removeFromLibrary(list: List<ABookmarkedNovelUI>) {
		launchIO {
			updateBookmarkedNovelUseCase(list.onEach {
				it.bookmarked = false
			})
		}
	}

	override fun getSortType(): NovelSortType = _novelSortType

	override fun setSortType(novelSortType: NovelSortType) {
		_novelSortType = novelSortType
	}

	override fun isSortReversed(): Boolean = _areNovelsReversed

	override fun setIsSortReversed(reversed: Boolean) {
		_areNovelsReversed = reversed
	}

	override fun addGenreToFilter(genre: String, inclusionState: InclusionState): Boolean =
		_genreFilter.add(genre to inclusionState)
			.also {
				logV("Launch 1")
				launchIO {
					logV("Launch 2")
					genreFilterFlow.emit(_genreFilter)
					logV("Launch 3")
				}
			}

	override fun removeGenreFromFilter(genre: String): Boolean =
		_genreFilter.removeFirst { it.first == genre }
			.also { launchIO { genreFilterFlow.emit(_genreFilter) } }

	override fun getFilterGenres(): List<Pair<String, InclusionState>> = _genreFilter

	override fun addAuthorToFilter(author: String, inclusionState: InclusionState): Boolean =
		_authorFilter.add(author to inclusionState)
			.also { launchIO { authorFilterFlow.emit(_authorFilter) } }

	override fun removeAuthorFromFilter(author: String): Boolean =
		_authorFilter.removeFirst { it.first == author }
			.also { launchIO { authorFilterFlow.emit(_authorFilter) } }

	override fun getFilterAuthors(): List<Pair<String, InclusionState>> = _authorFilter

	override fun addArtistToFilter(artist: String, inclusionState: InclusionState): Boolean =
		_artistFilter.add(artist to inclusionState)
			.also { launchIO { artistFilterFlow.emit(_artistFilter) } }

	override fun removeArtistFromFilter(artist: String): Boolean =
		_artistFilter.removeFirst { it.first == artist }
			.also { launchIO { artistFilterFlow.emit(_artistFilter) } }

	override fun getFilterArtists(): List<Pair<String, InclusionState>> = _artistFilter

	override fun addTagToFilter(tag: String, inclusionState: InclusionState): Boolean =
		_tagFilter.add(tag to inclusionState)
			.also { launchIO { tagFilterFlow.emit(_tagFilter) } }

	override fun removeTagFromFilter(tag: String): Boolean =
		_tagFilter.removeFirst { it.first == tag }
			.also { launchIO { tagFilterFlow.emit(_tagFilter) } }

	override fun getFilterTags(): List<Pair<String, InclusionState>> = _tagFilter

	override fun resetSortAndFilters() {
		_artistFilter.clear()
		_authorFilter.clear()
		_tagFilter.clear()
		_genreFilter.clear()

		launchIO {
			artistFilterFlow.emit(_artistFilter)
			authorFilterFlow.emit(_authorFilter)
			tagFilterFlow.emit(_tagFilter)
			genreFilterFlow.emit(_genreFilter)
		}

		_novelSortType = NovelSortType.BY_TITLE
		_areNovelsReversed = false
	}
}