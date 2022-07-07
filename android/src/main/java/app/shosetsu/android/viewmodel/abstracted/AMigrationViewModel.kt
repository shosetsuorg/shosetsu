package app.shosetsu.android.viewmodel.abstracted

import app.shosetsu.android.domain.model.local.StrippedBookmarkedNovelEntity
import app.shosetsu.android.view.uimodels.model.MigrationExtensionUI
import app.shosetsu.android.view.uimodels.model.MigrationNovelUI
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
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
 * Shosetsu
 *
 * @since 04 / 08 / 2021
 * @author Doomsdayrs
 */
abstract class AMigrationViewModel : ShosetsuViewModel() {

	/**
	 * The query that is being used for the current novel to be searched for
	 */
	abstract val currentQuery: Flow<String>

	/**
	 * The extensions to select from
	 */
	abstract val extensions: Flow<List<MigrationExtensionUI>>

	/**
	 * Novels that will be transfered
	 */
	abstract val novels: Flow<List<MigrationNovelUI>>

	/**
	 * Which novel is currently being worked on
	 */
	abstract val which: Flow<Int>

	abstract val results: Flow<List<StrippedBookmarkedNovelEntity>>

	/**
	 * Set which novel is being worked on
	 */
	abstract fun setWorkingOn(novelId: Int)

	/**
	 * Set the novels to work with
	 */
	abstract fun setNovels(array: IntArray)

	/**
	 * Set which extension to use with the currently selected novel
	 */
	abstract fun setSelectedExtension(extensionUI: MigrationExtensionUI)

	/**
	 * Set query of the current novel
	 */
	abstract fun setQuery(newQuery: String)
}