package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.async
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

import android.os.AsyncTask
import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.BACKUP_CHAPTERS
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.BACKUP_QUICK
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.BACKUP_SETTINGS
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.C_IN_NOVELS_H
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.C_IN_NOVELS_P
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.DISABLED_FORMATTERS
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.IS_DOWNLOAD_ON_UPDATE
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.IS_DOWNLOAD_PAUSED
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.NOVEL_CARD_TYPE
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.READER_BACK_C_COLOR
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.READER_IS_INVERTED_SWIPE
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.READER_IS_TAP_TO_SCROLL
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.READER_MARKING_TYPE
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.READER_TEXT_C_COLOR
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.READER_TEXT_INDENT
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.READER_TEXT_SIZE
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.READER_TEXT_SPACING
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.READER_THEME
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Columns
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.sqLiteDatabase
import com.github.doomsdayrs.apps.shosetsu.backend.database.Tables
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getInt
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getString
import com.github.doomsdayrs.apps.shosetsu.variables.ext.serializeToString
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toBoolean
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * shosetsu
 * 16 / 08 / 2019
 *
 * TODO Upgrade to a service
 * @author github.com/doomsdayrs
 */
@Suppress("unused")
class BackupProcess : AsyncTask<Void?, Void?, Boolean>() {
	override fun onPreExecute() {
		Log.i("Progress", "Starting backup")
	}

	override fun onPostExecute(success: Boolean) {
		if (success)
			Log.i("Progress", "Finished backup")
	}

