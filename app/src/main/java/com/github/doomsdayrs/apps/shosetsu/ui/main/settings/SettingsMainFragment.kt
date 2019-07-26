package com.github.doomsdayrs.apps.shosetsu.ui.main.settings

import androidx.preference.PreferenceScreen

import com.github.doomsdayrs.apps.shosetsu.R
import org.kodein.di.Kodein


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
 * 9 / June / 2019
 *
 * @author github.com/hXtreme
 */

/**
 * Constructor
 * TODO: Create custom option menu for settings to search specific ones
 */
class SettingsMainFragment(kodein: Kodein) : SettingsFragment(kodein) {

    override fun setupPreferenceScreen(screen: PreferenceScreen) : Unit = with (screen) {
        titleRes = R.string.settings
        preference{
            titleRes = R.string.setting_view
            onClick {
                fragmentManager!!.beginTransaction().addToBackStack("tag")
                        .replace(R.id.fragment_container, ViewSettings())
                        .commit()
            }
        }
        preference {
            titleRes = R.string.setting_download
            onClick {
                fragmentManager!!.beginTransaction().addToBackStack("tag")
                        .replace(R.id.fragment_container, DownloadSettings())
                        .commit()
            }
        }
        preference {
            titleRes = R.string.setting_advanced
            onClick {
                fragmentManager!!.beginTransaction().addToBackStack("tag")
                        .replace(R.id.fragment_container, AdvancedSettings())
                        .commit()
            }
        }
        preference {
            titleRes = R.string.settings_backup
            onClick {
                fragmentManager!!.beginTransaction().addToBackStack("tag")
                        .replace(R.id.fragment_container, BackupSettings())
                        .commit()
            }
        }
        preference {
            titleRes = R.string.setting_info
            onClick {
                fragmentManager!!.beginTransaction().addToBackStack("tag")
                        .replace(R.id.fragment_container, InfoSettings())
                        .commit()
            }
        }
    }
}


