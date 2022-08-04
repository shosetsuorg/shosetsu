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

import androidx.compose.ui.state.ToggleableState
import app.shosetsu.android.common.enums.InclusionState
import app.shosetsu.android.common.enums.InclusionState.EXCLUDE
import app.shosetsu.android.common.enums.InclusionState.INCLUDE
import app.shosetsu.android.common.enums.NovelCardType
import app.shosetsu.android.common.enums.NovelSortType
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
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
import kotlinx.coroutines.runBlocking
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

	private val selectedNovels = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
	private suspend fun copySelected(): HashMap<Int, Boolean> =
		selectedNovels.first().copy()

	private fun clearSelected() {
		selectedNovels.value = emptyMap()
	}

	override fun selectAll() {
		launchIO {
			val list = liveData.first()
			val selection = copySelected()

			list.forEach {
				selection[it.id] = true
			}

			selectedNovels.value = selection
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

			selectedNovels.value = selection
		}
	}

	override fun toggleSelection(item: LibraryNovelUI) {
		launchIO {
			val selection = copySelected()

			selection[item.id] = !item.isSelected

			selectedNovels.value = selection
		}
	}

	override fun invertSelection() {
		launchIO {
			val list = liveData.first()
			val selection = copySelected()

			list.forEach {
				selection[it.id] = !it.isSelected
			}

			selectedNovels.value = selection
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
	private val genreFilterFlow: MutableStateFlow<Map<String, InclusionState>> by lazy {
		MutableStateFlow(
			hashMapOf()
		)
	}
	private val authorFilterFlow: MutableStateFlow<Map<String, InclusionState>> by lazy {
		MutableStateFlow(
			hashMapOf()
		)
	}
	private val artistFilterFlow: MutableStateFlow<Map<String, InclusionState>> by lazy {
		MutableStateFlow(
			hashMapOf()
		)
	}
	private val tagFilterFlow: MutableStateFlow<Map<String, InclusionState>> by lazy {
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
			.combineSelection()
			.combineArtistFilter()
			.combineAuthorFilter()
			.combineGenreFilter()
			.combineTagsFilter()
			.combineUnreadStatus()
			.combineSortType()
			.combineSortReverse()
			.combineFilter().onIO()
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
		flow: Flow<Map<String, InclusionState>>,
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
				it.title.contains(query, ignoreCase = true)
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
			val selected = liveData.first().filter { it.isSelected }
			clearSelected()
			updateBookmarkedNovelUseCase(selected.map {
				it.copy(bookmarked = false)
			})
		}
	}

	override fun getSelectedIds(): Flow<IntArray> = flow {
		val ints = selectedNovels.first().keys.toIntArray()
		if (ints.isEmpty()) return@flow
		clearSelected()
		emit(ints)
	}

	override fun deselectAll() {
		launchIO {
			clearSelected()
		}
	}

	override fun getSortType(): Flow<NovelSortType> = novelSortTypeFlow

	override fun setSortType(novelSortType: NovelSortType) {
		novelSortTypeFlow.value = novelSortType
		areNovelsReversedFlow.value = false
	}

	override fun isSortReversed(): Flow<Boolean> = areNovelsReversedFlow

	override fun setIsSortReversed(reversed: Boolean) {
		areNovelsReversedFlow.value = reversed
	}

	override fun cycleFilterGenreState(genre: String, currentState: ToggleableState) {
		launchIO {
			val map = genreFilterFlow.value.copy()
			currentState.toInclusionState().cycle()?.let {
				map[genre] = it
			} ?: map.remove(genre)
			genreFilterFlow.value = map
		}
	}

	override fun getFilterGenreState(name: String): Flow<ToggleableState> = genreFilterFlow.map {
		it[name].toToggleableState()
	}

	override fun cycleFilterAuthorState(author: String, currentState: ToggleableState) {
		launchIO {
			val map = authorFilterFlow.value.copy()
			currentState.toInclusionState().cycle()?.let {
				map[author] = it
			} ?: map.remove(author)
			authorFilterFlow.value = map
		}
	}

	override fun getFilterAuthorState(name: String): Flow<ToggleableState> = authorFilterFlow.map {
		it[name].toToggleableState()
	}

	override fun cycleFilterArtistState(artist: String, currentState: ToggleableState) {
		launchIO {
			val map = artistFilterFlow.value.copy()
			currentState.toInclusionState().cycle()?.let {
				map[artist] = it
			} ?: map.remove(artist)
			artistFilterFlow.value = map
		}
	}

	override fun getFilterArtistState(name: String): Flow<ToggleableState> = artistFilterFlow.map {
		it[name].toToggleableState()
	}

	override fun cycleFilterTagState(tag: String, currentState: ToggleableState) {
		launchIO {
			val map = tagFilterFlow.value.copy()
			currentState.toInclusionState().cycle()?.let {
				map[tag] = it
			} ?: map.remove(tag)
			tagFilterFlow.value = map
		}
	}

	override fun getFilterTagState(name: String): Flow<ToggleableState> = tagFilterFlow.map {
		it[name].toToggleableState()
	}

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

	override fun cycleUnreadFilter(currentState: ToggleableState) {
		unreadStatusFlow.value = currentState.toInclusionState().cycle()
	}

	override fun getUnreadFilter(): Flow<ToggleableState> =
		unreadStatusFlow.map { it.toToggleableState() }

	fun ToggleableState.toInclusionState(): InclusionState? = when (this) {
		ToggleableState.On -> INCLUDE
		ToggleableState.Off -> null
		ToggleableState.Indeterminate -> EXCLUDE
	}

	fun InclusionState?.toToggleableState(): ToggleableState = when (this) {
		INCLUDE -> ToggleableState.On
		EXCLUDE -> ToggleableState.Indeterminate
		null -> ToggleableState.Off
	}

	fun InclusionState?.cycle(): InclusionState? = when (this) {
		INCLUDE -> EXCLUDE
		EXCLUDE -> null
		null -> INCLUDE
	}

	override val queryFlow: MutableStateFlow<String> = MutableStateFlow("")

	override fun setQuery(s: String) {
		queryFlow.value = s
	}
}