package app.shosetsu.android.view.uimodels.model

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import app.shosetsu.android.common.ext.picasso
import app.shosetsu.android.common.utils.FormatterUtils
import app.shosetsu.android.domain.model.base.Convertible
import app.shosetsu.android.domain.model.local.ExtensionEntity
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter
import com.squareup.picasso.Picasso

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

	/***/
	class ViewHolder(itemView: View) : FastAdapter.ViewHolder<ExtensionUI>(itemView) {
		val imageView: ImageView = itemView.findViewById(R.id.imageView)
		val language: TextView = itemView.findViewById(R.id.language)
		val title: TextView = itemView.findViewById(R.id.title)
		private val hash: TextView = itemView.findViewById(R.id.hash)
		private val idText: TextView = itemView.findViewById(R.id.id)
		val version: TextView = itemView.findViewById(R.id.version)
		private val updatedVersion: TextView = itemView.findViewById(R.id.update_version)
		val button: Button = itemView.findViewById(R.id.button)

		override fun bindView(item: ExtensionUI, payloads: List<Any>) {
			val id = item.id

			if (item.installed && item.isExtEnabled) {
				button.setText(R.string.uninstall)
				version.text = item.installedVersion

				if (FormatterUtils.compareVersions(
								item.installedVersion ?: "",
								item.repositoryVersion
						)) {
					button.setText(R.string.update)
					updatedVersion.visibility = View.VISIBLE
					updatedVersion.text = item.repositoryVersion
				} else {
					updatedVersion.visibility = View.GONE
				}
			} else {
				version.text = item.installedVersion
			}

			title.text = item.name
			idText.text = id.toString()
			hash.text = item.md5
			language.text = item.lang

			if (!item.imageURL.isNullOrEmpty()) picasso(item.imageURL!!, imageView)
		}

		override fun unbindView(item: ExtensionUI) {
			button.setText(R.string.download)
			version.text = null
			updatedVersion.text = null
			title.text = null
			idText.text = null
			hash.text = null
			language.text = null
			imageView.setImageResource(R.drawable.ic_broken_image_24dp)
		}
	}

	override val layoutRes: Int = R.layout.extension_card
	override val type: Int = R.layout.extension_card

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
}