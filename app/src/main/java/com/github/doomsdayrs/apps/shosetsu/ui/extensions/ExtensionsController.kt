package com.github.doomsdayrs.apps.shosetsu.ui.extensions

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
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.adapter.ExtensionsAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.FastAdapterRecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ExtensionUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.IExtensionsViewModel
import com.mikepenz.fastadapter.FastAdapter

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsController : FastAdapterRecyclerController<ExtensionUI>() {
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