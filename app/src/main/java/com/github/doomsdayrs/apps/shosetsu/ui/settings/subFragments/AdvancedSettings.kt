package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import kotlinx.android.synthetic.main.settings_advanced.*
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
 */ //TODO add text size options
class AdvancedSettings : Fragment() {
    private val strings: MutableList<String> = ArrayList()

    init {
        strings.add("Light")
        strings.add("Dark")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "ViewSettings")
        return inflater.inflate(R.layout.settings_advanced, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Creating adapter for spinner
        if (context != null) {
            val dataAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, strings)
            settings_advanced_spinner.adapter = dataAdapter
            settings_advanced_spinner.setSelection(Settings.themeMode)
        }
        settings_advanced_spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                if (i in 0..2) {
                    Utilities.changeMode(activity!!, i)
                    adapterView.setSelection(i)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        purge_cache.setOnClickListener { view1: View? -> Database.DatabaseIdentification.purgeUnSavedNovels(view1) }
    }


}