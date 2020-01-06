package com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder

import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R

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
 * hXtreme
 */
@Suppress("unused")
class SettingsItem(view: View) : RecyclerView.ViewHolder(view) {
    private var type: SettingsItemData.SettingsType = SettingsItemData.SettingsType.INFORMATION

    private val itemTitle: TextView = itemView.findViewById(R.id.settings_item_title)
    private val itemDescription: TextView = itemView.findViewById(R.id.settings_item_desc)
    val button: Button = itemView.findViewById(R.id.button)
    private val spinner: Spinner = itemView.findViewById(R.id.spinner)
    val textView: TextView = itemView.findViewById(R.id.text)
    private val switchView: Switch = itemView.findViewById(R.id.switchView)


    fun setData(data: SettingsItemData): SettingsItem {
        type = data.type
        if (data.titleID != -1)
            itemTitle.setText(data.titleID)
        else
            itemTitle.text = data.titleText

        if (data.descID != -1)
            itemDescription.setText(data.descID)
        else
            itemDescription.text = data.descriptionText



        when (type) {
            SettingsItemData.SettingsType.BUTTON -> {
                if (data.textID != -1)
                    button.setText(data.textID)
                else
                    button.text = data.textText
                button.visibility = Button.VISIBLE
                button.setOnClickListener(data.buttonOnClickListener)
            }
            SettingsItemData.SettingsType.SPINNER -> {
                spinner.visibility = Spinner.VISIBLE
                //spinner.setOnClickListener { data.spinnerOnClick }
                spinner.adapter = data.adapter
                spinner.setSelection(data.spinnerSelection)
                spinner.onItemSelectedListener = data.spinnerOnItemSelectedListener
            }
            SettingsItemData.SettingsType.INFORMATION -> {
                itemView.setOnClickListener(data.itemViewOnClick)
            }
            SettingsItemData.SettingsType.TEXT -> {
                if (data.textID != -1)
                    textView.setText(data.textID)
                else
                    textView.text = data.textText
                textView.visibility = TextView.VISIBLE
                textView.setOnClickListener(data.textViewOnClickListener)
            }
            SettingsItemData.SettingsType.SWITCH -> {
                switchView.visibility = View.VISIBLE
                switchView.isChecked = data.switchIsChecked
                switchView.setOnCheckedChangeListener(data.switchOnCheckedListener)
            }
        }
        return this
    }

    class SettingsItemData(val type: SettingsType) {
        enum class SettingsType {
            INFORMATION,
            BUTTON,
            SPINNER,
            TEXT,
            SWITCH
        }

        var titleID: Int = -1
        var titleText: String = ""

        var descID: Int = -1
        var descriptionText: String = ""

        var textID: Int = -1
        var textText: String = ""
        var textViewOnClickListener: (View) -> Unit

        var buttonOnClickListener: (View) -> Unit

        var itemViewOnClick: (View) -> Unit

        private var spinnerOnClick: (View) -> Unit
        var spinnerOnItemSelectedListener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }
        }
        lateinit var adapter: ArrayAdapter<*>
        var spinnerSelection: Int = -1

        var switchIsChecked: Boolean = false
        var switchOnCheckedListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, _ -> }

        init {
            textViewOnClickListener = {}
            buttonOnClickListener = {}
            spinnerOnClick = {}
            itemViewOnClick = {}
        }

        fun setTitle(titleID: Int): SettingsItemData {
            this.titleID = titleID
            return this
        }

        fun setDescription(descID: Int): SettingsItemData {
            this.descID = descID
            return this
        }

        fun setTitle(title: String): SettingsItemData {
            this.titleText = title
            return this
        }

        fun setDescription(desc: String): SettingsItemData {
            descriptionText = desc
            return this
        }

        fun setOnClickListenerButton(onClickListener: (View) -> Unit): SettingsItemData {
            buttonOnClickListener = (onClickListener)
            return this
        }

        @Suppress("unused")
        fun setOnClickListenerSpinner(onClickListener: (View) -> Unit): SettingsItemData {
            spinnerOnClick = onClickListener
            return this
        }

        fun setOnClickListener(onClickListener: (View) -> Unit): SettingsItemData {
            itemViewOnClick = onClickListener
            return this
        }

        fun setOnItemSelectedListener(onItemSelectedListener: AdapterView.OnItemSelectedListener): SettingsItemData {
            spinnerOnItemSelectedListener = onItemSelectedListener
            return this
        }

        fun setArrayAdapter(ad: ArrayAdapter<*>): SettingsItemData {
            adapter = ad
            return this
        }

        fun setSpinnerSelection(i: Int): SettingsItemData {
            spinnerSelection = i
            return this
        }

        fun setTextViewText(s: String): SettingsItemData {
            textText = s
            return this
        }

        fun setTextViewText(id: Int): SettingsItemData {
            textID = id
            return this
        }

        fun setTextOnClickListener(onClickListener: (View) -> Unit): SettingsItemData {
            textViewOnClickListener = onClickListener
            return this
        }

        fun setSwitchIsChecked(b: Boolean): SettingsItemData {
            Log.d("Data-SwitchChecked", b.toString())
            switchIsChecked = b
            return this
        }

        fun setSwitchOnCheckedListner(onCheckedChangeListener: CompoundButton.OnCheckedChangeListener): SettingsItemData {
            switchOnCheckedListener = onCheckedChangeListener
            return this
        }
    }

}