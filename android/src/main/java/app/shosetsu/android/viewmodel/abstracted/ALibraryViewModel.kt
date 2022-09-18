package app.shosetsu.android.viewmodel.abstracted

import androidx.compose.ui.state.ToggleableState
import app.shosetsu.android.common.enums.NovelCardType
import app.shosetsu.android.common.enums.NovelSortType
import app.shosetsu.android.view.uimodels.model.LibraryNovelUI
import app.shosetsu.android.view.uimodels.model.LibraryUI
import app.shosetsu.android.viewmodel.base.IsOnlineCheckViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.android.viewmodel.base.StartUpdateManagerViewModel
import app.shosetsu.android.viewmodel.base.SubscribeViewModel
import kotlinx.coroutines.flow.Flow

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


/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
abstract class ALibraryViewModel :
	SubscribeViewModel<LibraryUI>,
	ShosetsuViewModel(),
	IsOnlineCheckViewModel,
	StartUpdateManagerViewModel {

	abstract val isEmptyFlow: Flow<Boolean>
	abstract val hasSelectionFlow: Flow<Boolean>
	abstract val hasSelection: Boolean

	/** All genres from all [LibraryNovelUI] combined*/
	abstract val genresFlow: Flow<List<String>>

	/** All tags from all [LibraryNovelUI] combined*/
	abstract val tagsFlow: Flow<List<String>>

	/** All authors from all [LibraryNovelUI] combined*/
	abstract val authorsFlow: Flow<List<String>>

	/** All artists from all [LibraryNovelUI] combined*/
	abstract val artistsFlow: Flow<List<String>>

	abstract val novelCardTypeFlow: Flow<NovelCardType>

	abstract val columnsInH: Flow<Int>
	abstract val columnsInV: Flow<Int>
	abstract val badgeUnreadToastFlow: Flow<Boolean>

	abstract fun cycleUnreadFilter(currentState: ToggleableState)
	abstract fun getUnreadFilter(): Flow<ToggleableState>

	abstract fun getSortType(): Flow<NovelSortType>
	abstract fun setSortType(novelSortType: NovelSortType)

	abstract fun isSortReversed(): Flow<Boolean>
	abstract fun setIsSortReversed(reversed: Boolean)

	abstract fun cycleFilterGenreState(genre: String, currentState: ToggleableState)
	abstract fun getFilterGenreState(name: String): Flow<ToggleableState>

	abstract fun cycleFilterAuthorState(author: String, currentState: ToggleableState)
	abstract fun getFilterAuthorState(name: String): Flow<ToggleableState>

	abstract fun cycleFilterArtistState(artist: String, currentState: ToggleableState)
	abstract fun getFilterArtistState(name: String): Flow<ToggleableState>

	abstract fun cycleFilterTagState(tag: String, currentState: ToggleableState)
	abstract fun getFilterTagState(name: String): Flow<ToggleableState>

	abstract fun resetSortAndFilters()
	abstract fun setViewType(cardType: NovelCardType)

	abstract fun setCategories(categories: IntArray)

	abstract fun removeSelectedFromLibrary()

	abstract fun getSelectedIds(): Flow<IntArray>
	abstract fun deselectAll()
	abstract fun selectAll()
	abstract fun invertSelection()
	abstract fun selectBetween()
	abstract fun toggleSelection(item: LibraryNovelUI)

	abstract val queryFlow: Flow<String>
	abstract fun setQuery(s: String)

	abstract val activeCategory: Flow<Int>
	abstract fun setActiveCategory(category: Int)

}