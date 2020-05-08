package com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure

import android.view.View
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.setActivityTitle
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.adapters.ConfigExtAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ExtensionUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IExtensionsConfigureViewModel

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
 * @author github.com/doomsdayrs
 */
class ConfigureExtensions : RecyclerController<ConfigExtAdapter, ExtensionUI>() {
	val viewModel: IExtensionsConfigureViewModel by viewModel()

	override fun onViewCreated(view: View) {
		activity?.setActivityTitle(R.string.configure_extensions)
		adapter = ConfigExtAdapter(this)
		viewModel.liveData.observe(this, Observer { handleRecyclerUpdate(it) })
	}

	override fun difAreItemsTheSame(oldItem: ExtensionUI, newItem: ExtensionUI): Boolean =
			oldItem.id == newItem.id
}