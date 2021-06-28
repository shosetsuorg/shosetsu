package app.shosetsu.android.ui.settings.sub

import androidx.appcompat.app.AlertDialog
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.launchUI
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.settings.SettingsSubController
import app.shosetsu.android.view.uimodels.settings.SwitchSettingData
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.onChecked
import app.shosetsu.android.viewmodel.abstracted.settings.AViewSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey.NavStyle
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import com.github.doomsdayrs.apps.shosetsu.R
import org.kodein.di.instance

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
 * Shosetsu
 * 13 / 07 / 2019
 */
class ViewSettings : SettingsSubController() {
	override val viewModel: AViewSettingsViewModel by viewModel()
	private val iSettingsRepository: ISettingsRepository by instance()
	override val viewTitleRes: Int = R.string.settings_view

	override val adjustments: List<SettingsItemData>.() -> Unit = {
		var state = false
		find<SwitchSettingData>(4)?.onChecked { cb, isChecked ->
			if (state) {
				state = false
				return@onChecked
			}
			AlertDialog.Builder(this@ViewSettings.activity!!).apply {
				this.setMessage(R.string.need_restart)
				setPositiveButton(R.string.restart) { d, _ ->
					launchIO {
						iSettingsRepository.setInt(NavStyle, if (isChecked) 1 else 0)
						launchUI {
							d.dismiss()
							this@ViewSettings.activity?.finish()
						}
					}
				}
				setNegativeButton(R.string.never_mind) { d, _ ->
					d.dismiss()
					state = true
					cb?.isChecked = !isChecked
				}
			}.show()
		}
	}
}