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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.common.ext.getString
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchAsync
import com.github.doomsdayrs.apps.shosetsu.common.ext.runOnMain
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.adapter.ExtensionsAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ExtensionUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.ExtensionsViewModel
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IExtensionsViewModel

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsController : RecyclerController<ExtensionsAdapter, ExtensionUI>() {
	init {
		setHasOptionsMenu(true)
	}

	val extensionViewModel: IExtensionsViewModel by viewModel()

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_extensions, menu)
	}

	override fun onViewCreated(view: View) {
		Utilities.setActivityTitle(activity, getString(R.string.extensions))
		createRecycler()
		establishObserver()
	}

	private fun createRecycler() {
		adapter = ExtensionsAdapter(this)
		launchAsync {
			recyclerArray.addAll(extensionViewModel.loadData())
			runOnMain {
				recyclerView?.post { adapter?.notifyDataSetChanged() }
			}
		}
	}

	private fun establishObserver() {
		extensionViewModel.subscribeObserver(this, Observer { list ->
			val dif = DiffUtil.calculateDiff(
					ExtensionsViewModel.ExtensionsDifCalc(recyclerArray, list)
			)
			recyclerArray.clear()
			recyclerArray.addAll(list)
			adapter?.let { dif.dispatchUpdatesTo(it) }
		})
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.refresh -> {
				extensionViewModel.refreshRepository()
				true
			}
			R.id.reload -> {
				extensionViewModel.reloadFormatters()
				true
			}
			else -> false
		}
	}
}