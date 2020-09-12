package app.shosetsu.android.viewmodel.model.settings

import android.util.Log
import androidx.lifecycle.LiveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.domain.usecases.load.LoadAppUpdateUseCase
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.viewmodel.abstracted.settings.AInfoSettingsViewModel

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
class InfoSettingsViewModel(
		private val loadAppUpdateUseCase: LoadAppUpdateUseCase
) : AInfoSettingsViewModel() {
	override fun checkForAppUpdate() {
		Log.d(logID(), "Checking for update")
		launchIO {
			loadAppUpdateUseCase()
		}
	}

	override val settings: List<SettingsItemData>
		get() = TODO("Not yet implemented")

	override val liveData: LiveData<HResult<List<SettingsItemData>>>
		get() = TODO("Not yet implemented")

}