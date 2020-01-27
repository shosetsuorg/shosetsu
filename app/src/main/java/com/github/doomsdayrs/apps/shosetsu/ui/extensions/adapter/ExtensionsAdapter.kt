package com.github.doomsdayrs.apps.shosetsu.ui.extensions.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.dep.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterController
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.ExtensionsFragment
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.viewHolder.ExtensionHolder
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import com.squareup.picasso.Picasso
import org.json.JSONObject


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
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsAdapter(private val extensionsFragment: ExtensionsFragment) : RecyclerView.Adapter<ExtensionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtensionHolder {
        return ExtensionHolder(LayoutInflater.from(parent.context).inflate(R.layout.extension_card, parent, false))
    }

    override fun getItemCount(): Int {
        return extensionsFragment.array.size
    }

    override fun onBindViewHolder(holder: ExtensionHolder, position: Int) {
        val jsonObject: JSONObject = extensionsFragment.array[position]
        val id = jsonObject.getInt("id")

        if (DefaultScrapers.getByID(id) != DefaultScrapers.unknown) {
            holder.button.text = holder.itemView.context.getString(R.string.uninstall)
            //  holder.button.setImageResource(R.drawable.ic_delete_black_24dp)
            holder.installed = true

            val luaFormatter: LuaFormatter = (DefaultScrapers.getByID(id) as LuaFormatter)
            val meta = luaFormatter.getMetaData()!!
            holder.version.text = meta.getString("version")
            val v = FormatterController.compareVersions(jsonObject.getString("version"), meta.getString("version"))
            Log.i("ExtensionsAdapter", "Update $id : $v")

            if (v == -1) {
                Log.i("ExtensionsAdapter", "UPDATE")
                holder.update = true
                // holder.button.setImageResource(R.drawable.ic_update_black_24dp)
                holder.button.text = holder.itemView.context.getText(R.string.update)
                holder.updatedVersion.visibility = View.VISIBLE
                holder.updatedVersion.text = jsonObject.getString("version")
            } else {
                holder.updatedVersion.visibility = View.GONE
            }
        } else {
            holder.version.text = jsonObject.getString("version")
        }

        holder.title.text = jsonObject.getString("name")
        holder.id.text = id.toString()
        holder.hash.text = jsonObject.getString("md5")
        holder.language.text = jsonObject.getString("lang")
        holder.button.setOnClickListener {
            if (!holder.installed || holder.update) {
                FormatterController.downloadScript(jsonObject.getString("name"),jsonObject.getString("lang"), holder, extensionsFragment.activity!!)
            } else
                FormatterController.deleteScript(jsonObject.getString("name"), id, holder, extensionsFragment.activity!!)

        }

        if (!jsonObject.getString("imageURL").isNullOrEmpty()) {
            Picasso.get().load(jsonObject.getString("imageURL")).into(holder.imageView)
        }
    }
}