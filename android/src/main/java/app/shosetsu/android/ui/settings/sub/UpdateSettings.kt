package app.shosetsu.android.ui.settings.sub

import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.settings.SettingsSubController
import app.shosetsu.android.viewmodel.abstracted.settings.AUpdateSettingsViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.snackbar.Snackbar

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
class UpdateSettings : SettingsSubController() {
	override val viewTitleRes: Int = com.github.doomsdayrs.apps.shosetsu.R.string.settings_update
	override val viewModel: AUpdateSettingsViewModel by viewModel()

	override fun onDestroy() {
		launchUI {
			val doNovelUpdateSnackBar: (() -> Unit)
			var doRepoUpdateSnackBar: (() -> Unit)? = null

			doNovelUpdateSnackBar = {
				makeSnackBar(
					getString(
						R.string.controller_settings_restart_worker,
						getString(R.string.settings_update_worker_id_novel_updaters)
					),
					Snackbar.LENGTH_LONG
				)?.setAction(R.string.restart) {
					viewModel.restartNovelUpdater()
				}?.setOnDismissed { snackbar, event ->
					viewModel.novelUpdateSettingsChanged = false
					if (viewModel.repoUpdateSettingsChanged)
						doRepoUpdateSnackBar?.invoke()
				}?.show()
			}

			doRepoUpdateSnackBar = {
				makeSnackBar(
					getString(
						R.string.controller_settings_restart_worker,
						getString(R.string.worker_title_update_repository)
					),
					Snackbar.LENGTH_LONG
				)?.setAction(R.string.restart) {
					viewModel.restartRepoUpdater()
				}?.setOnDismissed { snackbar, event ->
					viewModel.repoUpdateSettingsChanged = false
					if (viewModel.novelUpdateSettingsChanged)
						doNovelUpdateSnackBar.invoke()
				}?.show()
			}

			if (viewModel.novelUpdateSettingsChanged)
				doNovelUpdateSnackBar()
			else if (viewModel.repoUpdateSettingsChanged)
				doRepoUpdateSnackBar()
		}
		super.onDestroy()
	}
}