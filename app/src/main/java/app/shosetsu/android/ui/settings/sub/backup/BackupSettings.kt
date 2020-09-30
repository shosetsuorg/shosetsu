package app.shosetsu.android.ui.settings.sub.backup

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import app.shosetsu.android.common.ShosetsuSettings
import app.shosetsu.android.common.ext.context
import app.shosetsu.android.common.ext.toast
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.settings.SettingsSubController
import app.shosetsu.android.view.uimodels.settings.ButtonSettingData
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.ASubSettingsViewModel
import app.shosetsu.android.viewmodel.model.settings.BackupSettingsViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.vincent.filepicker.Constant
import com.vincent.filepicker.Constant.REQUEST_CODE_PICK_FILE
import com.vincent.filepicker.activity.NormalFilePickActivity
import com.vincent.filepicker.filter.entity.NormalFile
import org.kodein.di.generic.instance
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
	override val viewTitleRes: Int = R.string.settings_backup

	override val viewModel: BackupSettingsViewModel by viewModel()

	override val adjustments: List<SettingsItemData>.() -> Unit = {
		find<ButtonSettingData>(4).onButtonClicked {
			performFileSelection()
		}
	}

	private fun performFileSelection() {
		context?.toast("Please make sure this is on the main storage, " +
				"SD card storage is not functional yet", duration = Toast.LENGTH_LONG)
		val intent = Intent(context, NormalFilePickActivity::class.java)
		intent.putExtra(Constant.MAX_NUMBER, 9)
		intent.putExtra(NormalFilePickActivity.SUFFIX, arrayOf("shoback", "json"))
		activity?.startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (REQUEST_CODE_PICK_FILE == requestCode && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				val list: ArrayList<NormalFile>? =
						data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE)
				if (list != null && list.size > 0) {
					val normalFile = list[0]
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