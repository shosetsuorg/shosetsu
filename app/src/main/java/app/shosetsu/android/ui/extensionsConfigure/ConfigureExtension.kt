package app.shosetsu.android.ui.extensionsConfigure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_FORMATTER
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.base.FastAdapterRecyclerController
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.viewmodel.abstracted.IExtensionSingleConfigureViewModel
import com.github.doomsdayrs.apps.shosetsu.databinding.ConfigureExtensionViewBinding

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
class ConfigureExtension(bundle: Bundle) : FastAdapterRecyclerController<ConfigureExtensionViewBinding, SettingsItemData>(bundle) {
	val viewModel: IExtensionSingleConfigureViewModel by viewModel()

	override fun onViewCreated(view: View) {
		viewModel.setExtensionID(args.getInt(BUNDLE_FORMATTER))
	}

	private fun observe() {
		viewModel.liveData.observe(this) {
			it.settingsModel
		}
	}

	override fun bindView(inflater: LayoutInflater): ConfigureExtensionViewBinding =
			ConfigureExtensionViewBinding.inflate(inflater)
}