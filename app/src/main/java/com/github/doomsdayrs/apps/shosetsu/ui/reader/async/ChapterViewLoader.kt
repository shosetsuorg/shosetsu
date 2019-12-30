package com.github.doomsdayrs.apps.shosetsu.ui.reader.async

import android.app.Activity
import android.os.AsyncTask
import android.view.View.GONE
import android.view.View.VISIBLE
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper
import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.ChapterView
import kotlinx.android.synthetic.main.chapter_view.*
import kotlinx.android.synthetic.main.network_error.*

class ChapterViewLoader(private val chapterView: ChapterView) : AsyncTask<Any?, Void?, Void?>() {

    override fun onPreExecute() {
        chapterView.progress.visibility = VISIBLE
        chapterView.network_error.visibility = GONE
    }

    override fun doInBackground(vararg objects: Any?): Void? {
        try {
            chapterView.chapterReader?.formatter?.let { formatter: Formatter ->
                {
                    WebViewScrapper.docFromURL(chapterView.url, formatter.hasCloudFlare)?.let {
                        chapterView.unformattedText = formatter.getNovelPassage(it)
                    }
                }
            }
        } catch (e: Exception) {
            chapterView.activity?.runOnUiThread {
                chapterView.network_error.visibility = VISIBLE
                chapterView.error_message.text = e.message
                chapterView.error_button.setOnClickListener { ChapterViewLoader(chapterView).execute() }
            }
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        chapterView.progress.visibility = GONE
        chapterView.setUpReader()
        chapterView.scrollView?.let { it.post { chapterView.scrollView!!.scrollTo(0, DatabaseChapter.getY(chapterView.chapterID)) } }
        chapterView.ready = true
    }

}