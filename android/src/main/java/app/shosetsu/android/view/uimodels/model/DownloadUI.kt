package app.shosetsu.android.view.uimodels.model

import androidx.compose.runtime.Immutable
import app.shosetsu.android.common.enums.DownloadStatus
import app.shosetsu.android.domain.model.local.DownloadEntity
import app.shosetsu.android.dto.Convertible

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
@Immutable
data class DownloadUI(
	val chapterID: Int,
	val novelID: Int,
	val chapterURL: String,
	val chapterName: String,
	val novelName: String,
	val extensionID: Int,
	val status: DownloadStatus = DownloadStatus.PENDING,
	val isSelected: Boolean = false
) : Convertible<DownloadEntity> {

	override fun convertTo(): DownloadEntity = DownloadEntity(
		chapterID,
		novelID,
		chapterURL,
		chapterName,
		novelName,
		extensionID,
		status
	)
}