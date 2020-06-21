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
 */

import android.os.AsyncTask
import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.common.Settings.BACKUP_CHAPTERS
import com.github.doomsdayrs.apps.shosetsu.common.Settings.BACKUP_QUICK
import com.github.doomsdayrs.apps.shosetsu.common.Settings.BACKUP_SETTINGS
import com.github.doomsdayrs.apps.shosetsu.common.Settings.C_IN_NOVELS_H
import com.github.doomsdayrs.apps.shosetsu.common.Settings.C_IN_NOVELS_P
import com.github.doomsdayrs.apps.shosetsu.common.Settings.ONLY_UPDATE_ONGOING
import com.github.doomsdayrs.apps.shosetsu.common.Settings.IS_DOWNLOAD_PAUSED
import com.github.doomsdayrs.apps.shosetsu.common.Settings.NOVEL_CARD_TYPE
import com.github.doomsdayrs.apps.shosetsu.common.Settings.READER_BACK_C_COLOR
import com.github.doomsdayrs.apps.shosetsu.common.Settings.READER_IS_INVERTED_SWIPE
import com.github.doomsdayrs.apps.shosetsu.common.Settings.READER_IS_TAP_TO_SCROLL
import com.github.doomsdayrs.apps.shosetsu.common.Settings.READER_MARKING_TYPE
import com.github.doomsdayrs.apps.shosetsu.common.Settings.READER_TEXT_C_COLOR
import com.github.doomsdayrs.apps.shosetsu.common.Settings.READER_TEXT_INDENT
import com.github.doomsdayrs.apps.shosetsu.common.Settings.READER_TEXT_SIZE
import com.github.doomsdayrs.apps.shosetsu.common.Settings.READER_TEXT_SPACING
import com.github.doomsdayrs.apps.shosetsu.common.Settings.READER_THEME
import com.github.doomsdayrs.apps.shosetsu.backend.database.Columns
import com.github.doomsdayrs.apps.shosetsu.backend.shoDir
import com.github.doomsdayrs.apps.shosetsu.common.ext.serializeToString
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * shosetsu
 * 16 / 08 / 2019
 * TODO Upgrade to a service
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
			}

			if (Settings.backupChapters && !Settings.backupQuick)
			else { }
			if (Settings.backupSettings)
				backupJSON.put("settings", getSettingsInJSON())

			Log.i("Progress", "Writing")
			val folder = File(shoDir + "/backup/")
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
		settings[READER_TEXT_INDENT] = Settings.readerIndentSize

		settings[READER_IS_TAP_TO_SCROLL] = Settings.isTapToScroll
		settings[READER_IS_INVERTED_SWIPE] = Settings.isInvertedSwipe
		settings[READER_MARKING_TYPE] = Settings.readerMarkingType

		settings[IS_DOWNLOAD_PAUSED] = Settings.isDownloadPaused
		settings[ONLY_UPDATE_ONGOING] = Settings.downloadOnUpdate

		settings[C_IN_NOVELS_P] = Settings.columnsInNovelsViewP
		settings[C_IN_NOVELS_H] = Settings.columnsInNovelsViewH
		settings[NOVEL_CARD_TYPE] = Settings.novelCardType

		settings[BACKUP_CHAPTERS] = Settings.backupChapters
		settings[BACKUP_SETTINGS] = Settings.backupSettings
		settings[BACKUP_QUICK] = Settings.backupQuick

		settings["shoDir"] = shoDir.serializeToString()

		return settings
	}
}

@Throws(JSONException::class)
private operator fun JSONObject.set(key: String, value: Any) = put(key, value)

@Throws(JSONException::class)
private operator fun JSONObject.set(key: Columns, value: Any) = put(key.toString(), value)
