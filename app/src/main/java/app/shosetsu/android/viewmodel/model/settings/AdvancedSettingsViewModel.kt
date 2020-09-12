package app.shosetsu.android.viewmodel.model.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.loading
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.viewmodel.abstracted.settings.AAdvancedSettingsViewModel

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
class AdvancedSettingsViewModel : AAdvancedSettingsViewModel() {
	override val settings: List<SettingsItemData>
		get() = TODO("Not yet implemented")

	override val liveData: LiveData<HResult<List<SettingsItemData>>> by lazy {
		liveData<HResult<List<SettingsItemData>>> {
			emit(loading())
			emit(successResult(settings))
		}
	}
}