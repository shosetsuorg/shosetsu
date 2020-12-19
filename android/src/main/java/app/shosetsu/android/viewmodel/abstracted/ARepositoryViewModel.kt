package app.shosetsu.android.viewmodel.abstracted

import app.shosetsu.android.view.uimodels.model.RepositoryUI
import app.shosetsu.android.viewmodel.base.ErrorReportingViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.android.viewmodel.base.SubscribeHandleViewModel

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 16 / 09 / 2020
 */
abstract class ARepositoryViewModel
	: SubscribeHandleViewModel<List<RepositoryUI>>, ShosetsuViewModel(), ErrorReportingViewModel {
	/**
	 * Adds a URL via a string the user provides
	 *
	 * @param url THe URL of the repository
	 */
	abstract fun addRepository(url: String)

	/**
	 * Checks if the string provided is a valid URL
	 */
	abstract fun isURL(string: String): Boolean

	/**
	 * Remove the repo from the app
	 */
	abstract fun remove(repositoryInfoUI: RepositoryUI)
}