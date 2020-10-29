package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.viewmodel.base.ErrorReportingViewModel
import app.shosetsu.android.viewmodel.base.SubscribeHandleViewModel

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
 * 16 / 07 / 2020
 *
 * This file is mainly to configure settings of a formatter
 *
 * [liveData] is of the formatter object itself
 */
abstract class IExtensionConfigureViewModel
	: ViewModel(), SubscribeHandleViewModel<ExtensionUI>, ErrorReportingViewModel {
	abstract val extensionSettings: LiveData<HResult<List<SettingsItemData>>>

	/** Set the extension ID to use */
	abstract fun setExtensionID(id: Int)

	/** Save the setting of this specific formatter */
	abstract suspend fun saveSetting(id: Int, value: Any)
	abstract fun uninstall(extensionUI: ExtensionUI)

	abstract fun destroy()
}