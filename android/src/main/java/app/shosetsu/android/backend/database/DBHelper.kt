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
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.ext.deserializeString
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.NovelEntity
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
			db.rawQuery("SELECT * FROM $NOVEL_IDENTIFICATION", null).let { cursor ->
				ArrayList<NovelIdentification>().apply {
					while (cursor.moveToNext()) add(
						NovelIdentification(
							novelID = cursor.getInt(ID),
							novelURL = cursor.getString(URL),
							extensionID = cursor.getInt(FORMATTER_ID)
						)
					)
				}.also {
					cursor.close()
				}
			}

		val chapterIDS: List<ChapterIdentification> =
			db.rawQuery("SELECT * FROM $CHAPTER_IDENTIFICATION", null)
				.let { cursor ->
					ArrayList<ChapterIdentification>().apply {
						while (cursor.moveToNext()) add(
							ChapterIdentification(
								chapterID = cursor.getInt(ID),
								novelID = cursor.getInt(PARENT_ID),
								chapterURL = cursor.getString(URL)
							)
						)
					}.also {
						cursor.close()
					}
				}

		val novels: List<NovelEntity> =
			db.rawQuery("SELECT * FROM $NOVELS", null)
				.let { cursor ->
					ArrayList<OldNovelEntity>().apply {
						while (cursor.moveToNext()) add(
							OldNovelEntity(
								novelID = cursor.getInt(PARENT_ID),
								bookmarked = cursor.getInt(BOOKMARKED) == 1,
								title = cursor.getString(TITLE).deserializeString() ?: "",
								imageURL = cursor.getString(IMAGE_URL),
								description = cursor.getString(DESCRIPTION).deserializeString()
									?: "",
								language = cursor.getString(LANGUAGE).deserializeString() ?: "",
								maxChapter = cursor.getInt(MAX_CHAPTER_PAGE),
								publishingStatus = cursor.getInt(STATUS).let {
									NovelStatusConverter().toStatus(it)
								},
								authors = cursor.getString(AUTHORS)
									.deserializeString<String>()?.convertStringToArray()
									?: listOf(),
								genres = cursor.getString(GENRES)
									.deserializeString<String>()?.convertStringToArray()
									?: listOf(),
								tags = cursor.getString(TAGS)
									.deserializeString<String>()?.convertStringToArray()
									?: listOf(),
								artists = cursor.getString(ARTISTS)
									.deserializeString<String>()?.convertStringToArray()
									?: listOf()
							)
						)
					}.also {
						cursor.close()
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
			db.rawQuery("SELECT * FROM $CHAPTERS", null).let { cursor ->
				ArrayList<OldChapterEntity>().apply {
					while (cursor.moveToNext()) add(
						OldChapterEntity(
							chapterID = cursor.getInt(ID),
							novelID = cursor.getInt(PARENT_ID),
							title = cursor.getString(TITLE).deserializeString() ?: "",
							date = cursor.getString(RELEASE_DATE).deserializeString() ?: "",
							order = cursor.getDouble(ORDER),
							yPosition = cursor.getInt(Y_POSITION),
							readChapter = cursor.getInt(READ_CHAPTER).let {
								ReadingStatus.fromInt(it)
							},
							bookmarked = cursor.getInt(BOOKMARKED) == 1
						)
					)
				}.also {
					cursor.close()
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
		getColumnIndex(column.toString()).takeIf { it >= 0 }?.let { getString(it) }!!

	private fun Cursor.getInt(column: Columns): Int =
		getColumnIndex(column.toString()).takeIf { it >= 0 }?.let { getInt(it) }!!


	private fun Cursor.getDouble(column: Columns): Double =
		getColumnIndex(column.toString()).takeIf { it >= 0 }?.let { getDouble(it) }!!


}