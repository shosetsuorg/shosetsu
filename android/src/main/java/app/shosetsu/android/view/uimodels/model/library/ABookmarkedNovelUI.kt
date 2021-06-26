package app.shosetsu.android.view.uimodels.model.library

import android.view.View
import androidx.core.view.isVisible
import app.shosetsu.android.common.consts.SELECTED_STROKE_WIDTH
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.GetImageURL
import app.shosetsu.android.view.uimodels.base.GetTitle
import app.shosetsu.android.view.viewholders.TitleImageFViewHolder
import app.shosetsu.common.domain.model.local.LibraryNovelEntity
import app.shosetsu.common.dto.Convertible
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.card.MaterialCardView
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
 */

/**
 * shosetsu
 * 06 / 06 / 2020
 *
 * For displaying novels in library (UI) owo
 */
abstract class ABookmarkedNovelUI
	: BaseRecyclerItem<ABookmarkedNovelUI.ViewHolder>(), Convertible<LibraryNovelEntity>,
	GetImageURL, GetTitle {

	/** ID of the novel*/
	abstract val id: Int

	/** title of the novel*/
	abstract val title: String

	/** imageURL of the novel*/
	abstract val imageURL: String

	/** If this novel is bookmarked or not*/
	abstract var bookmarked: Boolean

	/** chapters of this novel*/
	abstract val unread: Int

	abstract val genres: List<String>
	abstract val authors: List<String>
	abstract val artists: List<String>
	abstract val tags: List<String>

	override var identifier: Long
		get() = id.toLong()
		set(@Suppress("UNUSED_PARAMETER") value) {}

	override fun getDataImageURL(): String = imageURL

	override fun getDataTitle(): String = title

	override fun convertTo(): LibraryNovelEntity =
		LibraryNovelEntity(
			id,
			title,
			imageURL,
			bookmarked,
			unread,
			genres,
			authors,
			artists,
			tags
		)

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	class ViewHolder(itemView: View) : TitleImageFViewHolder<ABookmarkedNovelUI>(itemView) {
		val materialCardView: MaterialCardView = itemView.findViewById(R.id.novel_item_card)
		val chip: Chip = itemView.findViewById(R.id.left_to_read_chip)

		override fun bindView(item: ABookmarkedNovelUI, payloads: List<Any>) {
			super.bindView(item, payloads)
			setUnreadCount(this, item.unread)
			materialCardView.strokeWidth = if (item.isSelected) SELECTED_STROKE_WIDTH else 0
		}

		override fun unbindView(item: ABookmarkedNovelUI) {
			super.unbindView(item)
			chip.text = null
			chip.setOnClickListener(null)
		}

		private fun setUnreadCount(viewHolder: ViewHolder, unreadCount: Int) {
			logD("Setting unread count of $unreadCount")
			viewHolder.chip.isVisible = if (unreadCount != 0) {
				viewHolder.chip.text = unreadCount.toString()
				true
			} else false
		}
	}
}