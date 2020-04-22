package com.github.doomsdayrs.apps.shosetsu.ui.extensions.adapter

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.shosetsuRoomDatabase
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.ExtensionsController
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.viewHolder.ExtensionHolder
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context
import com.github.doomsdayrs.apps.shosetsu.variables.ext.logID
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Formatters
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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
class ExtensionsAdapter(private val extensionsController: ExtensionsController)
	: RecyclerView.Adapter<ExtensionHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtensionHolder {
		return ExtensionHolder(LayoutInflater.from(parent.context).inflate(
				R.layout.extension_card,
				parent,
				false
		))
	}

	override fun getItemCount(): Int {
		return extensionsController.recyclerArray.size
	}

	override fun onBindViewHolder(holder: ExtensionHolder, position: Int) {
		val entity = extensionsController.recyclerArray[position]
		val id = entity.formatterID

		if (Formatters.getByID(id) != Formatters.unknown) {
			holder.button.text = holder.itemView.context.getString(R.string.uninstall)
			//  holder.button.setImageResource(R.drawable.ic_delete_black_24dp)
			holder.installed = true


			holder.version.text = entity.installedVersion
			if (FormatterUtils.compareVersions(
							entity.installedVersion ?: "",
							entity.repositoryVersion
					)) {
				Log.i(logID(), "$id has an update")
				holder.update = true
				// holder.button.setImageResource(R.drawable.ic_update_black_24dp)
				holder.button.text = holder.itemView.context.getText(R.string.update)
				holder.updatedVersion.visibility = View.VISIBLE
				holder.updatedVersion.text = entity.repositoryVersion
			} else {
				holder.updatedVersion.visibility = View.GONE
			}
		} else {
			holder.version.text = entity.installedVersion
		}

		holder.title.text = entity.name
		holder.id.text = id.toString()
		holder.hash.text = entity.md5
		holder.language.text = entity.lang

		holder.button.setOnClickListener {
			try {
				if (!holder.installed || holder.update) {
					GlobalScope.launch {
						extensionsController.context?.let { context ->
							entity.install(context)
							entity.installed = true
							entity.enabled = true

							holder.installed = true
							holder.update = false
							shosetsuRoomDatabase.formatterDao().updateFormatter(entity)
							Handler(Looper.getMainLooper()).post {
								context.toast("Installed ${entity.name}")
								this@ExtensionsAdapter.notifyDataSetChanged()
							}
						} ?: Log.e(logID(), "Context is missing to delete")

					}
				} else {
					GlobalScope.launch {
						extensionsController.context?.let { context ->
							entity.delete(context)
							entity.installed = false
							entity.enabled = false

							holder.installed = false
							holder.update = false
							shosetsuRoomDatabase.formatterDao().updateFormatter(entity)
							Handler(Looper.getMainLooper()).post {
								context.toast("Deleted ${entity.name}")
								this@ExtensionsAdapter.notifyDataSetChanged()
							}
						} ?: Log.e(logID(), "Context is missing to delete")
					}
				}
			} catch (e: Exception) {
				it.context.toast("Holy shit what happened")
				Log.e(logID(), "Unhandled exception", e)
			}

		}

		if (!entity.imageURL.isNullOrEmpty()) {
			Picasso.get().load(entity.imageURL).into(holder.imageView)
		}
	}
}