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
import app.shosetsu.common.enums.NovelSortType
import app.shosetsu.common.enums.NovelUIType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.collections.ArrayList

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

	private val librarySourceFlow = libraryAsCardsUseCase()

	@ExperimentalCoroutinesApi
	override val genresFlow: Flow<List<String>> by lazy {
		librarySourceFlow.mapLatest { result ->
			ArrayList<String>().apply {
				result.handle { list ->
					list.forEach { ui ->
						ui.genres.forEach { key ->
							if (!contains(key.capitalize(Locale.getDefault()))) {
								add(key.capitalize(Locale.getDefault()))
							}
						}
					}
				}
			}
		}
	}

	@ExperimentalCoroutinesApi
	override val tagsFlow: Flow<List<String>> by lazy {
		librarySourceFlow.mapLatest { result ->
			ArrayList<String>().apply {
				result.handle { list ->
					list.forEach { ui ->
						ui.tags.forEach { key ->
							if (!contains(key.capitalize(Locale.getDefault()))) {
								add(key.capitalize(Locale.getDefault()))
							}
						}
					}
				}
			}
		}
	}

	@ExperimentalCoroutinesApi
	override val authorsFlow: Flow<List<String>> by lazy {
		librarySourceFlow.mapLatest { result ->
			ArrayList<String>().apply {
				result.handle { list ->
					list.forEach { ui ->
						ui.authors.forEach { key ->
							if (!contains(key.capitalize(Locale.getDefault()))) {
								add(key.capitalize(Locale.getDefault()))
							}
						}
					}
				}
			}
		}
	}

	@ExperimentalCoroutinesApi
	override val artistsFlow: Flow<List<String>> by lazy {
		librarySourceFlow.mapLatest { result ->
			ArrayList<String>().apply {
				result.handle { list ->
					list.forEach { ui ->
						ui.artists.forEach { key ->
							if (!contains(key.capitalize(Locale.getDefault()))) {
								add(key.capitalize(Locale.getDefault()))
							}
						}
					}
				}
			}
		}
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

	private val _genreFilter by lazy { ArrayList<String>() }

	private val _authorFilter by lazy { ArrayList<String>() }

	private val _artistFilter by lazy { ArrayList<String>() }

	private val _tagFilter by lazy { ArrayList<String>() }

	private val novelSortTypeFlow: MutableStateFlow<NovelSortType> by lazy {
		MutableStateFlow(
			_novelSortType
		)
	}

	private val areNovelsReversedFlow: MutableStateFlow<Boolean> by lazy {
		MutableStateFlow(
			_areNovelsReversed
		)
	}

	private val genreFilterFlow: MutableStateFlow<ArrayList<String>> by lazy {
		MutableStateFlow(
			_genreFilter
		)
	}

	private val authorFilterFlow: MutableStateFlow<ArrayList<String>> by lazy {
		MutableStateFlow(
			_authorFilter
		)
	}

	private val artistFilterFlow: MutableStateFlow<ArrayList<String>> by lazy {
		MutableStateFlow(
			_artistFilter
		)
	}

	private val tagFilterFlow: MutableStateFlow<ArrayList<String>> by lazy {
		MutableStateFlow(
			_tagFilter
		)
	}

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineGenreFilter() =
		combine(genreFilterFlow) { novelResult, filters ->
			novelResult.transform { list ->
				successResult(
					if (filters.isNotEmpty())
						list.filter { novelUI -> novelUI.genres.any { filters.contains(it) } }
					else list
				)
			}
		}

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineTagsFilter() =
		combine(tagFilterFlow) { novelResult, filters ->
			novelResult.transform { list ->
				successResult(
					if (filters.isNotEmpty())
						list.filter { novelUI -> novelUI.tags.any { filters.contains(it) } }
					else list
				)
			}
		}

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineAuthorFilter() =
		combine(authorFilterFlow) { novelResult, filters ->
			novelResult.transform { list ->
				successResult(
					if (filters.isNotEmpty())
						list.filter { novelUI -> novelUI.authors.any { filters.contains(it) } }
					else list
				)
			}
		}

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineArtistFilter() =
		combine(artistFilterFlow) { novelResult, filters ->
			novelResult.transform { list ->
				successResult(
					if (filters.isNotEmpty())
						list.filter { novelUI -> novelUI.artists.any { filters.contains(it) } }
					else list
				)
			}
		}


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
					}.also {
					}
				)
			}
		}

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
			updateBookmarkedNovelUseCase(list.apply {
				forEach {
					it.bookmarked = false
				}
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

	override fun addGenreToFilter(genre: String): Boolean {
		return _genreFilter.add(genre)
			.also { launchIO { genreFilterFlow.emit(_genreFilter) } }
	}

	override fun removeGenreFromFilter(genre: String): Boolean {
		return _genreFilter.remove(genre)
			.also { launchIO { genreFilterFlow.emit(_genreFilter) } }
	}

	override fun getFilterGenres(): List<String> = _genreFilter

	override fun addAuthorToFilter(author: String): Boolean {
		return _authorFilter.add(author)
			.also { launchIO { authorFilterFlow.emit(_authorFilter) } }
	}

	override fun removeAuthorFromFilter(author: String): Boolean {
		return _authorFilter.remove(author)
			.also { launchIO { authorFilterFlow.emit(_authorFilter) } }
	}

	override fun getFilterAuthors(): List<String> = _authorFilter

	override fun addArtistToFilter(artist: String): Boolean {
		return _artistFilter.add(artist)
			.also { launchIO { artistFilterFlow.emit(_artistFilter) } }
	}

	override fun removeArtistFromFilter(artist: String): Boolean {
		return _artistFilter.remove(artist)
			.also { launchIO { artistFilterFlow.emit(_artistFilter) } }
	}

	override fun getFilterArtists(): List<String> = _artistFilter

	override fun addTagToFilter(tag: String): Boolean {
		return _tagFilter.add(tag).also { launchIO { tagFilterFlow.emit(_tagFilter) } }
	}

	override fun removeTagFromFilter(tag: String): Boolean {
		return _tagFilter.remove(tag).also { launchIO { tagFilterFlow.emit(_tagFilter) } }
	}

	override fun getFilterTags(): List<String> = _tagFilter

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