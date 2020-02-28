package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context


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
 * <p>
 *     TODO add text size options
 * </p>
 */
class AdvancedSettings : SettingsSubController() {
    var ready = false
    private class ThemeChange(val advancedSettings: AdvancedSettings) : OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position in 0..1 && advancedSettings.ready) {
                val delegate = (advancedSettings.activity!! as AppCompatActivity).delegate
                when (position) {
                    0 -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                    1 -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                }
                val theme = delegate.localNightMode
                parent?.setSelection(if (theme == AppCompatDelegate.MODE_NIGHT_YES || theme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM || theme == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY) 1 else 0)
            } else advancedSettings.ready = true
        }
    }

    override val settings by lazy {
        arrayListOf(
                SettingsItemData(SettingsItemData.SettingsType.SPINNER)
                        .setTitle(R.string.theme)
                        .setOnItemSelectedListener(ThemeChange(this)),
                SettingsItemData(SettingsItemData.SettingsType.BUTTON)
                        .setTitle(R.string.remove_novel_cache)
                        .setOnClickListenerButton { Database.DatabaseIdentification.purgeUnSavedNovels() }
        )
    }

    override fun onViewCreated(view: View) {
        settings[0].setArrayAdapter(ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources!!.getStringArray(R.array.application_themes)))
        val theme = (activity as AppCompatActivity).delegate.localNightMode
        settings[0].setSpinnerSelection(if (theme == AppCompatDelegate.MODE_NIGHT_YES || theme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM || theme == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY) 1 else 0)
        super.onViewCreated(view)
    }


}