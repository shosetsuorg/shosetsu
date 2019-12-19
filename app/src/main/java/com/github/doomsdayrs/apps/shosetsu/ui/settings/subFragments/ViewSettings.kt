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
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
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
 */ // TODO: Migrate to using PreferenceScreen and PreferenceGroup.
class ViewSettings : Fragment() {
    private val textSizes: MutableList<String> = ArrayList()
    private val paragraphSpaces: MutableList<String> = ArrayList()
    private val indentSizes: MutableList<String> = ArrayList()

    init {
        textSizes.add("Small")
        textSizes.add("Medium")
        textSizes.add("Large")
        paragraphSpaces.add("None")
        paragraphSpaces.add("Small")
        paragraphSpaces.add("Medium")
        paragraphSpaces.add("Large")
        indentSizes.add("None")
        indentSizes.add("Small")
        indentSizes.add("Medium")
        indentSizes.add("Large")
    }


    //TODO remove this abomination of code. We just need to make a simple old switch
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "ViewSettings")
        return inflater.inflate(R.layout.settings_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup Text size
// SettingsItem textSizeItem = new SettingsItem(settingsReaderView.findViewById(R.id.settings_reader_text_size));
// textSizeItem.setTitle(R.string.text_size);
// TODO: Get current Text size
// Setup Paragraph Spacing
// SettingsItem paraSpacingItem = new SettingsItem(settingsReaderView.findViewById(R.id.settings_reader_para_spacing));
// paraSpacingItem.setTitle(R.string.spacing);
// TODO: Get current Paragraph spacing
// Setup Indent Size
// SettingsItem paraIndentItem = new SettingsItem(settingsReaderView.findViewById(R.id.settings_reader_para_indent));
// paraIndentItem.setTitle(R.string.indent_size);
// TODO: Get current Indent size
        //TODO figure out why the itemSelectedListner runs without being selected

        // Setup Night Mode
        //  val nightModeItem = SettingsItem(settings_reader_night_mode, SettingsItem.SettingsType.INFORMATION)
        //  nightModeItem.setTitle(R.string.reader_night_mode)
        val nightModeStatus = if (Utilities.isReaderNightMode()) R.string.on else R.string.off
        //  nightModeItem.setDescription(nightModeStatus)
        // nightModeItem.setOnClickListener { v: View -> onClickNightMode(v) }

        run {
            val dataAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, textSizes)
            reader_textSize!!.adapter = dataAdapter
            when (Settings.ReaderTextSize.toInt()) {
                14 -> reader_textSize!!.setSelection(0)
                17 -> reader_textSize!!.setSelection(1)
                20 -> reader_textSize!!.setSelection(2)
                else -> reader_textSize!!.setSelection(0)
            }
            reader_textSize!!.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
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
            }
        }

        run {
            val dataAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, paragraphSpaces)
            val spaceBack = Settings.paragraphSpacing
            when (Settings.paragraphSpacing) {
                0 -> reader_paragraphSpacing!!.setSelection(0)
                1 -> reader_paragraphSpacing!!.setSelection(1)
                2 -> reader_paragraphSpacing!!.setSelection(2)
                3 -> reader_paragraphSpacing!!.setSelection(3)
            }
            reader_paragraphSpacing.adapter = dataAdapter
            reader_paragraphSpacing.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    if (i in 0..3) {
                        Utilities.changeParagraphSpacing(i)
                        adapterView.setSelection(i)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
            Utilities.changeParagraphSpacing(spaceBack)
            when (Settings.paragraphSpacing) {
                0 -> reader_paragraphSpacing!!.setSelection(0)
                1 -> reader_paragraphSpacing!!.setSelection(1)
                2 -> reader_paragraphSpacing!!.setSelection(2)
                3 -> reader_paragraphSpacing!!.setSelection(3)
            }
        }

        run {
            val dataAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, indentSizes)
            val spaceBack = Settings.indentSize
            reader_indentSize!!.setSelection(Settings.indentSize)
            reader_indentSize!!.adapter = dataAdapter
            reader_indentSize!!.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    if (i in 0..3) {
                        Utilities.changeIndentSize(i)
                        adapterView.setSelection(i)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
            Utilities.changeIndentSize(spaceBack)
            reader_indentSize!!.setSelection(Settings.indentSize)
        }

        run {
            tap_to_scroll!!.isChecked = Utilities.isTapToScroll()
            tap_to_scroll!!.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean -> Utilities.toggleTapToScroll() }
        }
    }
}