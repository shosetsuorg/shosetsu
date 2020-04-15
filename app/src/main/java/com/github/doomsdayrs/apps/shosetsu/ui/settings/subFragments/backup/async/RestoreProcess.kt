package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.async

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Columns
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.sqLiteDatabase
import com.github.doomsdayrs.apps.shosetsu.backend.database.Tables
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

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
class RestoreProcess(private val file_path: String, @field:SuppressLint("StaticFieldLeak") private val context: Context) : AsyncTask<Void?, Void?, Boolean>() {
    @SuppressLint("StaticFieldLeak")
    private val close: Button
    @SuppressLint("StaticFieldLeak")
    private val progressBar: ProgressBar
    @SuppressLint("StaticFieldLeak")
    private val progressBar2: ProgressBar
    @SuppressLint("StaticFieldLeak")
    private val textView: TextView
    private val dialog: Dialog = Dialog(context)
    override fun onPreExecute() {
        Log.i("Progress", "Started restore")
        dialog.show()
    }

    override fun onPostExecute(b: Boolean) {
        if (b) {
            Log.i("Progress", "Completed restore")
            textView.post { textView.setText(R.string.completed) }
            progressBar2.post { progressBar2.visibility = View.GONE }
            close.post { close.setOnClickListener { dialog.cancel() } }
        } else {
            dialog.cancel()
            context.toast("Failed to process")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun doInBackground(vararg voids: Void?): Boolean {
        val file = File("" + file_path)
        if (file.exists()) {
            try {
                val bufferedReader = BufferedReader(FileReader(file))
                textView.post { textView.setText(R.string.reading_file) }
                val string = StringBuilder()
                run {
                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        string.append(line)
                    }
                    bufferedReader.close()
                }
                val backupJSON = JSONObject(string.substring(7))
                val novels = backupJSON.getJSONArray("novels")
                val chapters = backupJSON.getJSONArray("chapters")
                progressBar.post { progressBar.max = novels.length() + chapters.length() + 1 }
                Log.i("Progress", "Restoring novels")
                for (x in 0 until novels.length()) {
                    val novel = novels.getJSONObject(x)
                    val novelURL = novel.getString(Columns.URL.toString())
                    textView.post { textView.text = "Restoring: $novelURL" }
                    if (DatabaseNovels.isNotInNovels(novelURL)) {
                        DatabaseIdentification.addNovel(novelURL, novel.getInt(Columns.FORMATTER_ID.toString()))
                        val id = DatabaseIdentification.getNovelIDFromNovelURL(novelURL)
                        try {
                            sqLiteDatabase.execSQL("insert into " + Tables.NOVELS + "(" +
                                    Columns.PARENT_ID + "," +
                                    Columns.BOOKMARKED + "," +
                                    Columns.READING_STATUS + "," +
                                    Columns.READER_TYPE + "," +
                                    Columns.TITLE + "," +
                                    Columns.IMAGE_URL + "," +
                                    Columns.DESCRIPTION + "," +
                                    Columns.GENRES + "," +
                                    Columns.AUTHORS + "," +
                                    Columns.STATUS + "," +
                                    Columns.TAGS + "," +
                                    Columns.ARTISTS + "," +
                                    Columns.LANGUAGE + "," +
                                    Columns.MAX_CHAPTER_PAGE +
                                    ")" + "values" + "(" +
                                    id + "," +
                                    1 + "," +
                                    novel.getInt(Columns.READING_STATUS.toString()) + "," +
                                    novel.getInt(Columns.READER_TYPE.toString()) + "," +
                                    "'" + novel.getString(Columns.TITLE.toString()) + "'," +
                                    "'" + novel.getString(Columns.IMAGE_URL.toString()) + "'," +
                                    "'" + novel.getString(Columns.DESCRIPTION.toString()) + "'," +
                                    "'" + novel.getString(Columns.GENRES.toString()) + "'," +
                                    "'" + novel.getString(Columns.AUTHORS.toString()) + "'," +
                                    "'" + novel.getString(Columns.STATUS.toString()) + "'," +
                                    "'" + novel.getString(Columns.TAGS.toString()) + "'," +
                                    "'" + novel.getString(Columns.ARTISTS.toString()) + "'," +
                                    "'" + novel.getString(Columns.LANGUAGE.toString()) + "'," +
                                    novel.getInt(Columns.MAX_CHAPTER_PAGE.toString()) +
                                    ")")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " +
                                Columns.BOOKMARKED + "=1," +
                                Columns.READING_STATUS + "=" + novel[Columns.READING_STATUS.toString()] + "," +
                                Columns.READER_TYPE + "=" + novel[Columns.READER_TYPE.toString()] +
                                " where " + Columns.PARENT_ID + "=" + DatabaseIdentification.getNovelIDFromNovelURL(novelURL)
                        )
                    }
                    progressBar.post { progressBar.incrementProgressBy(1) }
                }
                Log.i("Progress", "Restoring chapters")
                for (x in 0 until chapters.length()) {
                    val chapter = chapters.getJSONObject(x)
                    val chapterURL = chapter.getString(Columns.URL.toString())
                    val novelURL = chapter.getString("novelURL")
                    textView.post { textView.text = "Restoring: $novelURL|$chapterURL" }
                    progressBar.post { progressBar.incrementProgressBy(1) }
                    if (!DatabaseChapter.isNotInChapters(chapterURL)) {
                        val novelID = DatabaseIdentification.getNovelIDFromNovelURL(novelURL)
                        DatabaseIdentification.addChapter(novelID, chapterURL)
                        val chapterID = DatabaseIdentification.getChapterIDFromChapterURL(chapterURL)
                        sqLiteDatabase.execSQL("insert into " + Tables.CHAPTERS +
                                "(" +
                                Columns.ID + "," +
                                Columns.PARENT_ID + "," +
                                Columns.TITLE + "," +
                                Columns.RELEASE_DATE + "," +
                                Columns.ORDER + "," +
                                Columns.Y_POSITION + "," +
                                Columns.READ_CHAPTER + "," +
                                Columns.BOOKMARKED + "," +
                                Columns.IS_SAVED +
                                ") " +
                                "values" +
                                "(" +
                                chapterID + "," +
                                novelID + ",'" +
                                chapter.getString(Columns.TITLE.toString()) + "','" +
                                chapter.getString(Columns.RELEASE_DATE.toString()) + "'," +
                                chapter.getInt(Columns.ORDER.toString()) + "," +
                                chapter.getInt(Columns.Y_POSITION.toString()) + "," +
                                chapter.getInt(Columns.READ_CHAPTER.toString()) + "," +
                                chapter.getInt(Columns.BOOKMARKED.toString()) + "," +
                                0 + ")")
                    } else {
                        sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " +
                                Columns.Y_POSITION + "=" + chapter.getString(Columns.Y_POSITION.toString()) + "," +
                                Columns.READ_CHAPTER + "=" + chapter.getString(Columns.READ_CHAPTER.toString()) + "," +
                                Columns.BOOKMARKED + "=" + chapter.getString(Columns.BOOKMARKED.toString()) +
                                " where " + Columns.ID + "=" + DatabaseIdentification.getChapterIDFromChapterURL(chapter.getString(Columns.URL.toString()))
                        )
                    }
                }
                textView.post { textView.text = "Restoring settings" }
                progressBar.post { progressBar.incrementProgressBy(1) }
                //TODO Settings
                progressBar.post { progressBar.incrementProgressBy(1) }
                return true
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return false
    }

    init {
        dialog.setContentView(R.layout.backup_restore_view)
        close = dialog.findViewById(R.id.button)
        progressBar = dialog.findViewById(R.id.progress)
        progressBar2 = dialog.findViewById(R.id.progressBar3)
        textView = dialog.findViewById(R.id.text)
    }
}