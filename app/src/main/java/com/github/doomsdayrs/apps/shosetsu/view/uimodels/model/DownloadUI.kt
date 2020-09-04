package com.github.doomsdayrs.apps.shosetsu.view.uimodels.model

import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.DownloadEntity
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.base.BaseRecyclerItem
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
 * ====================================================================
 */

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 *
 */
data class DownloadUI(
		val chapterID: Int,
		val novelID: Int,
		val chapterURL: String,
		val chapterName: String,
		val novelName: String,
		val formatterID: Int,
		var status: Int = 0
) : BaseRecyclerItem<DownloadUI.ViewHolder>(), Convertible<DownloadEntity> {
	override var identifier: Long
		get() = chapterID.toLong()
		set(value) {}

	override fun convertTo() = DownloadEntity(
			chapterID,
			novelID,
			chapterURL,
			chapterName,
			novelName,
			formatterID,
			status
	)

	class ViewHolder(itemView: View) : FastAdapter.ViewHolder<DownloadUI>(itemView) {

		/** Novel title */
		val novelTitle: TextView = itemView.findViewById(R.id.novel_title)

		/** Chapter title */
		val chapterTitle: TextView = itemView.findViewById(R.id.chapter_title)

		/** Status of the download */
		val status: TextView = itemView.findViewById(R.id.status)

		/** More option to apply to the download */
		var moreOptions: ImageView = itemView.findViewById(R.id.more_options)

		/** Popup menu for [moreOptions] */
		var popupMenu: PopupMenu? = null

		init {
			if (popupMenu == null) {
				popupMenu = PopupMenu(moreOptions.context, moreOptions)
				popupMenu!!.inflate(R.menu.popup_download_menu)
			}
		}

		override fun bindView(item: DownloadUI, payloads: List<Any>) {
			novelTitle.text = item.novelName
			chapterTitle.text = item.chapterName
			status.text = item.status.toString()
			moreOptions.setOnClickListener { popupMenu?.show() }
		}

		override fun unbindView(item: DownloadUI) {
			novelTitle.text = null
			chapterTitle.text = null
			status.text = null
			moreOptions.setOnClickListener(null)
			popupMenu = null
		}
	}

	override val layoutRes: Int = R.layout.recycler_download_card
	override val type: Int = R.layout.recycler_download_card
	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
}