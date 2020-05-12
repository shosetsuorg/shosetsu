package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup

import android.app.Activity
import android.content.Intent
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.async.RestoreProcess
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ISettingsBackupViewModel
import com.vincent.filepicker.Constant
import com.vincent.filepicker.Constant.REQUEST_CODE_PICK_FILE
import com.vincent.filepicker.filter.entity.NormalFile
import java.util.*

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
 * Shosetsu
 * 13 / 07 / 2019
 */
class BackupSettings : SettingsSubController() {
	val viewModel: ISettingsBackupViewModel by viewModel()
	override val settings by lazy { viewModel.settings }

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (REQUEST_CODE_PICK_FILE == requestCode && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				val list: ArrayList<NormalFile>? =
						data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE)
				if (list != null && list.size > 0) {
					val normalFile = list[0]
					RestoreProcess(normalFile.path, context!!).execute()
				}
				/*
				String path = data.getData().getPath();
				Log.i("SelectedPath", path);

				int i = path.lastIndexOf(".");
				if (i > -1) {
					String fileEnding = path.substring(i + 1);
					if (fileEnding.equalsIgnoreCase("shoback")) {
						Log.i("Selected Folder", "Uri: " + path);
						//TODO Fix this shit, need's a proper integrated file manager
						new RestoreProcess("/Shosetsu/backup/backup-Mon Oct
						28 20:46:16 EDT 2019.shoback", getContext()).execute();
					} else
						Toast.makeText(getContext(), "Invalid file to use!",
						 Toast.LENGTH_LONG).show();
				}*/
			}
		}
	}
}