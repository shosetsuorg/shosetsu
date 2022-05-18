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

import app.shosetsu.android.common.enums.InclusionState
import app.shosetsu.android.common.enums.InclusionState.EXCLUDE
import app.shosetsu.android.common.enums.InclusionState.INCLUDE
import app.shosetsu.android.common.enums.NovelCardType
import app.shosetsu.android.common.enums.NovelSortType
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.utils.copy
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.load.LoadLibraryUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsHUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsPUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUITypeUseCase
import app.shosetsu.android.domain.usecases.settings.SetNovelUITypeUseCase
import app.shosetsu.android.domain.usecases.start.StartUpdateWorkerUseCase
import app.shosetsu.android.domain.usecases.update.UpdateBookmarkedNovelUseCase
import app.shosetsu.android.view.uimodels.model.LibraryNovelUI
import app.shosetsu.android.viewmodel.abstracted.ALibraryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.Locale.getDefault as LGD

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModel(
	private val libraryAsCardsUseCase: LoadLibraryUseCase,
	private val updateBookmarkedNovelUseCase: UpdateBookmarkedNovelUseCase,
	private val isOnlineUseCase: IsOnlineUseCase,
	private var startUpdateWorkerUseCase: StartUpdateWorkerUseCase,
	private val loadNovelUITypeUseCase: LoadNovelUITypeUseCase,
	private val loadNovelUIColumnsH: LoadNovelUIColumnsHUseCase,
	private val loadNovelUIColumnsP: LoadNovelUIColumnsPUseCase,
	private val setNovelUITypeUseCase: SetNovelUITypeUseCase
) : ALibraryViewModel() {

	private val selectedNovels = MutableStateFlow<Map<Int, Boolean>>(mapOf())
	private suspend fun copySelected(): HashMap<Int, Boolean> =
		selectedNovels.first().copy()

	private suspend fun clearSelectedSuspend() {
		selectedNovels.emit(mapOf())
	}

	override fun selectAll() {
		launchIO {
			val list = liveData.first()
			val selection = copySelected()

			list.forEach {
				selection[it.id] = true
			}

			selectedNovels.emit(selection)
		}
	}

	override fun selectBetween() {
		launchIO {
			val list = liveData.first()
			val selection = copySelected()

			val firstSelected = list.indexOfFirst { it.isSelected }
			val lastSelected = list.indexOfLast { it.isSelected }

			if (listOf(firstSelected, lastSelected).any { it == -1 }) {
				logE("Received -1 index")
				return@launchIO
			}

			if (firstSelected == lastSelected) {
				logE("Ignoring select between, requires more then 1 selected item")
				return@launchIO
			}

			if (firstSelected + 1 == lastSelected) {
				logE("Ignoring select between, requires gap between items")
				return@launchIO
			}

			list.subList(firstSelected + 1, lastSelected).forEach {
				selection[it.id] = true
			}

			selectedNovels.emit(selection)
		}
	}

	override fun toggleSelection(item: LibraryNovelUI) {
		logI("Toggle: $item")
		launchIO {
			val selection = copySelected()

			selection[item.id] = !item.isSelected

			selectedNovels.emit(selection)
		}
	}

	override fun invertSelection() {
		launchIO {
			val list = liveData.first()
			val selection = copySelected()

			list.forEach {
				selection[it.id] = !it.isSelected
			}

			selectedNovels.emit(selection)
		}
	}

	private val librarySourceFlow: Flow<List<LibraryNovelUI>> by lazy { libraryAsCardsUseCase() }

	override val isEmptyFlow: Flow<Boolean> by lazy {
		librarySourceFlow.map {
			it.isEmpty()
		}
	}

	override val hasSelectionFlow: Flow<Boolean> by lazy {
		selectedNovels.mapLatest { map ->
			val b = map.values.any { it }
			hasSelection = b
			b
		}
	}

	override var hasSelection: Boolean = false

	override val genresFlow: Flow<List<String>> by lazy {
		stripOutList { it.genres }
	}

	override val tagsFlow: Flow<List<String>> by lazy {
		stripOutList { it.tags }
	}

	override val authorsFlow: Flow<List<String>> by lazy {
		stripOutList { it.authors }
	}

	override val artistsFlow: Flow<List<String>> by lazy {
		stripOutList { it.artists }
	}

	override val novelCardTypeFlow: Flow<NovelCardType> by lazy {
		loadNovelUITypeUseCase()
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
	override val liveData: Flow<List<LibraryNovelUI>> by lazy {
		librarySourceFlow
			.combineFilter()
			.combineSelection()
			.combineArtistFilter()
			.combineAuthorFilter()
			.combineGenreFilter()
			.combineTagsFilter()
			.combineUnreadStatus()
			.combineSortType()
			.combineSortReverse()
	}

	override val columnsInH by lazy {
		loadNovelUIColumnsH().onIO()
	}

	override val columnsInV by lazy {
		loadNovelUIColumnsP().onIO()
	}

	/**
	 * Removes the list for filtering from the [LibraryNovelUI] with the flow
	 */
	private fun stripOutList(
		strip: (LibraryNovelUI) -> List<String>
	): Flow<List<String>> = librarySourceFlow.mapLatest { result ->
		ArrayList<String>().apply {
			result.let { list ->
				list.forEach { ui ->
					strip(ui).forEach { key ->
						if (!contains(key.replaceFirstChar { if (it.isLowerCase()) it.titlecase(LGD()) else it.toString() }) && key.isNotBlank()) {
							add(key.replaceFirstChar { if (it.isLowerCase()) it.titlecase(LGD()) else it.toString() })
						}
					}
				}
			}
		}
	}.onIO()

	/**
	 * @param flow What [Flow] to merge in updates from
	 * @param against Return a [List] of [String] to compare against
	 */
	private fun Flow<List<LibraryNovelUI>>.applyFilterList(
		flow: Flow<HashMap<String, InclusionState>>,
		against: (LibraryNovelUI) -> List<String>
	) = combine(flow) { list, filters ->
		if (filters.isNotEmpty()) {
			var result = list
			filters.forEach { (s, inclusionState) ->
				result = when (inclusionState) {
					INCLUDE ->
						result.filter { novelUI ->
							against(novelUI).any { g ->
								g.replaceFirstChar {
									if (it.isLowerCase()) it.titlecase(
										LGD()
									) else it.toString()
								} == s
							}
						}
					EXCLUDE ->
						result.filterNot { novelUI ->
							against(novelUI).any { g ->
								g.replaceFirstChar {
									if (it.isLowerCase()) it.titlecase(
										LGD()
									) else it.toString()
								} == s
							}
						}
				}
			}
			result
		} else {
			list
		}
	}

	private fun Flow<List<LibraryNovelUI>>.combineGenreFilter() =
		applyFilterList(genreFilterFlow) { it.genres }

	private fun Flow<List<LibraryNovelUI>>.combineTagsFilter() =
		applyFilterList(tagFilterFlow) { it.tags }

	private fun Flow<List<LibraryNovelUI>>.combineAuthorFilter() =
		applyFilterList(authorFilterFlow) { it.authors }

	private fun Flow<List<LibraryNovelUI>>.combineArtistFilter() =
		applyFilterList(artistFilterFlow) { it.artists }


	private fun Flow<List<LibraryNovelUI>>.combineSortReverse() =
		combine(areNovelsReversedFlow) { novelResult, reversed ->
			novelResult.let { list ->
				if (reversed)
					list.reversed()
				else list
			}
		}

	private fun Flow<List<LibraryNovelUI>>.combineFilter() =
		combine(queryFlow) { list, query ->
			list.filter {
				it.title.contains(query)
			}
		}

	private fun Flow<List<LibraryNovelUI>>.combineSelection() =
		combine(selectedNovels) { list, query ->
			list.map {
				it.copy(
					isSelected = query.getOrElse(it.id) { false }
				)
			}
		}


	private fun Flow<List<LibraryNovelUI>>.combineSortType() =
		combine(novelSortTypeFlow) { novelResult, sortType ->
			novelResult.let { list ->
				when (sortType) {
					NovelSortType.BY_TITLE -> list.sortedBy { it.title }
					NovelSortType.BY_UNREAD_COUNT -> list.sortedBy { it.unread }
					NovelSortType.BY_ID -> list.sortedBy { it.id }
				}
			}
		}

	private fun Flow<List<LibraryNovelUI>>.combineUnreadStatus() =
		combine(unreadStatusFlow) { novelResult, sortType ->
			novelResult.let { list ->
				sortType?.let {
					when (sortType) {
						INCLUDE -> list.filter { it.unread > 0 }
						EXCLUDE -> list.filterNot { it.unread > 0 }
					}
				} ?: list
			}
		}

	override fun isOnline(): Boolean = isOnlineUseCase()

	override fun startUpdateManager() {
		startUpdateWorkerUseCase(true)
	}

	override fun removeSelectedFromLibrary() {
		launchIO {
			updateBookmarkedNovelUseCase(liveData.first().filter { it.isSelected }.onEach {
				it.bookmarked = false
			})
		}
	}

	override fun getSelectedIds(): Flow<IntArray> = flow {
		emit(selectedNovels.first().keys.toIntArray())
	}

	override fun deselectAll() {
		launchIO {
			clearSelectedSuspend()
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

	override fun setViewType(cardType: NovelCardType) {
		launchIO { setNovelUITypeUseCase(cardType) }
	}

	override fun setUnreadFilter(inclusionState: InclusionState?) {
		unreadStatusFlow.value = inclusionState
	}

	override fun getUnreadFilter(): InclusionState? = unreadStatusFlow.value


	override val queryFlow: MutableStateFlow<String> = MutableStateFlow("")

	override fun setQuery(s: String) {
		queryFlow.tryEmit(s)
	}
}