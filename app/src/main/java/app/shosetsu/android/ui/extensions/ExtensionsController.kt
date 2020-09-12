package app.shosetsu.android.ui.extensions

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

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.extensions.adapter.ExtensionsAdapter
import app.shosetsu.android.view.base.FastAdapterRecyclerController.BasicFastAdapterRecyclerController
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.viewmodel.abstracted.IExtensionsViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsController : BasicFastAdapterRecyclerController<ExtensionUI>() {
	override val viewTitleRes: Int = R.string.extensions

	init {
		setHasOptionsMenu(true)
	}

	/***/
	private val viewModel: IExtensionsViewModel by viewModel()

	override val fastAdapter: FastAdapter<ExtensionUI> by lazy {
		val adapter = ExtensionsAdapter(viewModel)
		adapter.addAdapter(0, itemAdapter)
		adapter
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_extensions, menu)
	}

	override fun onViewCreated(view: View) {
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		R.id.refresh -> {
			if (viewModel.isOnline())
				viewModel.refreshRepository()
			else toast(R.string.you_not_online)
			true
		}
		else -> false
	}
}