package com.github.doomsdayrs.apps.shosetsu.ui.scriptManager.adapter

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterController
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.scriptManager.ScriptManagementFragment
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption


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
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val title: TextView = itemView.findViewById(R.id.title)
        val hash: TextView = itemView.findViewById(R.id.hash)
        val identification: TextView = itemView.findViewById(R.id.id)
        val version: TextView = itemView.findViewById(R.id.version)
        val floatingActionButton: FloatingActionButton = itemView.findViewById(R.id.floatingActionButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtensionHolder {
        return ExtensionHolder(LayoutInflater.from(parent.context).inflate(R.layout.extension_card, parent, false))
    }

    override fun getItemCount(): Int {
        return scriptManagementFragment.array.size
    }

    override fun onBindViewHolder(holder: ExtensionHolder, position: Int) {
        val jsonObject: JSONObject = scriptManagementFragment.array[position]
        holder.title.text = jsonObject.getString("name")
        val id = jsonObject.getInt("id")
        holder.identification.text = id.toString()
        holder.hash.text = jsonObject.getString("md5")
        if (DefaultScrapers.getByID(id) != DefaultScrapers.unknown) {
            holder.floatingActionButton.setImageResource(R.drawable.ic_delete_black_24dp)
            holder.installed = true
        }
        holder.floatingActionButton.setOnClickListener {
            if (!holder.installed) {
                val request: DownloadManager.Request = DownloadManager.Request(Uri.parse("https://raw.githubusercontent.com/Doomsdayrs/shosetsu-extensions/master/src/main/resources/${jsonObject.getString("name")}.lua"))
                request.setDescription("Installing ${jsonObject.getString("name")}")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, jsonObject.getString("name") + ".lua")

                val manager = scriptManagementFragment.activity!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val downloadID = manager.enqueue(request)
                val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent) {
                        val intentID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        if (downloadID == intentID) {
                            Toast.makeText(scriptManagementFragment.context, "Installed: " + jsonObject.getString("name"), Toast.LENGTH_SHORT).show()
                            var file = scriptManagementFragment.activity!!.getExternalFilesDir(null)!!.absolutePath
                            file = file.substring(0, file.indexOf("/Android"))
                            val downloadedFile = File("$file/${Environment.DIRECTORY_DOCUMENTS}/${jsonObject.getString("name")}.lua")
                            val targetFile = File(Utilities.shoDir + FormatterController.directory + FormatterController.scriptFolder + "/${jsonObject.getString("name")}.lua")
                            Log.i("Extension download", downloadedFile.absolutePath)
                            Log.i("Extension download", targetFile.absolutePath)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Files.move(downloadedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                            } else {
                                downloadedFile.renameTo(targetFile)
                            }

                            holder.floatingActionButton.setImageResource(R.drawable.ic_delete_black_24dp)
                            holder.installed = true
                            DefaultScrapers.formatters.add(FormatterController.getScriptFromSystem(targetFile.absolutePath))
                        }
                    }
                }
                scriptManagementFragment.activity!!.registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            } else {
                holder.floatingActionButton.setImageResource(R.drawable.ic_file_download)
                var i = 0
                while (i < DefaultScrapers.formatters.size && holder.installed) {
                    if (DefaultScrapers.formatters[i].formatterID == id) {
                        DefaultScrapers.formatters.removeAt(i)
                        holder.installed = false
                        val targetFile = File(Utilities.shoDir + FormatterController.directory + FormatterController.scriptFolder + "/${jsonObject.getString("name")}.lua")
                        targetFile.delete()
                        Toast.makeText(scriptManagementFragment.context, "Script deleted", Toast.LENGTH_SHORT).show()
                    }
                    i++
                }
            }
        }

        holder.version.text = jsonObject.getString("version")

        if (!jsonObject.getString("imageURL").isNullOrEmpty()) {
            Picasso.get().load(jsonObject.getString("imageURL")).into(holder.imageView)
        }
    }
}