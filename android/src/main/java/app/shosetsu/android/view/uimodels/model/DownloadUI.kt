package app.shosetsu.android.view.uimodels.model

import android.view.View
import app.shosetsu.android.common.consts.SELECTED_STROKE_WIDTH
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.BindViewHolder
import app.shosetsu.common.domain.model.local.DownloadEntity
import app.shosetsu.common.dto.Convertible
import app.shosetsu.common.enums.DownloadStatus
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.RecyclerDownloadCardBinding

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
	val extensionID: Int,
	var status: DownloadStatus = DownloadStatus.PENDING,
) : BaseRecyclerItem<DownloadUI.ViewHolder>(), Convertible<DownloadEntity> {
	override var identifier: Long
		get() = chapterID.toLong()
		set(_) {}

	override fun convertTo(): DownloadEntity = DownloadEntity(
		chapterID,
		novelID,
		chapterURL,
		chapterName,
		novelName,
		extensionID,
		status
	)

	class ViewHolder(itemView: View) :
		BindViewHolder<DownloadUI, RecyclerDownloadCardBinding>(itemView) {
		override val binding = RecyclerDownloadCardBinding.bind(itemView)

		override fun RecyclerDownloadCardBinding.bindView(item: DownloadUI, payloads: List<Any>) {
			novelTitle.text = item.novelName
			chapterTitle.text = item.chapterName
			status.setText(
				when (item.status) {
					DownloadStatus.PENDING -> {
						progress.isIndeterminate = false
						R.string.pending
					}
					DownloadStatus.DOWNLOADING -> {
						progress.isIndeterminate = true
						R.string.downloading
					}
					DownloadStatus.PAUSED -> {
						progress.isIndeterminate = false
						R.string.paused
					}
					DownloadStatus.ERROR -> {
						progress.isIndeterminate = false
						R.string.error
					}
					DownloadStatus.WAITING -> {
						progress.isIndeterminate = true
						R.string.waiting
					}
					else ->{
						progress.isIndeterminate = false
						R.string.completed
					}
				}
			)
			cardView.strokeWidth = if (item.isSelected) SELECTED_STROKE_WIDTH else 0
			if (item.isSelected) cardView.isSelected
		}

		override fun RecyclerDownloadCardBinding.unbindView(item: DownloadUI) {
			novelTitle.text = null
			chapterTitle.text = null
			status.text = null
			progress.isIndeterminate = false
		}
	}

	override val layoutRes: Int = R.layout.recycler_download_card
	override val type: Int = R.layout.recycler_download_card
	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
}