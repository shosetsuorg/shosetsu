package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter.SettingItemsAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.async.BackupProcess
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.async.RestoreProcess
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.NormalFilePickActivity
import com.vincent.filepicker.filter.entity.NormalFile
import kotlinx.android.synthetic.main.settings_backup.*
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
 * ====================================================================
 */

/**
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class BackupSettings : Fragment() {
    val settings: ArrayList<SettingsItem.SettingsItemData> = arrayListOf(
            SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.BUTTON)
                    .setOnClickListenerButton { view?.post { BackupProcess().execute() } }
                    .setTitle(R.string.backup_now)
                    .setTextViewText(R.string.restore_now),
            SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.BUTTON)
                    .setOnClickListenerButton { view?.post { performFileSelection() } }
                    .setTitle(R.string.restore_now)
                    .setTextViewText(R.string.restore_now)
    )

    val adapter: SettingItemsAdapter = SettingItemsAdapter(settings)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "BackupSettings")
        return inflater.inflate(R.layout.settings_backup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SettingItemsAdapter(settings)
    }

    private fun performFileSelection() {
        Toast.makeText(context, "Please make sure this is on the main storage, SD card storage is not functional yet", Toast.LENGTH_LONG).show()
        val intent = Intent(context, NormalFilePickActivity::class.java)
        intent.putExtra(Constant.MAX_NUMBER, 9)
        intent.putExtra(NormalFilePickActivity.SUFFIX, arrayOf("shoback", "json"))
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Constant.REQUEST_CODE_PICK_FILE == requestCode && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val list: ArrayList<NormalFile>? = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE)
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
                        new RestoreProcess("/Shosetsu/backup/backup-Mon Oct 28 20:46:16 EDT 2019.shoback", getContext()).execute();
                    } else
                        Toast.makeText(getContext(), "Invalid file to use!", Toast.LENGTH_LONG).show();
                }*/
            }
        }
    }
}