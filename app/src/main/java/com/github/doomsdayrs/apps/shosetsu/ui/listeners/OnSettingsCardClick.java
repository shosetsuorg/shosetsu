package com.github.doomsdayrs.apps.shosetsu.ui.listeners;
/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */

import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.main.settings.AdvancedSettings;
import com.github.doomsdayrs.apps.shosetsu.ui.main.settings.BackupSettings;
import com.github.doomsdayrs.apps.shosetsu.ui.main.settings.CreditsSettings;
import com.github.doomsdayrs.apps.shosetsu.ui.main.settings.DownloadSettings;
import com.github.doomsdayrs.apps.shosetsu.ui.main.settings.ViewSettings;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types;

public class OnSettingsCardClick implements View.OnClickListener {
    final Types type;
    final FragmentManager fragmentManager;

    public OnSettingsCardClick(Types id, FragmentManager fragmentManager) {
        type = id;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onClick(View v) {
        switch (type) {
            case VIEW: {
                //   Toast.makeText(v.getContext(), "View", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, new ViewSettings()).commit();
            }
            break;
            case CREDITS: {
                //  Toast.makeText(v.getContext(), "Credits", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, new CreditsSettings()).commit();

            }
            break;
            case ADVANCED: {
                //    Toast.makeText(v.getContext(), "Advanced", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, new AdvancedSettings()).commit();
            }
            break;
            case DOWNLOAD: {
                // Toast.makeText(v.getContext(), "Download", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, new DownloadSettings()).commit();
            }
            break;
            case BACKUP: {
                //    Toast.makeText(v.getContext(), "Backup", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, new BackupSettings()).commit();
            }
            break;
            default: {
            }
        }
    }
}