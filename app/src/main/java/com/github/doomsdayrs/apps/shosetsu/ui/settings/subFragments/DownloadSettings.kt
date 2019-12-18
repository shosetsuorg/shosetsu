package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import kotlinx.android.synthetic.main.settings_download.*
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
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */   class DownloadSettings : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "DownloadSettings")
        return inflater.inflate(R.layout.settings_download, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settings_download_dir.text = Utilities.shoDir
        settings_download_dir.setOnClickListener { performFileSearch() }

    }

    private fun setDir(dir: String) {
        Utilities.downloadPreferences.edit().putString("dir", dir).apply()
        Utilities.shoDir = dir
        settings_download_dir!!.text = dir
    }

    private fun performFileSearch() {
        Toast.makeText(context, "Please make sure this is on the main storage, SD card storage is not functional yet", Toast.LENGTH_LONG).show()
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
                if (path != null) setDir(path.substring(Objects.requireNonNull(path).indexOf(":") + 1)) else Toast.makeText(context, "Path is null", Toast.LENGTH_SHORT).show()
            }
        }
    }
}