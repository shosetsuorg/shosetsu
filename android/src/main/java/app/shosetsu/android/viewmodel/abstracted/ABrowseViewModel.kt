package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.domain.model.local.BrowseExtensionEntity
import app.shosetsu.android.domain.model.local.ExtensionInstallOptionEntity
import app.shosetsu.android.viewmodel.base.IsOnlineCheckViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.android.viewmodel.base.SubscribeViewModel

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
abstract class ABrowseViewModel :
	ShosetsuViewModel(),
	SubscribeViewModel<List<BrowseExtensionEntity>>,
	IsOnlineCheckViewModel {

	data class FilteredLanguages(
		val languages: List<String>,
		val states: Map<String, Boolean>
	)

	/** Refreshes the repositories and data values */
	abstract fun refresh()

	/** Installs an extension */
	abstract fun installExtension(
		extension: BrowseExtensionEntity,
		option: ExtensionInstallOptionEntity
	)

	/** Update an extension, only works if it is already installed */
	abstract fun updateExtension(ext: BrowseExtensionEntity)

	/** Cancel an extension install */
	abstract fun cancelInstall(ext: BrowseExtensionEntity)


	/**
	 * Languages that are present, this is used for filtering
	 *
	 * First value is a list of possible strings
	 * Second value is if it is filtered or not
	 */
	abstract val filteredLanguagesLive: LiveData<FilteredLanguages>

	/**
	 * Set if a language is filtered or not
	 */
	abstract fun setLanguageFiltered(language: String, state: Boolean)


	abstract val onlyInstalledLive: LiveData<Boolean>

	/**
	 * Set if to only show installed or not
	 */
	abstract fun showOnlyInstalled(state: Boolean)


	abstract val searchTermLive: LiveData<String>

	/**
	 * Filter the extension list to only display extensions matching [name]
	 */
	abstract fun setSearch(name: String)

	/**
	 * Reset the term set by [setSearch]
	 */
	abstract fun resetSearch()
}