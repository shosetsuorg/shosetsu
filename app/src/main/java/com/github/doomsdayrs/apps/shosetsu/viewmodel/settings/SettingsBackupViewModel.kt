package com.github.doomsdayrs.apps.shosetsu.viewmodel.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.widget.CompoundButton
import android.widget.Toast
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.async.BackupProcess
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType.BUTTON
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType.CHECKBOX
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ISettingsBackupViewModel
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.NormalFilePickActivity

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
class SettingsBackupViewModel(
		val activity: Activity,
		val context: Context,
		val resources: Resources = context.resources
) : ISettingsBackupViewModel() {
	override val settings: ArrayList<SettingsItemData> by lazy {
		arrayListOf(
				SettingsItemData(CHECKBOX, 0)
						.setTitle(R.string.backup_chapters_option)
						.setDescription(R.string.backup_chapters_option_description)
						.setIsChecked(Settings.backupChapters)
						.setOnCheckedListner(CompoundButton.OnCheckedChangeListener { _, isChecked ->
							Settings.backupChapters = isChecked
						}),
				SettingsItemData(CHECKBOX, 1)
						.setTitle((R.string.backup_settings_option))
						.setDescription(R.string.backup_settings_option_desc)
						.setIsChecked(Settings.backupSettings)
						.setOnCheckedListner(CompoundButton.OnCheckedChangeListener { _, isChecked ->
							Settings.backupSettings = isChecked
						}),
				SettingsItemData(CHECKBOX, 2)
						.setTitle(R.string.backup_quick_option)
						.setDescription(R.string.backup_quick_option_desc)
						.setIsChecked(Settings.backupQuick)
						.setOnCheckedListner(CompoundButton.OnCheckedChangeListener { _, isChecked ->
							Settings.backupQuick = isChecked
						}),
				SettingsItemData(BUTTON, 3)
						.setOnClickListenerButton { it.post { BackupProcess().execute() } }
						.setTitle(R.string.backup_now)
						.setTextViewText(R.string.restore_now),
				SettingsItemData(BUTTON, 4)
						.setOnClickListenerButton {
							it.post { performFileSelection() }
						}
						.setTitle(R.string.restore_now)
						.setTextViewText(R.string.restore_now)
		)
	}

	private fun performFileSelection() {
		context.toast("Please make sure this is on the main storage, " +
				"SD card storage is not functional yet", duration = Toast.LENGTH_LONG)
		val intent = Intent(context, NormalFilePickActivity::class.java)
		intent.putExtra(Constant.MAX_NUMBER, 9)
		intent.putExtra(NormalFilePickActivity.SUFFIX, arrayOf("shoback", "json"))
		activity.startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE)
	}

}