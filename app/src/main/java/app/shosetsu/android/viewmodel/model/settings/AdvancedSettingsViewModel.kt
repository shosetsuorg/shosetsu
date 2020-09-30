package app.shosetsu.android.viewmodel.model.settings

import android.content.Context
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import app.shosetsu.android.common.ext.toast
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AAdvancedSettingsViewModel
import com.github.doomsdayrs.apps.shosetsu.R

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
 * 31 / 08 / 2020
 */
class AdvancedSettingsViewModel(
		iSettingsRepository: ISettingsRepository,
		private val context: Context
) : AAdvancedSettingsViewModel(iSettingsRepository) {
	override suspend fun settings(): List<SettingsItemData> = listOf(
			spinnerSettingData(1) {
				title { R.string.theme }
				arrayAdapter = ArrayAdapter(
						context,
						android.R.layout.simple_spinner_dropdown_item,
						context.resources.getStringArray(R.array.application_themes)
				)
				onSpinnerItemSelected { adapterView, _, position, _ ->
					if (position in 0..1) {
						val delegate = (context as AppCompatActivity).delegate
						when (position) {
							0 -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
							1 -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
						}
						val theme = delegate.localNightMode
						adapterView?.setSelection(if (
								theme == AppCompatDelegate.MODE_NIGHT_YES ||
								theme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM ||
								theme == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
						) 1 else 0)
					}
				}
			},
			buttonSettingData(2) {
				title { com.github.doomsdayrs.apps.shosetsu.R.string.remove_novel_cache }
				onButtonClicked {
					try {
						// TODO purge
					} catch (e: android.database.SQLException) {
						context.toast("SQLITE Error")
						android.util.Log.e("AdvancedSettings", "DatabaseError", e)
					}
				}
			}
	)
}