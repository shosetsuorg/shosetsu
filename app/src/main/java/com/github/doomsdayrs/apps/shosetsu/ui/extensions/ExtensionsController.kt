package com.github.doomsdayrs.apps.shosetsu.ui.extensions

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.extensionsDao
import com.github.doomsdayrs.apps.shosetsu.providers.database.entities.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.adapter.ExtensionsAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getString
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Formatters
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsController : RecyclerController<ExtensionsAdapter, ExtensionEntity>() {
	init {
		setHasOptionsMenu(true)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_extensions, menu)
	}

	override fun onViewCreated(view: View) {
		Utilities.setActivityTitle(activity, getString(R.string.extensions))
		adapter = ExtensionsAdapter(this)
		GlobalScope.launch {
			recyclerArray = extensionsDao.loadFormatters().toArrayList()
			adapter?.notifyDataSetChanged()
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.refresh -> {
				// TODO Refresh the json
				true
			}
			R.id.reload -> {
				Formatters.formatters.clear()
				// TODO Load formatters once again
				true
			}
			else -> false
		}
	}
}