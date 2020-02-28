package com.github.doomsdayrs.apps.shosetsu.ui.reader.async

import android.content.DialogInterface
import android.os.AsyncTask
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.errorView.ErrorAlert
import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.ChapterView
import java.util.concurrent.TimeUnit

class ChapterViewLoader(private val chapterView: ChapterView) : AsyncTask<Any?, Void?, Boolean>() {

    override fun onPreExecute() {
        Log.i("ChapterViewLoader", "onPreExecute${chapterView.appendID()}")
        chapterView.progress?.visibility = VISIBLE
    }

    override fun doInBackground(vararg objects: Any?): Boolean {
        Log.i("ChapterViewLoader", "doInBackground${chapterView.appendID()}")
        if (chapterView.chapterReader?.formatter == null) return false
        try {
            TimeUnit.SECONDS.sleep(5)
            chapterView.unformattedText = chapterView.chapterReader?.formatter!!.getPassage(chapterView.url)
            return true
        } catch (e: Exception) {
            chapterView.activity?.runOnUiThread {
                ErrorAlert(chapterView.activity!!) { dialog: DialogInterface?, _: Int -> ChapterViewLoader(chapterView).execute();dialog?.dismiss() }
                        .setMessage(e.message)
                        .show()
            }
        }
        return false
    }

    override fun onPostExecute(result: Boolean) {
        Log.i("ChapterViewLoader", "onPostExecute${chapterView.appendID()}")
        chapterView.view?.post {
            chapterView.progress?.visibility = GONE
            chapterView.setUpReader()
            chapterView.scrollView?.post { chapterView.scrollView?.scrollTo(0, Database.DatabaseChapter.getY(chapterView.chapterID)) }
            chapterView.ready = true
        }
    }
}

