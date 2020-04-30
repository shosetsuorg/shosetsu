package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast.LENGTH_LONG
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class DownloadSettings : SettingsSubController() {
	override val settings by lazy {
		arrayListOf(
				SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.TEXT)
						.setTitle(R.string.download_directory)
						.setTextViewText(Utilities.shoDir)
						.setTextOnClickListener { performFileSearch() },
				SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.SPINNER)
						.setTitle(R.string.download_speed),
				SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.SWITCH)
						.setTitle(R.string.download_chapter_updates)
						.setIsChecked(Settings.isDownloadOnUpdateEnabled)
						.setOnCheckedListner(CompoundButton.OnCheckedChangeListener { _, p1 ->
							Log.d("Download on update", p1.toString())
							Settings.isDownloadOnUpdateEnabled = !Settings.isDownloadOnUpdateEnabled
						})
		)
	}

	override fun onViewCreated(view: View) {
		settings[1].setArrayAdapter(ArrayAdapter(
				context!!,
				android.R.layout.simple_spinner_item,
				arrayListOf("String")
		))
		super.onViewCreated(view)
	}

	private fun setDir(dir: String) {
		Settings.settings.edit().putString("dir", dir).apply()
		Utilities.shoDir = dir
		recyclerView?.post { adapter?.notifyDataSetChanged() }
	}

	private fun performFileSearch() {
		context?.toast("Please make sure this is on the main storage, " +
				"SD card storage is not functional yet", duration = LENGTH_LONG)
		val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
		i.addCategory(Intent.CATEGORY_DEFAULT)
		startActivityForResult(Intent.createChooser(i, "Choose directory"), 42)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				val path = data.data?.path
				Log.i("Selected Folder", "Uri: $path")
				if (path != null)
					setDir(path.substring(path.indexOf(":") + 1))
				else context?.toast("Path is null")
			}
		}
	}
}