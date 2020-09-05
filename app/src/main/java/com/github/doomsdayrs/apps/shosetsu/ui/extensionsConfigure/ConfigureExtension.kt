package com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_FORMATTER
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.view.base.FastAdapterRecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.IExtensionSingleConfigureViewModel

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
 * ====================================================================
 */

/**
 * shosetsu
 * 21 / 01 / 2020
 *
 * Opens up detailed view of an extension, allows modifications
 */
class ConfigureExtension(
		private val bundle: Bundle,
) : FastAdapterRecyclerController<SettingsItemData>(bundle) {
	val viewModel: IExtensionSingleConfigureViewModel by viewModel()

	override val layoutRes: Int = R.layout.configure_extension_view
	override val resourceID: Int = R.id.settings

	override fun onViewCreated(view: View) {
		viewModel.setExtensionID(bundle.getInt(BUNDLE_FORMATTER))
	}

	private fun observe() {
		viewModel.liveData.observe(this) {
			it.settingsModel
		}
	}
}