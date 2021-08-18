package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.view.uimodels.model.MigrationNovelUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.common.domain.model.local.StrippedBookmarkedNovelEntity
import app.shosetsu.common.dto.HResult

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
	 * The extensions to select from
	 */
	abstract val extensions: LiveData<HResult<List<ExtensionUI>>>

	/**
	 * Novels that will be transfered
	 */
	abstract val novels: LiveData<HResult<List<MigrationNovelUI>>>

	/**
	 * Which novel is currently being worked on
	 */
	abstract val which: LiveData<Int>

	/**
	 * Set which novel is being worked on
	 */
	abstract fun setWorkingOn(novelId: Int)

	/**
	 * Provides the results found for a novel
	 */
	abstract fun getResults(novelUI: NovelUI): LiveData<HResult<StrippedBookmarkedNovelEntity>>

	/**
	 * Set the novels to work with
	 */
	abstract fun setNovels(array: IntArray)

	/**
	 * Set which extension to use with the currently selected novel
	 */
	abstract fun setSelectedExtension(extensionUI: ExtensionUI)
}