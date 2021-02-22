package app.shosetsu.android.viewmodel.abstracted.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.viewmodel.base.ErrorReportingViewModel
import app.shosetsu.android.viewmodel.base.ExposedSettingsRepoViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.loading
import app.shosetsu.common.dto.successResult
import kotlinx.coroutines.Dispatchers.IO

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
abstract class ASubSettingsViewModel(
	override val settingsRepo: ISettingsRepository
) : ShosetsuViewModel(), ErrorReportingViewModel, ExposedSettingsRepoViewModel {
	abstract suspend fun settings(): List<SettingsItemData>

	fun getSettings(): LiveData<HResult<List<SettingsItemData>>> =
		liveData(context = viewModelScope.coroutineContext + IO) {
			emit(loading())
			emit(successResult(settings()))
		}
}