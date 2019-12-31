package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.async

import android.os.AsyncTask
import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.backend.Serialize
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

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
 */ /**
 * shosetsu
 * 16 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class BackupProcess : AsyncTask<Void?, Void?, Void?>() {
    override fun onPreExecute() {
        Log.i("Progress", "Starting backup")
    }

    override fun onPostExecute(aVoid: Void?) {
        Log.i("Progress", "Finished backup")
    }

    protected override fun doInBackground(vararg voids: Void?): Void? {
        try {
            val BACKUP = JSONObject()
            Log.i("Progress", "Backing up novels")
            run {
                val NOVELS = JSONArray()
                val cursor = sqLiteDatabase.rawQuery("select * from " + Tables.NOVELS + " where " + Columns.BOOKMARKED + "=1", null)
                if (cursor.count > 0) while (cursor.moveToNext()) { // Gets if it is in library, if not then it skips
                    val bookmarked = Utilities.intToBoolean(cursor.getInt(cursor.getColumnIndex(Columns.BOOKMARKED.toString())))
                    Log.i("NovelBack", "Valid?: $bookmarked")
                    if (bookmarked) {
                        val novelURL = DatabaseIdentification.getNovelURLfromNovelID(cursor.getInt(cursor.getColumnIndex(Columns.PARENT_ID.toString())))
                        val novel = JSONObject()
                        novel.put(Columns.URL.toString(), novelURL)
                        novel.put(Columns.FORMATTER_ID.toString(), DatabaseIdentification.getFormatterIDFromNovelURL(novelURL))
                        novel.put(Columns.READING_STATUS.toString(), cursor.getInt(cursor.getColumnIndex(Columns.READING_STATUS.toString())))
                        novel.put(Columns.READER_TYPE.toString(), cursor.getInt(cursor.getColumnIndex(Columns.READER_TYPE.toString())))
                        novel.put(Columns.TITLE.toString(), cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString())))
                        novel.put(Columns.IMAGE_URL.toString(), cursor.getString(cursor.getColumnIndex(Columns.IMAGE_URL.toString())))
                        novel.put(Columns.DESCRIPTION.toString(), cursor.getString(cursor.getColumnIndex(Columns.DESCRIPTION.toString())))
                        novel.put(Columns.GENRES.toString(), cursor.getString(cursor.getColumnIndex(Columns.GENRES.toString())))
                        novel.put(Columns.AUTHORS.toString(), cursor.getString(cursor.getColumnIndex(Columns.AUTHORS.toString())))
                        novel.put(Columns.STATUS.toString(), cursor.getString(cursor.getColumnIndex(Columns.STATUS.toString())))
                        novel.put(Columns.TAGS.toString(), cursor.getString(cursor.getColumnIndex(Columns.TAGS.toString())))
                        novel.put(Columns.ARTISTS.toString(), cursor.getString(cursor.getColumnIndex(Columns.ARTISTS.toString())))
                        novel.put(Columns.LANGUAGE.toString(), cursor.getString(cursor.getColumnIndex(Columns.LANGUAGE.toString())))
                        novel.put(Columns.MAX_CHAPTER_PAGE.toString(), cursor.getInt(cursor.getColumnIndex(Columns.MAX_CHAPTER_PAGE.toString())))
                        NOVELS.put(novel)
                    }
                }
                BACKUP.put("novels", NOVELS)
                cursor.close()
            }
            Log.i("Progress", "Backing up Chapters")
            run {
                val CHAPTERS = JSONArray()
                val cursor = sqLiteDatabase.rawQuery("select * from " + Tables.CHAPTERS, null)
                if (cursor.count > 0) while (cursor.moveToNext()) {
                    val novelID = cursor.getInt(cursor.getColumnIndex(Columns.PARENT_ID.toString()))
                    val b = DatabaseNovels.isBookmarked(novelID)
                    if (b) {
                        val id = cursor.getInt(cursor.getColumnIndex(Columns.ID.toString()))
                        val chapter = JSONObject()
                        chapter.put("novelURL", DatabaseIdentification.getNovelURLfromNovelID(novelID))
                        chapter.put(Columns.URL.toString(), DatabaseIdentification.getChapterURLFromChapterID(id))
                        chapter.put(Columns.TITLE.toString(), cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString())))
                        chapter.put(Columns.RELEASE_DATE.toString(), cursor.getString(cursor.getColumnIndex(Columns.RELEASE_DATE.toString())))
                        chapter.put(Columns.ORDER.toString(), cursor.getInt(cursor.getColumnIndex(Columns.ORDER.toString())))
                        chapter.put(Columns.Y.toString(), cursor.getInt(cursor.getColumnIndex(Columns.Y.toString())))
                        chapter.put(Columns.READ_CHAPTER.toString(), cursor.getInt(cursor.getColumnIndex(Columns.READ_CHAPTER.toString())))
                        chapter.put(Columns.BOOKMARKED.toString(), cursor.getInt(cursor.getColumnIndex(Columns.BOOKMARKED.toString())))
                        CHAPTERS.put(chapter)
                    }
                }
                BACKUP.put("chapters", CHAPTERS)
                cursor.close()
            }
            BACKUP.put("settings", Serialize.getSettingsInJSON())
            Log.i("Progress", "Writing")
            val folder = File(Utilities.shoDir + "/backup/")
            if (!folder.exists()) if (!folder.mkdirs()) {
                throw IOException("Failed to mkdirs")
            }
            val fileOutputStream = FileOutputStream(
                    folder.path + "/backup-" + Date().toString() + ".shoback"
            )
            fileOutputStream.write("JSON+-=$BACKUP".toByteArray())
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }
}