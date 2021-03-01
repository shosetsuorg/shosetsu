package app.shosetsu.android.view.uimodels.model

import android.view.View
import android.widget.ImageView
import app.shosetsu.android.common.ext.getString
import app.shosetsu.android.common.ext.picasso
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.BindViewHolder
import app.shosetsu.android.view.uimodels.model.NovelUI.ViewHolder
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.dto.Convertible
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoHeaderBinding
import com.google.android.material.chip.Chip

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
data class NovelUI(
	val id: Int,

	val novelURL: String,

	val extID: Int,

	var extName: String = "",

	var bookmarked: Boolean,

	var title: String,

	var imageURL: String,

	var description: String,
	var loaded: Boolean,
	var language: String,

	var genres: List<String>,
	var authors: List<String>,
	var artists: List<String>,
	var tags: List<String>,

	var status: Novel.Status,
) : BaseRecyclerItem<ViewHolder>(), Convertible<NovelEntity> {

	override val layoutRes: Int = R.layout.controller_novel_info_header
	override val type: Int = R.layout.controller_novel_info_header

	/**
	 * Identifier made negative to avoid conflicts with [ChapterUI]
	 */
	override var identifier: Long = -1091
	override var isSelectable: Boolean = false
	override fun convertTo(): NovelEntity = NovelEntity(
		id = id,
		url = novelURL,
		extensionID = extID,
		bookmarked = bookmarked,
		loaded = loaded,
		title = title,
		imageURL = imageURL,
		description = description,
		language = language,
		genres = genres,
		authors = authors,
		artists = artists,
		tags = tags,
		status = status
	)

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
	class ViewHolder(view: View) : BindViewHolder<NovelUI, ControllerNovelInfoHeaderBinding>(view) {
		override val binding: ControllerNovelInfoHeaderBinding by lazy {
			ControllerNovelInfoHeaderBinding.bind(view)
		}

		override fun ControllerNovelInfoHeaderBinding.bindView(item: NovelUI, payloads: List<Any>) {
			// Handle title
			novelTitle.text = item.title
			novelSite.text = item.extName
			// Handle authors
			if (item.authors.isNotEmpty())
				novelAuthor.text = item.authors.takeIf {
					it.isNotEmpty()
				}?.joinToString(", ") ?: itemView.getString(R.string.none)

			// Handle description
			novelDescription.text = item.description

			// Handle artists
			if (item.artists.isNotEmpty())
				novelArtists.text = item.artists.takeIf {
					it.isNotEmpty()
				}?.joinToString(", ") ?: itemView.getString(R.string.none)

			// Handles the status of the novel
			when (item.status) {
				Novel.Status.PAUSED -> novelPublish.setText(R.string.paused)
				Novel.Status.COMPLETED -> novelPublish.setText(R.string.completed)
				Novel.Status.PUBLISHING -> novelPublish.setText(R.string.publishing)
				else -> novelPublish.setText(R.string.unknown)
			}

			// Inserts the chips for genres
			novelGenres.removeAllViews()
			for (string in item.genres) {
				val chip = Chip(novelGenres.context)
				chip.text = string
				novelGenres.addView(chip)
			}

			// Loads the image
			listOf(novelImage, novelImageBackground).forEach { iV: ImageView? ->
				if (item.imageURL.isNotEmpty()) {
					iV?.let {
						picasso(item.imageURL, it)
					}
				} else {
					iV?.setImageResource(R.drawable.broken_image)
				}
			}

			if (item.bookmarked) {
				inLibrary.setChipIconResource(R.drawable.ic_heart_svg_filled)
				inLibrary.setText(R.string.in_library)
			} else {
				inLibrary.setChipIconResource(R.drawable.ic_heart_svg)
				inLibrary.setText(R.string.add_to_library)
			}
		}

		override fun ControllerNovelInfoHeaderBinding.unbindView(item: NovelUI) {
			inLibrary.setChipIconResource(R.drawable.ic_heart_svg)
			inLibrary.setText(R.string.add_to_library)
			listOf(novelImage, novelImageBackground).forEach { iV: ImageView? ->
				iV?.setImageResource(R.drawable.broken_image)
			}
			novelGenres.removeAllViews()
			novelPublish.setText(R.string.unknown)
			novelArtists.text = null
			novelDescription.text = null
			novelAuthor.text = null
			novelSite.text = null
			novelTitle.text = null
		}
	}
}