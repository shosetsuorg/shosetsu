package com.github.doomsdayrs.apps.shosetsu.ui.settings.listener

import android.view.View
import androidx.fragment.app.FragmentManager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.AdvancedSettings
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.DownloadSettings
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.InfoSettings
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.ViewSettings
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.BackupSettings
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types.*

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
class OnSettingsCardClick(private val type: Types, private val fragmentManager: FragmentManager) : View.OnClickListener {
    override fun onClick(v: View) {
        when (type) {
            VIEW -> {
                //   Toast.makeText(v.getContext(), "View", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, ViewSettings()).commit()
            }
            INFO -> {
                //  Toast.makeText(v.getContext(), "Info", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, InfoSettings()).commit()
            }
            ADVANCED -> {
                //    Toast.makeText(v.getContext(), "Advanced", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, AdvancedSettings()).commit()
            }
            DOWNLOAD -> {
                // Toast.makeText(v.getContext(), "Download", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, DownloadSettings()).commit()
            }
            BACKUP -> {
                //    Toast.makeText(v.getContext(), "Backup", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, BackupSettings()).commit()
            }
        }
    }

}