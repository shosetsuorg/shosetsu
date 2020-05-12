package com.github.doomsdayrs.apps.shosetsu.viewmodel.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ISettingsDownloadViewModel

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
 */


/**
 * shosetsu
 * 12 / May / 2020
 */
class SettingsDownloadViewModel(
		val context: Context,
		val activity: Activity
) : ISettingsDownloadViewModel() {
	override val settings: ArrayList<SettingsItemData> by lazy {
		arrayListOf(
				SettingsItemData(SettingsItemData.SettingsType.TEXT, 1)
						.setTitle(R.string.download_directory)
						.setTextViewText(Settings.downloadDirectory)
						.setTextOnClickListener { performFileSearch() },
				SettingsItemData(SettingsItemData.SettingsType.SPINNER, 2)
						.setTitle(R.string.download_speed)
						.setArrayAdapter(ArrayAdapter(
								context,
								android.R.layout.simple_spinner_item,
								arrayListOf("String")
						)),
				SettingsItemData(SettingsItemData.SettingsType.SWITCH, 3)
						.setTitle(R.string.download_chapter_updates)
						.setIsChecked(Settings.isDownloadOnUpdateEnabled)
						.setOnCheckedListner(CompoundButton.OnCheckedChangeListener { _, p1 ->
							Log.d("Download on update", p1.toString())
							Settings.isDownloadOnUpdateEnabled = !Settings.isDownloadOnUpdateEnabled
						})
		)
	}


	private fun performFileSearch() {
		context.toast("Please make sure this is on the main storage, " +
				"SD card storage is not functional yet", duration = Toast.LENGTH_LONG)
		val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
		i.addCategory(Intent.CATEGORY_DEFAULT)
		activity.startActivityForResult(Intent.createChooser(i, "Choose directory"), 42)
	}

}