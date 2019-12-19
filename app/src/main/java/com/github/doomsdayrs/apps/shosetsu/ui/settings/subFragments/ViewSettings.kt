package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter.SettingItemsAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import kotlinx.android.synthetic.main.settings_download.*
import kotlinx.android.synthetic.main.settings_view.*
import kotlinx.android.synthetic.main.settings_view.recyclerView
import okhttp3.internal.Util
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
    private val textSizes: Array<String> = arrayOf("Small", "Medium", "Large")
    private val paragraphSpaces: Array<String> = arrayOf("None", "Small", "Medium", "Large")
    private val indentSizes: Array<String> = arrayOf("None", "Small", "Medium", "Large")

    val settings: ArrayList<SettingsItem.SettingsItemData> = arrayListOf(
            SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.SWITCH)
                    .setTitle(R.string.reader_night_mode)
                    .setSwitchIsChecked(Utilities.isReaderNightMode())
                    .setSwitchOnCheckedListner(CompoundButton.OnCheckedChangeListener { _, p1 ->
                        Log.d("NightMode", p1.toString())
                        if (!Utilities.isReaderNightMode()) Utilities.setNightNode() else Utilities.unsetNightMode()
                    }),

            SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.SPINNER)
                    .setTitle(R.string.spacing)
                    .setOnItemSelectedListener(object : OnItemSelectedListener {
                        override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                            Log.d("SpaceSelection", i.toString())
                            if (i in 0..3) {
                                Utilities.changeParagraphSpacing(i)
                                adapterView.setSelection(i)
                            }
                        }

                        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                    }),

            SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.SPINNER)
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
                                Utilities.setTextSize(size)
                                adapterView.setSelection(i)
                            }
                        }

                        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                    }),

            SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.SPINNER)
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

            SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.SWITCH)
                    .setTitle(R.string.tap_to_scroll)
                    .setSwitchIsChecked(Utilities.isTapToScroll())
                    .setSwitchOnCheckedListner(CompoundButton.OnCheckedChangeListener { _, p1 ->
                        Log.d("NightMode", p1.toString())
                        Utilities.toggleTapToScroll()
                    })
    )
    val adapter: SettingItemsAdapter = SettingItemsAdapter(settings)


    /*
    private fun onClickNightMode(v: View) {
        if (this.context != null) {
            //   val nightMOdeItem = SettingsItem(v, SettingsItem.SettingsType.INFORMATION)
            val builder = AlertDialog.Builder(this.context!!)
            builder.setTitle(R.string.reader_night_mode)
            val states = arrayOf(getString(R.string.on), getString(R.string.off))
            builder.setItems(states
            ) { _: DialogInterface?, i: Int ->
                if (i == 0) Utilities.setNightNode() else Utilities.unsetNightMode()
                val nightModeStatus = if (Utilities.isReaderNightMode()) R.string.on else R.string.off
                //      nightMOdeItem.setDescription(nightModeStatus)
                //          nightMOdeItem.invalidate()
            }
            builder.show()
        }
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "ViewSettings")
        return inflater.inflate(R.layout.settings_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        run {

            val dataAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, textSizes)
            settings[2].setArrayAdapter(dataAdapter)
            when (Settings.ReaderTextSize.toInt()) {
                14 -> settings[2].spinnerSelection = 0
                17 -> settings[2].spinnerSelection = 1
                20 -> settings[2].spinnerSelection = 2
                else -> settings[2].spinnerSelection = 0
            }
        }

        run {
            val dataAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, paragraphSpaces)
            settings[1].adapter = dataAdapter
            settings[1].spinnerSelection = Settings.paragraphSpacing
        }

        run {
            val dataAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, indentSizes)
            val spaceBack = Settings.indentSize
            settings[3].adapter = dataAdapter
            settings[3].spinnerSelection = (Settings.indentSize)
        }

        Log.i("onViewCreated", "Finished creation")
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }
}