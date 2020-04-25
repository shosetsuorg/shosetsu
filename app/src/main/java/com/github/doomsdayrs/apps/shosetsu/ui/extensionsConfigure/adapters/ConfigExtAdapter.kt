package com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.ConfigureExtensions
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.viewHolders.ConfigExtView
import com.github.doomsdayrs.apps.shosetsu.variables.ext.defaultListing
import com.github.doomsdayrs.apps.shosetsu.variables.ext.setDefaultListing
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.variables.obj.FormattersRepository
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

/**
 * shosetsu
 * 21 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ConfigExtAdapter(val configureExtensions: ConfigureExtensions) : RecyclerView.Adapter<ConfigExtView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfigExtView {
        return ConfigExtView(LayoutInflater.from(parent.context).inflate(R.layout.alert_extensions_configure_card, parent, false))
    }

    override fun getItemCount(): Int {
        return configureExtensions.jsonArray.length() + FormattersRepository.formatters.size
    }

    override fun onBindViewHolder(holder: ConfigExtView, position: Int) {
        val name: String
        val id: Int
        val image: String

        var enabled = false
        val isInteral: Boolean
        var fom: Formatter? = null
        if (position < configureExtensions.jsonArray.length()) {
            val jsonObject: JSONObject = configureExtensions.jsonArray[position] as JSONObject
            name = jsonObject.getString("name")
            id = jsonObject.getInt("id")
            image = jsonObject.getString("imageUrl")
            isInteral = jsonObject.getBoolean("internal")
        } else {
            fom = FormattersRepository.formatters[position - configureExtensions.jsonArray.length()]
            name = fom.name
            id = fom.formatterID
            image = fom.imageURL
            enabled = true
            // TODO Fix this
            isInteral = false
        }

        if (image.isNotEmpty())
            Picasso.get().load(image).into(holder.imageView)

        holder.title.text = name

        holder.switch.isChecked = enabled
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.switch.setText(R.string.enabled)


                val file = if (!isInteral)
                    File(Utilities.shoDir + FormatterUtils.scriptDirectory + FormatterUtils.sourceFolder + name + ".lua")
                else File(configureExtensions.activity!!.filesDir.absolutePath + FormatterUtils.sourceFolder + FormatterUtils.scriptDirectory + name + ".lua")

                FormatterUtils.confirm(file, object : FormatterUtils.CheckSumAction {
                    override fun fail() {
                        holder.switch.isChecked = !isChecked
                    }

                    override fun pass() {
                        configureExtensions.jsonArray.remove(findPostion(id))
                        FormattersRepository.formatters.add(LuaFormatter(file))
                        Settings.disabledFormatters = configureExtensions.jsonArray
                    }

                    override fun noMeta() {
                        holder.switch.isChecked = !isChecked
                    }

                })

            } else {
                holder.switch.setText(R.string.disabled)
                if (findPostion(id) == -1) {
                    val js = JSONObject()
                    js.put("name", name)
                    js.put("id", id)
                    js.put("imageUrl", image)
                    js.put("internal", isInteral)
                    configureExtensions.jsonArray.put(js)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        FormattersRepository.formatters.removeIf { it.formatterID == id }
                    } else {
                        var point = -1
                        for (i in 0 until FormattersRepository.formatters.size)
                            if (FormattersRepository.formatters[i].formatterID == id)
                                point = i
                        if (point != -1)
                            FormattersRepository.formatters.removeAt(point)
                    }
                    Settings.disabledFormatters = configureExtensions.jsonArray
                }
            }
        }
        fom?.let {
            if (fom.listings.size > 1) {
                val a = ArrayList<String>()
                holder.constraintLayout.visibility = View.VISIBLE
                fom.listings.forEach { a.add(it.name) }
                holder.spinner.adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, a)
                holder.spinner.setSelection(fom.defaultListing)
                var first = true
                holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (!first)
                            if (!fom.setDefaultListing(position))
                                view?.context?.toast(R.string.invalid_selection)
                            else view?.context?.toast("${view.context.getString(R.string.set_to)} ${parent?.getItemAtPosition(position)}")
                        else first = !first
                    }
                }

            }
        }
    }

    fun findPostion(id: Int): Int {
        for (i in 0 until configureExtensions.jsonArray.length())
            if ((configureExtensions.jsonArray[i] as JSONObject).getInt("id") == id)
                return i
        return -1
    }

}