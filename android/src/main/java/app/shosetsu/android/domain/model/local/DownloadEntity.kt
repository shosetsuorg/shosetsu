package app.shosetsu.android.domain.model.local

import app.shosetsu.android.common.enums.DownloadStatus

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
 * 22 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */

data class DownloadEntity(
	val chapterID: Int,
	val novelID: Int,
	val chapterURL: String,
	val chapterName: String,
	val novelName: String,
	val extensionID: Int,
	var status: DownloadStatus = DownloadStatus.PENDING,
)