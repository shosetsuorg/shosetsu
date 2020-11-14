package app.shosetsu.android.domain.model.local

import androidx.annotation.NonNull
import androidx.room.*
import app.shosetsu.android.common.dto.Convertible
import app.shosetsu.android.common.enums.DownloadStatus
import app.shosetsu.android.view.uimodels.model.DownloadUI
import java.io.Serializable

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
@Entity(tableName = "downloads",
		foreignKeys = [
			ForeignKey(
					entity = ChapterEntity::class,
					parentColumns = ["id"],
					childColumns = ["chapterID"],
					onDelete = ForeignKey.CASCADE
			),
			ForeignKey(
					entity = NovelEntity::class,
					parentColumns = ["id"],
					childColumns = ["novelID"],
					onDelete = ForeignKey.CASCADE
			),
			ForeignKey(
					entity = ChapterEntity::class,
					parentColumns = ["url"],
					childColumns = ["chapterURL"],
					onDelete = ForeignKey.CASCADE
			)
		],
		indices = [
			Index("chapterID"),
			Index("novelID"),
			Index("chapterURL")
		]
)
data class DownloadEntity(
		@PrimaryKey
		val chapterID: Int,
		val novelID: Int,
		val chapterURL: String,
		val chapterName: String,
		val novelName: String,
		@NonNull
		@ColumnInfo(name = "formatterID")
		val extensionID: Int,
		var status: DownloadStatus = DownloadStatus.PENDING,
) : Convertible<DownloadUI>, Serializable {
	override fun convertTo(): DownloadUI =
			DownloadUI(
					chapterID,
					novelID,
					chapterURL,
					chapterName,
					novelName,
					extensionID,
					status
			)
}