package com.github.doomsdayrs.apps.shosetsu.ui.settings.listener;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */

import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.settings.types.AdvancedSettings;
import com.github.doomsdayrs.apps.shosetsu.ui.settings.types.BackupSettings;
import com.github.doomsdayrs.apps.shosetsu.ui.settings.types.DownloadSettings;
import com.github.doomsdayrs.apps.shosetsu.ui.settings.types.InfoSettings;
import com.github.doomsdayrs.apps.shosetsu.ui.settings.types.ViewSettings;
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
            case INFO: {
                //  Toast.makeText(v.getContext(), "Info", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, new InfoSettings()).commit();
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