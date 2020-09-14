package app.shosetsu.android.view.uimodels.model

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import app.shosetsu.android.common.ext.picasso
import app.shosetsu.android.common.utils.FormatterUtils
import app.shosetsu.android.domain.model.base.Convertible
import app.shosetsu.android.domain.model.local.ExtensionEntity
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter

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
 */

/**
 * shosetsu
 * 24 / 04 / 2020
 */
data class ExtensionUI(
		val id: Int,
		val repoID: Int,
		var name: String,
		val fileName: String,
		var imageURL: String?,
		var lang: String,
		var isExtEnabled: Boolean,
		var installed: Boolean,
		var installedVersion: String?,
		var repositoryVersion: String,
		var md5: String,
) : BaseRecyclerItem<ExtensionUI.ViewHolder>(), Convertible<ExtensionEntity> {
	override val layoutRes: Int = R.layout.extension_card
	override val type: Int = R.layout.extension_card
	override var identifier: Long
		get() = id.toLong()
		set(value) {}

	fun hasUpdate() = FormatterUtils.compareVersions(installedVersion ?: "", repositoryVersion)

	override fun convertTo(): ExtensionEntity = ExtensionEntity(
			id,
			repoID,
			name,
			fileName,
			imageURL,
			lang,
			isExtEnabled,
			installed,
			installedVersion,
			repositoryVersion,
			md5
	)

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	/***/
	class ViewHolder(itemView: View) : FastAdapter.ViewHolder<ExtensionUI>(itemView) {
		val imageView: ImageView = itemView.findViewById(R.id.imageView)
		val language: TextView = itemView.findViewById(R.id.language)
		val title: TextView = itemView.findViewById(R.id.title)
		val version: TextView = itemView.findViewById(R.id.version)
		private val updatedVersion: TextView = itemView.findViewById(R.id.update_version)

		val download: ImageButton = itemView.findViewById(R.id.button)
		val settings: ImageButton = itemView.findViewById(R.id.settings)


		override fun bindView(item: ExtensionUI, payloads: List<Any>) {
			if (item.installed && item.isExtEnabled) {
				download.isVisible = false
				version.text = item.installedVersion

				if (item.hasUpdate()) {
					download.isVisible = true
					download.setImageResource(R.drawable.ic_file_update)
					download.rotation = 180f

					updatedVersion.visibility = View.VISIBLE
					updatedVersion.text = item.repositoryVersion
				} else {
					updatedVersion.visibility = View.GONE
				}
			} else {
				version.text = item.repositoryVersion
			}

			title.text = item.name
			language.text = item.lang

			if (!item.imageURL.isNullOrEmpty()) picasso(item.imageURL!!, imageView)
		}

		override fun unbindView(item: ExtensionUI) {
			download.setImageResource(R.drawable.ic_file_download)
			download.rotation = 0f
			download.isVisible = true

			settings.isVisible = true

			version.text = null
			updatedVersion.text = null
			title.text = null
			language.text = null
			imageView.setImageResource(R.drawable.ic_broken_image_24dp)
		}
	}
}