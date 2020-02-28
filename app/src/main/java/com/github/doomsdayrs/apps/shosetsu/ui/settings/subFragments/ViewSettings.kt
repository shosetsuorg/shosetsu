package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType
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
 */ // TODO: Migrate to using PreferenceScreen and PreferenceGroup. Maybe
class ViewSettings : SettingsSubController() {

    override val settings by lazy {
        arrayListOf(
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
                        }),
                SettingsItemData(SettingsType.NUMBER_PICKER)
                        .setTitle(R.string.columns_of_novel_listing_p)
                        .setDescription((R.string.columns_zero_automatic))
                        .setNumberValue(Settings.columnsInNovelsViewP.let { if (it == -1) 0 else it })
                        .setNumberLowerBound(0)
                        .setNumberUpperBound(10)
                        .setNumberPickerOnValueChangedListener { _, _, newVal ->
                            when (newVal) {
                                0 -> Settings.columnsInNovelsViewP = -1
                                else -> Settings.columnsInNovelsViewP = newVal
                            }
                        },
                SettingsItemData(SettingsType.NUMBER_PICKER)
                        .setTitle(R.string.columns_of_novel_listing_h)
                        .setDescription(R.string.columns_zero_automatic)
                        .setNumberValue(Settings.columnsInNovelsViewH.let { if (it == -1) 0 else it })
                        .setNumberLowerBound(0)
                        .setNumberUpperBound(10)
                        .setNumberPickerOnValueChangedListener { _, _, newVal ->
                            when (newVal) {
                                0 -> Settings.columnsInNovelsViewH = -1
                                else -> Settings.columnsInNovelsViewH = newVal
                            }
                        },
                SettingsItemData(SettingsType.SPINNER)
                        .setTitle((R.string.novel_card_type_selector_title))
                        .setDescription((R.string.novel_card_type_selector_desc))
                        .setSpinnerSelection(Settings.novelCardType)
                        .setOnItemSelectedListener(object : OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                Settings.novelCardType = position
                            }

                        })
        )
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        run {
            val x = findDataByID(R.string.text_size)
            settings[x].adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources!!.getStringArray(R.array.sizes_no_none))
            when (Settings.ReaderTextSize.toInt()) {
                14 -> settings[x].spinnerSelection = 0
                17 -> settings[x].spinnerSelection = 1
                20 -> settings[x].spinnerSelection = 2
                else -> settings[x].spinnerSelection = 0
            }
        }

        run {
            val x = findDataByID(R.string.reader_theme)
            settings[x].adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources!!.getStringArray(R.array.reader_themes))
            settings[x].spinnerSelection = Utilities.getReaderColor(context!!)
        }

        run {
            val x = findDataByID(R.string.spacing)
            settings[x].adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources!!.getStringArray(R.array.sizes_with_none))
            settings[x].spinnerSelection = Settings.paragraphSpacing
        }

        run {
            val x = findDataByID(R.string.indent_size)
            settings[x].adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources!!.getStringArray(R.array.sizes_with_none))
            settings[x].spinnerSelection = (Settings.indentSize)
        }

        run {
            val x = findDataByID(R.string.marking_mode)
            settings[x].adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources!!.getStringArray(R.array.marking_names))
            settings[x].spinnerSelection = Settings.ReaderMarkingType
        }

        run {
            val x = findDataByID(R.string.novel_card_type_selector_title)
            settings[x].adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources!!.getStringArray(R.array.novel_card_types))
        }
        Log.i("onViewCreated", "Finished creation")
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
    }
}