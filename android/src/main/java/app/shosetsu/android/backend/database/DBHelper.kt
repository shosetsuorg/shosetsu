@file:Suppress("DEPRECATION")

package app.shosetsu.android.backend.database

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import app.shosetsu.android.backend.database.DBHelper.Columns.*
import app.shosetsu.android.backend.database.DBHelper.Tables.*
import app.shosetsu.android.common.ext.deserializeString
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.providers.database.converters.NovelStatusConverter
import app.shosetsu.android.providers.database.dao.ChaptersDao
import app.shosetsu.android.providers.database.dao.NovelsDao
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.enums.ReadingStatus
import app.shosetsu.lib.Novel
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

import java.io.IOException

/*
* This file is part of Shosetsu.
*
* Shosetsu is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Shosetsu is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
*/

/**
 * Shosetsu
 * 14 / 06 / 2019
 */
@Deprecated("SQL Database removed")
class DBHelper(context: Context) :
	SQLiteOpenHelper(context, "database.db", null, 10), DIAware {
	override val di: DI by closestDI(context)
	private val novelDAO by di.instance<NovelsDao>()
	private val chapterDAO by instance<ChaptersDao>()

	private enum class Columns(private val key: String) {
		URL("url"),
		PARENT_ID("parent_id"),
		ID("id"),

		TITLE("title"),
		IMAGE_URL("image_url"),
		DESCRIPTION("description"),
		GENRES("genres"),
		AUTHORS("authors"),
		STATUS("status"),
		TAGS("tags"),
		ARTISTS("artists"),
		LANGUAGE("language"),
		MAX_CHAPTER_PAGE("max_chapter_page"),

		RELEASE_DATE("release_date"),
		ORDER("order_of"),

		FORMATTER_ID("formatterID"),
		READ_CHAPTER("read"),
		Y_POSITION("y"),
		BOOKMARKED("bookmarked"),
		;

		override fun toString(): String = key
	}

	private enum class Tables(private val key: String) {
		NOVEL_IDENTIFICATION("novel_identification"),
		CHAPTER_IDENTIFICATION("chapter_identification"),
		NOVELS("novels"),
		CHAPTERS("chapters"),

		UPDATES("updates"),

		DOWNLOADS("downloads"),
		;

		override fun toString(): String {
			return key
		}
	}

	/***/
	override fun onCreate(db: SQLiteDatabase) {}

	/***/
	@Throws(SQLException::class, IOException::class, ClassNotFoundException::class)
	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		if (oldVersion < 9) {
			db.execSQL("DROP TABLE IF EXISTS $CHAPTERS")
			db.execSQL("DROP TABLE IF EXISTS $NOVELS")
			@Suppress("DEPRECATION")
			db.execSQL("DROP TABLE IF EXISTS $DOWNLOADS")
			@Suppress("DEPRECATION")
			db.execSQL("DROP TABLE IF EXISTS $UPDATES")
		} else if (oldVersion < 10) {
			convert(db)
		}
	}

	@Throws(IOException::class, ClassNotFoundException::class)
	private fun convert(db: SQLiteDatabase) {

		val novelIDS: List<NovelIdentification> =
			db.rawQuery("SELECT * FROM $NOVEL_IDENTIFICATION", null).let {
				ArrayList<NovelIdentification>().apply {
					while (it.moveToNext()) add(
						NovelIdentification(
							novelID = it.getInt(ID),
							novelURL = it.getString(URL),
							extensionID = it.getInt(FORMATTER_ID)
						)
					)
				}
			}

		val chapterIDS: List<ChapterIdentification> =
			db.rawQuery("SELECT * FROM $CHAPTER_IDENTIFICATION", null).let {
				ArrayList<ChapterIdentification>().apply {
					while (it.moveToNext()) add(
						ChapterIdentification(
							chapterID = it.getInt(ID),
							novelID = it.getInt(PARENT_ID),
							chapterURL = it.getString(URL)
						)
					)
				}
			}

		val novels: List<NovelEntity> =
			db.rawQuery("SELECT * FROM $NOVELS", null).let {
				ArrayList<OldNovelEntity>().apply {
					while (it.moveToNext()) add(
						OldNovelEntity(
							novelID = it.getInt(PARENT_ID),
							bookmarked = it.getInt(BOOKMARKED) == 1,
							title = it.getString(TITLE).deserializeString() ?: "",
							imageURL = it.getString(IMAGE_URL),
							description = it.getString(DESCRIPTION).deserializeString() ?: "",
							language = it.getString(LANGUAGE).deserializeString() ?: "",
							maxChapter = it.getInt(MAX_CHAPTER_PAGE),
							publishingStatus = it.getInt(STATUS).let {
								NovelStatusConverter().toStatus(it)
							},
							authors = it.getString(AUTHORS)
								.deserializeString<String>()?.convertStringToArray()
								?: listOf(),
							genres = it.getString(GENRES)
								.deserializeString<String>()?.convertStringToArray()
								?: listOf(),
							tags = it.getString(TAGS)
								.deserializeString<String>()?.convertStringToArray()
								?: listOf(),
							artists = it.getString(ARTISTS)
								.deserializeString<String>()?.convertStringToArray()
								?: listOf()
						)
					)
				}
			}
				.map { (novelID, bookmarked, title, imageURL, description, genres, authors, tags, publishingStatus, artists, language, _) ->
					val novelIDF = novelIDS.find { it.novelID == novelID }!!

					NovelEntity(
						id = novelID,
						url = novelIDF.novelURL,
						extensionID = novelIDF.extensionID,
						bookmarked = bookmarked,
						loaded = true,
						title = title,
						imageURL = imageURL,
						description = description,
						language = language,
						genres = genres.toList(),
						authors = authors.toList(),
						artists = artists.toList(),
						tags = tags.toList(),
						status = publishingStatus,
					)
				}

		val chapters: List<ChapterEntity> =
			db.rawQuery("SELECT * FROM $CHAPTERS", null).let {
				ArrayList<OldChapterEntity>().apply {
					while (it.moveToNext()) add(
						OldChapterEntity(
							chapterID = it.getInt(ID),
							novelID = it.getInt(PARENT_ID),
							title = it.getString(TITLE).deserializeString() ?: "",
							date = it.getString(RELEASE_DATE).deserializeString() ?: "",
							order = it.getDouble(ORDER),
							yPosition = it.getInt(Y_POSITION),
							readChapter = it.getInt(READ_CHAPTER).let {
								ReadingStatus.fromInt(it)
							},
							bookmarked = it.getInt(BOOKMARKED) == 1
						)
					)
				}
			}.map { (chapterID, novelID, title, date, order, yPosition, readChapter, bookmarked) ->
				val chapterIDF = chapterIDS.find { it.chapterID == chapterID }!!
				val novelIDF = novelIDS.find { it.novelID == novelID }!!

				ChapterEntity(
					id = chapterID,
					novelID = novelID,
					url = chapterIDF.chapterURL,
					extensionID = novelIDF.extensionID,
					title = title,
					releaseDate = date,
					order = order,
					readingPosition = yPosition.toDouble(),
					readingStatus = readChapter,
					bookmarked = bookmarked
				)
			}

		launchIO {
			try {
				novelDAO.insertAllIgnore(novels.toDB())
				chapterDAO.insertAllIgnore(chapters.toDB())
				Log.d(logID(), "Finished insert, Deleting tables")

				db.execSQL("DROP TABLE IF EXISTS $CHAPTER_IDENTIFICATION")
				db.execSQL("DROP TABLE IF EXISTS $NOVEL_IDENTIFICATION")
				db.execSQL("DROP TABLE IF EXISTS $CHAPTERS")
				db.execSQL("DROP TABLE IF EXISTS $NOVELS")
				db.execSQL("DROP TABLE IF EXISTS $DOWNLOADS")
				db.execSQL("DROP TABLE IF EXISTS $UPDATES")
			} catch (e: SQLException) {
				e.printStackTrace()
			}
		}
	}

	private data class NovelIdentification(
		val novelID: Int,
		val novelURL: String,
		val extensionID: Int,
	)

	private data class ChapterIdentification(
		val chapterID: Int,
		val novelID: Int,
		val chapterURL: String,
	)

	private data class OldChapterEntity(
		val chapterID: Int,
		val novelID: Int,
		val title: String,
		val date: String,
		val order: Double,
		val yPosition: Int,
		val readChapter: ReadingStatus,
		val bookmarked: Boolean,
	)

	private data class OldNovelEntity(
		val novelID: Int,
		val bookmarked: Boolean,
		val title: String,
		val imageURL: String,
		val description: String,
		val genres: List<String>,
		val authors: List<String>,
		val tags: List<String>,
		val publishingStatus: Novel.Status,
		val artists: List<String>,
		val language: String,
		val maxChapter: Int,
	)

	/**
	 * This is an old method that once was used to store arrays into the database
	 *
	 * Converts a String Array back into an Array of Strings
	 *
	 * @return Array of Strings
	 */
	private fun String.convertStringToArray(): List<String> {
		val a = this.substring(1, this.length - 1).split(", ".toRegex()).toTypedArray()
		for (x in a.indices) {
			a[x] = a[x].replace(">,<", ",")
		}
		return a.toList()
	}

	private fun Cursor.getString(column: Columns): String =
		getString(getColumnIndex(column.toString()))

	private fun Cursor.getInt(column: Columns): Int =
		getInt(getColumnIndex(column.toString()))

	private fun Cursor.getDouble(column: Columns): Double =
		getDouble(getColumnIndex(column.toString()))

}