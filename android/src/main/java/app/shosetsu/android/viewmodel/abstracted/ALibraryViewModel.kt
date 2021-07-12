package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.utils.ColumnCalculator
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.viewmodel.base.*
import app.shosetsu.common.enums.InclusionState
import app.shosetsu.common.enums.NovelCardType
import app.shosetsu.common.enums.NovelSortType

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
	SubscribeHandleViewModel<List<ABookmarkedNovelUI>>,
	ShosetsuViewModel(),
	IsOnlineCheckViewModel,
	StartUpdateManagerViewModel,
	ErrorReportingViewModel,
	ColumnCalculator {

	/** All genres from all [ABookmarkedNovelUI] combined*/
	abstract val genresLiveData: LiveData<List<String>>

	/** All tags from all [ABookmarkedNovelUI] combined*/
	abstract val tagsLiveData: LiveData<List<String>>

	/** All authors from all [ABookmarkedNovelUI] combined*/
	abstract val authorsLiveData: LiveData<List<String>>

	/** All artists from all [ABookmarkedNovelUI] combined*/
	abstract val artistsLiveData: LiveData<List<String>>

	abstract val novelCardTypeLiveData: LiveData<NovelCardType>

	/**
	 * Remove the following from the library
	 */
	abstract fun removeFromLibrary(list: List<ABookmarkedNovelUI>)

	abstract fun setUnreadFilter(inclusionState: InclusionState?)
	abstract fun getUnreadFilter(): InclusionState?

	abstract fun getSortType(): NovelSortType
	abstract fun setSortType(novelSortType: NovelSortType)

	abstract fun isSortReversed(): Boolean
	abstract fun setIsSortReversed(reversed: Boolean)

	abstract fun addGenreToFilter(genre: String, inclusionState: InclusionState)
	abstract fun removeGenreFromFilter(genre: String)
	abstract fun getFilterGenres(): HashMap<String, InclusionState>

	abstract fun addAuthorToFilter(author: String, inclusionState: InclusionState)
	abstract fun removeAuthorFromFilter(author: String)
	abstract fun getFilterAuthors(): HashMap<String, InclusionState>

	abstract fun addArtistToFilter(artist: String, inclusionState: InclusionState)
	abstract fun removeArtistFromFilter(artist: String)
	abstract fun getFilterArtists(): HashMap<String, InclusionState>

	abstract fun addTagToFilter(tag: String, inclusionState: InclusionState)
	abstract fun removeTagFromFilter(tag: String)
	abstract fun getFilterTags(): HashMap<String, InclusionState>

	abstract fun resetSortAndFilters()
	abstract fun setViewType(cardType: NovelCardType)

}