package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.enums.NavigationStyle
import app.shosetsu.android.viewmodel.base.IsOnlineCheckViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.common.domain.model.local.AppUpdateEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.AppThemes

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
 * 20 / 06 / 2020
 */
abstract class AMainViewModel : ShosetsuViewModel(), IsOnlineCheckViewModel {
	abstract fun startAppUpdateCheck(): LiveData<HResult<AppUpdateEntity>>

	/**
	 * If 0, Bottom
	 * If 1, Drawer
	 */
	abstract val navigationStyle: NavigationStyle

	abstract val appThemeLiveData: LiveData<AppThemes>

	abstract val requireDoubleBackToExit: Boolean

	/**
	 * The user requests to update the app
	 *
	 * If preview, will use in-app update for preview
	 * If stable-git, will use in-app update for stable
	 * If stable-goo, will open up google play store
	 * If stable-utd, will open up up-to-down
	 * If stable-fdr, will open up f-droid
	 */
	abstract fun handleAppUpdate(): LiveData<HResult<AppUpdateAction>>

	sealed class AppUpdateAction {

		/**
		 * Shosetsu is downloading the update itself
		 */
		object SelfUpdate : AppUpdateAction()

		/**
		 * The user has to handle the update
		 */
		data class UserUpdate(
			val updateTitle: String,
			val updateURL: String
		) : AppUpdateAction()

	}

	abstract val backupProgressState: LiveData<HResult<Unit>>

	/** If the application should show the show splash screen */
	abstract suspend fun showIntro(): Boolean

	/** Toggle the state if show intro or not*/
	abstract fun toggleShowIntro()
}