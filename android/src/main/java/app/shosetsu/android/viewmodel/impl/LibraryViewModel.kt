package app.shosetsu.android.viewmodel.impl

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
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.load.LoadLibraryUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsHUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsPUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUITypeUseCase
import app.shosetsu.android.domain.usecases.settings.SetNovelUITypeUseCase
import app.shosetsu.android.domain.usecases.start.StartUpdateWorkerUseCase
import app.shosetsu.android.domain.usecases.update.UpdateBookmarkedNovelUseCase
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.viewmodel.abstracted.ALibraryViewModel
import app.shosetsu.common.consts.settings.SettingKey.ChapterColumnsInLandscape
import app.shosetsu.common.consts.settings.SettingKey.ChapterColumnsInPortait
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.InclusionState
import app.shosetsu.common.enums.InclusionState.EXCLUDE
import app.shosetsu.common.enums.InclusionState.INCLUDE
import app.shosetsu.common.enums.NovelCardType
import app.shosetsu.common.enums.NovelSortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
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
	private val setNovelUITypeUseCase: SetNovelUITypeUseCase
) : ALibraryViewModel() {


	private var columnP: Int = ChapterColumnsInPortait.default

	private var columnH: Int = ChapterColumnsInLandscape.default

	private val librarySourceFlow: Flow<HResult<List<ABookmarkedNovelUI>>> by lazy { libraryAsCardsUseCase() }

	@ExperimentalCoroutinesApi
	override val genresLiveData: LiveData<List<String>> by lazy {
		stripOutList { it.genres }
	}

	@ExperimentalCoroutinesApi
	override val tagsLiveData: LiveData<List<String>> by lazy {
		stripOutList { it.tags }
	}

	@ExperimentalCoroutinesApi
	override val authorsLiveData: LiveData<List<String>> by lazy {
		stripOutList { it.authors }
	}

	@ExperimentalCoroutinesApi
	override val artistsLiveData: LiveData<List<String>> by lazy {
		stripOutList { it.artists }
	}

	@ExperimentalCoroutinesApi
	override val novelCardTypeLiveData: LiveData<NovelCardType> by lazy {
		loadNovelUITypeUseCase().asIOLiveData()
	}

	private val novelSortTypeFlow: MutableStateFlow<NovelSortType> by lazy {
		MutableStateFlow(
			NovelSortType.BY_TITLE
		)
	}
	private val areNovelsReversedFlow: MutableStateFlow<Boolean> by lazy {
		MutableStateFlow(
			false
		)
	}
	private val genreFilterFlow: MutableStateFlow<HashMap<String, InclusionState>> by lazy {
		MutableStateFlow(
			hashMapOf()
		)
	}
	private val authorFilterFlow: MutableStateFlow<HashMap<String, InclusionState>> by lazy {
		MutableStateFlow(
			hashMapOf()
		)
	}
	private val artistFilterFlow: MutableStateFlow<HashMap<String, InclusionState>> by lazy {
		MutableStateFlow(
			hashMapOf()
		)
	}
	private val tagFilterFlow: MutableStateFlow<HashMap<String, InclusionState>> by lazy {
		MutableStateFlow(
			hashMapOf()
		)
	}
	private val unreadStatusFlow: MutableStateFlow<InclusionState?> by lazy {
		MutableStateFlow(null)
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
			.combineUnreadStatus()
			.combineSortType()
			.combineSortReverse()
			.asIOLiveData()
	}

	override val columnsInH: LiveData<Int> by lazy {
		loadNovelUIColumnsHUseCase().asIOLiveData()
	}

	override val columnsInP: LiveData<Int> by lazy {
		loadNovelUIColumnsPUseCase().asIOLiveData()
	}


	/**
	 * Removes the list for filtering from the [ABookmarkedNovelUI] with the flow
	 */
	@ExperimentalCoroutinesApi
	private fun stripOutList(
		strip: (ABookmarkedNovelUI) -> List<String>
	): LiveData<List<String>> = librarySourceFlow.mapLatest { result ->
		ArrayList<String>().apply {
			result.handle { list ->
				list.forEach { ui ->
					strip(ui).forEach { key ->
						if (!contains(key.replaceFirstChar { if (it.isLowerCase()) it.titlecase(LGD()) else it.toString() }) && key.isNotBlank()) {
							add(key.replaceFirstChar { if (it.isLowerCase()) it.titlecase(LGD()) else it.toString() })
						}
					}
				}
			}
		}
	}.asIOLiveData()

	/**
	 * @param flow What [Flow] to merge in updates from
	 * @param against Return a [List] of [String] to compare against
	 */
	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.applyFilterList(
		flow: Flow<HashMap<String, InclusionState>>,
		against: (ABookmarkedNovelUI) -> List<String>
	) = combine(flow) { novelResult, filters ->
		novelResult.transform { list ->
			successResult(
				if (filters.isNotEmpty()) {
					var result = list
					filters.forEach { (s, inclusionState) ->
						result = when (inclusionState) {
							INCLUDE ->
								result.filter { novelUI ->
									against(novelUI).any { g -> g.replaceFirstChar {
										if (it.isLowerCase()) it.titlecase(
											LGD()
										) else it.toString()
									} == s }
								}
							EXCLUDE ->
								result.filterNot { novelUI ->
									against(novelUI).any { g -> g.replaceFirstChar {
										if (it.isLowerCase()) it.titlecase(
											LGD()
										) else it.toString()
									} == s }
								}
						}
					}
					result
				} else {
					list
				}
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
			novelResult.transformToSuccess { list ->
				if (reversed)
					list.reversed()
				else list
			}
		}

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineSortType() =
		combine(novelSortTypeFlow) { novelResult, sortType ->
			novelResult.transformToSuccess { list ->
				when (sortType) {
					NovelSortType.BY_TITLE -> list.sortedBy { it.title }
					NovelSortType.BY_UNREAD_COUNT -> list.sortedBy { it.unread }
					NovelSortType.BY_ID -> list.sortedBy { it.id }
				}
			}
		}

	private fun Flow<HResult<List<ABookmarkedNovelUI>>>.combineUnreadStatus() =
		combine(unreadStatusFlow) { novelResult, sortType ->
			novelResult.transformToSuccess { list ->
				sortType?.let {
					when (sortType) {
						INCLUDE -> list.filter { it.unread > 0 }
						EXCLUDE -> list.filterNot { it.unread > 0 }
					}
				} ?: list
			}
		}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}


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

	override fun getSortType(): NovelSortType = novelSortTypeFlow.value

	override fun setSortType(novelSortType: NovelSortType) {
		novelSortTypeFlow.value = novelSortType
	}

	override fun isSortReversed(): Boolean = areNovelsReversedFlow.value

	override fun setIsSortReversed(reversed: Boolean) {
		areNovelsReversedFlow.value = reversed
	}

	override fun addGenreToFilter(genre: String, inclusionState: InclusionState) {
		val map = genreFilterFlow.value.copy()
		map[genre] = inclusionState
		genreFilterFlow.tryEmit(map)
	}

	override fun removeGenreFromFilter(genre: String) {
		val map = genreFilterFlow.value.copy()
		map.remove(genre)
		genreFilterFlow.tryEmit(map.copy())
	}

	override fun getFilterGenres(): HashMap<String, InclusionState> = genreFilterFlow.value

	override fun addAuthorToFilter(author: String, inclusionState: InclusionState) {
		val map = authorFilterFlow.value.copy()
		map[author] = inclusionState
		authorFilterFlow.tryEmit(map)
	}

	override fun removeAuthorFromFilter(author: String) {
		val map = authorFilterFlow.value.copy()
		map.remove(author)
		authorFilterFlow.tryEmit(map.copy())
	}

	override fun getFilterAuthors(): HashMap<String, InclusionState> = authorFilterFlow.value

	override fun addArtistToFilter(artist: String, inclusionState: InclusionState) {
		val map = artistFilterFlow.value.copy()
		map[artist] = inclusionState
		artistFilterFlow.tryEmit(map)
	}

	override fun removeArtistFromFilter(artist: String) {
		val map = artistFilterFlow.value.copy()
		map.remove(artist)
		artistFilterFlow.tryEmit(map.copy())
	}

	override fun getFilterArtists(): HashMap<String, InclusionState> = artistFilterFlow.value

	override fun addTagToFilter(tag: String, inclusionState: InclusionState) {
		val map = tagFilterFlow.value.copy()
		map[tag] = inclusionState
		tagFilterFlow.tryEmit(map)
	}

	override fun removeTagFromFilter(tag: String) {
		val map = tagFilterFlow.value.copy()
		map.remove(tag)
		tagFilterFlow.tryEmit(map.copy())
	}

	override fun getFilterTags(): HashMap<String, InclusionState> = tagFilterFlow.value

	override fun resetSortAndFilters() {
		genreFilterFlow.value = hashMapOf()
		tagFilterFlow.value = hashMapOf()
		authorFilterFlow.value = hashMapOf()
		artistFilterFlow.value = hashMapOf()

		novelSortTypeFlow.value = NovelSortType.BY_TITLE
		areNovelsReversedFlow.value = false
	}

	private inline fun <reified K, reified V> HashMap<K, V>.copy(): HashMap<K, V> {
		val map = HashMap<K, V>()
		onEach { (k, v) ->
			map[k] = v
		}
		return map
	}

	override fun setViewType(cardType: NovelCardType) {
		launchIO { setNovelUITypeUseCase(cardType) }
	}

	override fun setUnreadFilter(inclusionState: InclusionState?) {
		unreadStatusFlow.value = inclusionState
	}

	override fun getUnreadFilter(): InclusionState? = unreadStatusFlow.value
}