package app.shosetsu.android.ui.extensionsConfigure

import android.view.View
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.extensionsConfigure.adapters.ConfigExtAdapter
import app.shosetsu.android.view.base.FastAdapterRecyclerController.BasicFastAdapterRecyclerController
import app.shosetsu.android.view.uimodels.model.ExtensionConfigUI
import app.shosetsu.android.viewmodel.abstracted.IExtensionsConfigureViewModel
import com.mikepenz.fastadapter.FastAdapter

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
class ConfigureExtensions : BasicFastAdapterRecyclerController<ExtensionConfigUI>() {
	private val viewModel: IExtensionsConfigureViewModel by viewModel()
	override val viewTitleRes: Int = com.github.doomsdayrs.apps.shosetsu.R.string.configure_extensions

	override val fastAdapter: FastAdapter<ExtensionConfigUI> by lazy {
		val adapter = ConfigExtAdapter(viewModel)
		adapter.addAdapter(0, itemAdapter)
		adapter
	}

	override fun onViewCreated(view: View) {
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
	}

	override fun setupFastAdapter() {
	}
}