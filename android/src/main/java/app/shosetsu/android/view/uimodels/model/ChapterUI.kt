package app.shosetsu.android.view.uimodels.model

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import app.shosetsu.android.common.consts.SELECTED_STROKE_WIDTH
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.BindViewHolder
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.dto.Convertible
import app.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.RecyclerNovelChapterBinding

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
 *
 * @author github.com/doomsdayrs
 */
data class ChapterUI(
	val id: Int,
	val novelID: Int,
	val link: String,
	val extensionID: Int,
	var title: String,
	var releaseDate: String,
	var order: Double,
	var readingPosition: Double,
	var readingStatus: ReadingStatus,
	var bookmarked: Boolean,
	var isSaved: Boolean,
) : BaseRecyclerItem<ChapterUI.ViewHolder>(), Convertible<ChapterEntity> {
	override val layoutRes: Int = R.layout.recycler_novel_chapter

	override val type: Int = R.layout.recycler_novel_chapter

	override var identifier: Long
		get() = id.toLong()
		set(@Suppress("UNUSED_PARAMETER") value) {}

	override fun convertTo(): ChapterEntity =
		ChapterEntity(
			id,
			link,
			novelID,
			extensionID,
			title,
			releaseDate,
			order,
			readingPosition,
			readingStatus,
			bookmarked,
			isSaved
		)

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	class ViewHolder(itemView: View) :
		BindViewHolder<ChapterUI, RecyclerNovelChapterBinding>(itemView) {
		override val binding = RecyclerNovelChapterBinding.bind(view)


		private var oldColors: ColorStateList? = null


		private fun setAlpha(float: Float = 1f) {
			binding.title.alpha = float
			binding.readProgressValue.alpha = float
			binding.readTag.alpha = float
			binding.downloadTag.alpha = float
		}

		override fun RecyclerNovelChapterBinding.bindView(item: ChapterUI, payloads: List<Any>) {
			//Log.d(logID(), "Binding ${chapterUI.id}")

			cardView.strokeWidth = if (item.isSelected) SELECTED_STROKE_WIDTH else 0
			if (item.isSelected) cardView.isSelected


			title.text = item.title
			oldColors = title.textColors
			if (item.bookmarked) {
				title.setTextColor(
					ContextCompat.getColor(
						itemView.context,
						R.color.bookmarked
					)
				)
			}

			downloadTag.isVisible = item.isSaved

			when (item.readingStatus) {
				ReadingStatus.READING -> {
					setAlpha()
					if (item.readingPosition > 0) {
						readTag.isVisible = true
						readProgressValue.isVisible = true
						readProgressValue.text = ("%2.1f%%".format(item.readingPosition))
					}
				}
				ReadingStatus.UNREAD -> setAlpha()
				ReadingStatus.READ -> {
					setAlpha(0.5F) // Opacity to move attention away
					readTag.isVisible = false
					readProgressValue.isVisible = false
				}
				else -> {
				}
			}

		}

		override fun RecyclerNovelChapterBinding.unbindView(item: ChapterUI) {
			title.text = null
			oldColors.let { title.setTextColor(it) }
			readProgressValue.isVisible = false
			readProgressValue.text = null
			readTag.isVisible = false
			downloadTag.isVisible = false
		}
	}
}
