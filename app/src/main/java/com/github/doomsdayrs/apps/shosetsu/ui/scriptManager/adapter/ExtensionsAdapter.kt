package com.github.doomsdayrs.apps.shosetsu.ui.scriptManager.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.dep.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterController
import com.github.doomsdayrs.apps.shosetsu.ui.scriptManager.ScriptManagementFragment
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
class ExtensionsAdapter(private val scriptManagementFragment: ScriptManagementFragment) : RecyclerView.Adapter<ExtensionsAdapter.ExtensionHolder>() {
    class ExtensionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var installed: Boolean = false
        var update: Boolean = false
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val title: TextView = itemView.findViewById(R.id.title)
        val hash: TextView = itemView.findViewById(R.id.hash)
        val identification: TextView = itemView.findViewById(R.id.id)
        val version: TextView = itemView.findViewById(R.id.version)
        val updatedVersion: TextView = itemView.findViewById(R.id.update_version)
        val button: Button = itemView.findViewById(R.id.floatingActionButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtensionHolder {
        return ExtensionHolder(LayoutInflater.from(parent.context).inflate(R.layout.extension_card, parent, false))
    }

    override fun getItemCount(): Int {
        return scriptManagementFragment.array.size
    }


    override fun onBindViewHolder(holder: ExtensionHolder, position: Int) {
        val jsonObject: JSONObject = scriptManagementFragment.array[position]
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

            if (v == 1) {
                Log.i("ExtensionsAdapter", "UPDATE")
                holder.update = true
                // holder.button.setImageResource(R.drawable.ic_update_black_24dp)
                holder.button.text = holder.itemView.context.getText(R.string.update)
                holder.updatedVersion.visibility = View.VISIBLE
                holder.updatedVersion.text = jsonObject.getString("version")
            } else {
                holder.updatedVersion.visibility = View.GONE
            }
        }

        holder.title.text = jsonObject.getString("name")
        holder.identification.text = id.toString()
        holder.hash.text = jsonObject.getString("md5")

        holder.button.setOnClickListener {
            if (!holder.installed || holder.update) {
                FormatterController.downloadScript(jsonObject.getString("name"), holder, scriptManagementFragment.activity!!)
            } else
                FormatterController.deleteScript(jsonObject.getString("name"), id, holder, scriptManagementFragment.activity!!)

        }

        if (!jsonObject.getString("imageURL").isNullOrEmpty()) {
            Picasso.get().load(jsonObject.getString("imageURL")).into(holder.imageView)
        }
    }
}