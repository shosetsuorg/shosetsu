package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter.SettingItemsAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import kotlinx.android.synthetic.main.settings_view.*
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
 */ // TODO: Migrate to using PreferenceScreen and PreferenceGroup. Maybe
class ViewSettings : Fragment() {
    val settings: ArrayList<SettingsItemData> = arrayListOf(

            SettingsItemData(SettingsType.SPINNER)
                    .setTitle(R.string.spacing)
                    .setOnItemSelectedListener(object : OnItemSelectedListener {
                        override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                            Log.d("SpaceSelection", i.toString())
                            if (i in 0..3) {
                                Settings.paragraphSpacing = (i)
                                adapterView.setSelection(i)
                            }
                        }

                        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                    }),

            SettingsItemData(SettingsType.SPINNER)
                    .setTitle(R.string.text_size)
                    .setOnItemSelectedListener(object : OnItemSelectedListener {
                        override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                            Log.d("TextSizeSelection", i.toString())
                            if (i in 0..2) {
                                var size = 14
                                when (i) {
                                    0 -> {
                                    }
                                    1 -> size = 17
                                    2 -> size = 20
                                }
                                Settings.ReaderTextSize = (size.toFloat())
                                adapterView.setSelection(i)
                            }
                        }

                        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                    }),

            SettingsItemData(SettingsType.SPINNER)
                    .setTitle(R.string.indent_size)
                    .setOnItemSelectedListener(object : OnItemSelectedListener {
                        override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                            Log.d("IndentSizeSelection", i.toString())
                            if (i in 0..3) {
                                Utilities.changeIndentSize(i)
                                adapterView.setSelection(i)
                            }
                        }

                        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                    }),

            SettingsItemData(SettingsType.SPINNER)
                    .setTitle(R.string.marking_mode)
                    .setOnItemSelectedListener(object : OnItemSelectedListener {
                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            Log.d("MarkingMode", p2.toString())
                            when (p2) {
                                0 -> {
                                    Utilities.setReaderMarkingType(Settings.MarkingTypes.ONVIEW)
                                }
                                1 -> {
                                    Utilities.setReaderMarkingType(Settings.MarkingTypes.ONSCROLL)
                                }
                                else -> {
                                    Log.e("MarkingMode", "UnknownType")
                                }
                            }
                        }
                    }),

            SettingsItemData(SettingsType.SPINNER)
                    .setTitle(R.string.reader_theme)
                    .setOnItemSelectedListener(object : OnItemSelectedListener {
                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            Log.d("NightMode", p1.toString())
                            when (p2) {
                                0 -> {
                                    Utilities.setNightNode()
                                }
                                1 -> {
                                    Utilities.setLightMode()
                                }
                                2 -> {
                                    p1?.context?.let { Utilities.setSepiaMode(it) }
                                }
                                else -> {
                                    Log.e("NightMode", "UnknownType")
                                }
                            }
                        }
                    }),

            SettingsItemData(SettingsType.SWITCH)
                    .setTitle(R.string.tap_to_scroll)
                    .setSwitchIsChecked(Utilities.isTapToScroll)
                    .setSwitchOnCheckedListner(CompoundButton.OnCheckedChangeListener { _, p1 ->
                        Log.d("Tap to scroll", p1.toString())
                        Utilities.toggleTapToScroll()
                    })
    )
    val adapter: SettingItemsAdapter = SettingItemsAdapter(settings)

    private fun findDataByID(@StringRes id: Int): Int {
        for ((index, data) in settings.withIndex()) {
            if (data.titleID == id)
                return index
        }
        return -1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "ViewSettings")
        return inflater.inflate(R.layout.settings_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        run {
            val x = findDataByID(R.string.text_size)
            settings[x].adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.sizes_no_none))
            when (Settings.ReaderTextSize.toInt()) {
                14 -> settings[x].spinnerSelection = 0
                17 -> settings[x].spinnerSelection = 1
                20 -> settings[x].spinnerSelection = 2
                else -> settings[x].spinnerSelection = 0
            }
        }

        run {
            val x = findDataByID(R.string.reader_theme)
            settings[x].adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.reader_themes))
            settings[x].spinnerSelection = Utilities.getReaderColor(context!!)
        }

        run {
            val x = findDataByID(R.string.spacing)
            settings[x].adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.sizes_with_none))
            settings[x].spinnerSelection = Settings.paragraphSpacing
        }

        run {
            val x = findDataByID(R.string.indent_size)
            settings[x].adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.sizes_with_none))
            settings[x].spinnerSelection = (Settings.indentSize)
        }

        run {
            val x = findDataByID(R.string.marking_mode)
            settings[x].adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.marking_names))
            settings[x].spinnerSelection = Settings.ReaderMarkingType
        }

        Log.i("onViewCreated", "Finished creation")
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }
}