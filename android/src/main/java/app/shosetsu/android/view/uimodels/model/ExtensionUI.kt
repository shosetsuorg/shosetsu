package app.shosetsu.android.view.uimodels.model

import android.view.View
import androidx.core.view.isVisible
import app.shosetsu.android.common.ext.picasso
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.BindViewHolder
import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.dto.Convertible
import app.shosetsu.lib.ExtensionType
import app.shosetsu.lib.Novel
import app.shosetsu.lib.Version
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ExtensionCardBinding

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
	var installedVersion: Version?,
	var repositoryVersion: Version,
	val chapterType: Novel.ChapterType,
	var md5: String,
	val extType: ExtensionType
) : BaseRecyclerItem<ExtensionUI.ViewHolder>(), Convertible<ExtensionEntity> {
	override val layoutRes: Int = R.layout.extension_card
	override val type: Int = R.layout.extension_card
	override var identifier: Long
		get() = id.toLong()
		set(_) {}

	/**
	 * Is the extension being updated currently?
	 */
	var isInstalling = false

	enum class State { UPDATE, NO_UPDATE, OBSOLETE }

	fun updateState(): State {
		if (repositoryVersion == OBSOLETE_VERSION) return State.OBSOLETE

		return if (installedVersion != null && installedVersion!!.compareTo(repositoryVersion) == -1)
			State.UPDATE else State.NO_UPDATE
	}

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
		chapterType,
		md5,
		extType
	)

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	/***/
	class ViewHolder(
		view: View
	) : BindViewHolder<ExtensionUI, ExtensionCardBinding>(view) {
		override val binding = ExtensionCardBinding.bind(view)

		override fun ExtensionCardBinding.bindView(item: ExtensionUI, payloads: List<Any>) {
			if (item.isInstalling) {
				installButton.isVisible = true
				installButton.setImageResource(R.drawable.animated_refresh)
				settings.isVisible = false
			} else
				if (item.installed && item.isExtEnabled) {
					installButton.isVisible = false
					version.text =
						item.installedVersion?.let { with(it) { "$major.$minor.$patch" } }

					when (item.updateState()) {
						State.UPDATE -> {
							installButton.isVisible = true
							installButton.setImageResource(R.drawable.download_tinted)
							installButton.rotation = 180f

							updateVersion.isVisible = true
							updateVersion.text =
								with(item.repositoryVersion) { "$major.$minor.$patch" }
						}
						State.NO_UPDATE -> {
							updateVersion.isVisible = false
						}
						State.OBSOLETE -> {
							updateVersion.isVisible = true
							updateVersion.setText(R.string.obsolete_extension)
							updateVersion.textSize = 32f
						}
					}
				} else {
					version.text = with(item.repositoryVersion) { "$major.$minor.$patch" }
					settings.isVisible = false
				}

			title.text = item.name
			language.text = item.lang

			if (!item.imageURL.isNullOrEmpty()) picasso(item.imageURL!!, imageView)
		}

		override fun ExtensionCardBinding.unbindView(item: ExtensionUI) {
			installButton.setImageResource(R.drawable.download)
			installButton.rotation = 0f
			installButton.isVisible = true

			settings.isVisible = true

			version.text = null
			updateVersion.text = null
			title.text = null
			language.text = null
			imageView.setImageResource(R.drawable.broken_image)
		}
	}

	companion object {
		val OBSOLETE_VERSION by lazy { Version(-9, -9, -9) }
	}
}