	override fun doInBackground(vararg voids: Void?): Boolean {
		try {
			val backupJSON = JSONObject()

			run {
				Log.i("Progress", "Backing up novels")
				val backupNovels = JSONArray()
				val cursor = sqLiteDatabase.rawQuery("select * from " + Tables.NOVELS + " where " + Columns.BOOKMARKED + "=1", null)!!
				if (cursor.count > 0) while (cursor.moveToNext()) { // Gets if it is in library, if not then it skips
					val bookmarked = (cursor.getInt(cursor.getColumnIndex(Columns.BOOKMARKED.toString()))).toBoolean()
					Log.i("NovelBack", "Valid?: $bookmarked")
					if (bookmarked) {
						val novelURL = DatabaseIdentification.getNovelURLFromNovelID(cursor.getInt(Columns.PARENT_ID))!!
						val novel = JSONObject()
						novel[Columns.URL] = novelURL
						novel[Columns.FORMATTER_ID] = DatabaseIdentification.getFormatterIDFromNovelURL(novelURL)
						novel[Columns.READING_STATUS] = cursor.getInt(Columns.READING_STATUS)
						novel[Columns.READER_TYPE] = cursor.getInt(Columns.READER_TYPE)
						novel[Columns.TITLE] = cursor.getString(Columns.TITLE)
						novel[Columns.IMAGE_URL] = cursor.getString(Columns.IMAGE_URL)
						novel[Columns.DESCRIPTION] = cursor.getString(Columns.DESCRIPTION)
						novel[Columns.GENRES] = cursor.getString(Columns.GENRES)
						novel[Columns.AUTHORS] = cursor.getString(Columns.AUTHORS)
						novel[Columns.STATUS] = cursor.getString(Columns.STATUS)
						novel[Columns.TAGS] = cursor.getString(Columns.TAGS)
						novel[Columns.ARTISTS] = cursor.getString(Columns.ARTISTS)
						novel[Columns.LANGUAGE] = cursor.getString(Columns.LANGUAGE)
						novel[Columns.MAX_CHAPTER_PAGE] = cursor.getInt(Columns.MAX_CHAPTER_PAGE)
						backupNovels.put(novel)
					}
				}
				backupJSON.put("novels", backupNovels)
				cursor.close()
			}

			if (Settings.backupChapters && !Settings.backupQuick)
				run {
					Log.i("Progress", "Backing up Chapters")
					val backupChapters = JSONArray()
					val cursor = sqLiteDatabase.rawQuery("select * from " + Tables.CHAPTERS, null)!!
					if (cursor.count > 0) while (cursor.moveToNext()) {
						val novelID = cursor.getInt(cursor.getColumnIndex(Columns.PARENT_ID.toString()))
						val b = Database.DatabaseNovels.isBookmarked(novelID)
						if (b) {
							val id = cursor.getInt(cursor.getColumnIndex(Columns.ID.toString()))
							val chapter = JSONObject()
							chapter["novelURL"] = DatabaseIdentification.getNovelURLFromNovelID(novelID)
									?: ""
							chapter[Columns.URL] = DatabaseIdentification.getChapterURLFromChapterID(id)
							chapter[Columns.TITLE] = cursor.getString(Columns.TITLE)
							chapter[Columns.RELEASE_DATE] = cursor.getString(Columns.RELEASE_DATE)
							chapter[Columns.ORDER] = cursor.getInt(Columns.ORDER)
							chapter[Columns.Y_POSITION] = cursor.getInt(Columns.Y_POSITION)
							chapter[Columns.READ_CHAPTER] = cursor.getInt(Columns.READ_CHAPTER)
							chapter[Columns.BOOKMARKED] = cursor.getInt(Columns.BOOKMARKED)
							backupChapters.put(chapter)
						}
					}
					backupJSON.put("chapters", backupChapters)
					cursor.close()
				}
			else {

			}
			if (Settings.backupSettings)
				backupJSON.put("settings", getSettingsInJSON())

			Log.i("Progress", "Writing")
			val folder = File(Utilities.shoDir + "/backup/")
			if (!folder.exists()) if (!folder.mkdirs()) {
				throw IOException("Failed to mkdirs")
			}
			val fileOutputStream = FileOutputStream(
					folder.path + "/backup-" + Date().toString() + ".shoback"
			)
			fileOutputStream.write("JSON+-=$backupJSON".toByteArray())
			fileOutputStream.close()
			return true
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return false
	}

	/**
	 * Returns current settings in JSON format, Follows schema.json
	 *
	 * @return JSON of settings
	 * @throws JSONException EXCEPTION
	 * @throws IOException   EXCEPTION IN SERIALIZING
	 */
	@Throws(JSONException::class, IOException::class)
	fun getSettingsInJSON(): JSONObject {
		val settings = JSONObject()
		settings[READER_THEME] = Settings.readerTheme
		settings[READER_TEXT_C_COLOR] = Settings.readerCustomTextColor
		settings[READER_BACK_C_COLOR] = Settings.readerCustomBackColor

		settings[READER_TEXT_SIZE] = Settings.readerTextSize
		settings[READER_TEXT_SPACING] = Settings.readerParagraphSpacing
		settings[READER_TEXT_INDENT] = Settings.ReaderIndentSize

		settings[READER_IS_TAP_TO_SCROLL] = Settings.isTapToScroll
		settings[READER_IS_INVERTED_SWIPE] = Settings.isInvertedSwipe
		settings[READER_MARKING_TYPE] = Settings.readerMarkingType

		settings[IS_DOWNLOAD_PAUSED] = Settings.isDownloadPaused
		settings[IS_DOWNLOAD_ON_UPDATE] = Settings.isDownloadOnUpdateEnabled

		settings[DISABLED_FORMATTERS] = Settings.disabledFormatters

		settings[C_IN_NOVELS_P] = Settings.columnsInNovelsViewP
		settings[C_IN_NOVELS_H] = Settings.columnsInNovelsViewH
		settings[NOVEL_CARD_TYPE] = Settings.novelCardType

		settings[BACKUP_CHAPTERS] = Settings.backupChapters
		settings[BACKUP_SETTINGS] = Settings.backupSettings
		settings[BACKUP_QUICK] = Settings.backupQuick

		settings["shoDir"] = Utilities.shoDir.serializeToString()

		return settings
	}
}

@Throws(JSONException::class)
private operator fun JSONObject.set(key: String, value: Any) = put(key, value)

@Throws(JSONException::class)
private operator fun JSONObject.set(key: Columns, value: Any) = put(key.toString(), value)
