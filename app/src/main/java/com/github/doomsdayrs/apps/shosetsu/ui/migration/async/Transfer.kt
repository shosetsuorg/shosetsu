package com.github.doomsdayrs.apps.shosetsu.ui.migration.async

import android.os.AsyncTask
import android.view.View
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers.getByID
import kotlinx.android.synthetic.main.migrate_source_view.*
import java.util.*

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
 * ====================================================================
 * shosetsu
 * 05 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class Transfer(private val strings: ArrayList<Array<String>>, target: Int, private val migrationView: MigrationView?) : AsyncTask<Void?, Void?, Void?>() {
    private val formatter: Formatter? = getByID(target)
    var isNotCanceled = true


    override fun onCancelled() {
        isNotCanceled = false
        super.onCancelled()
    }

    override fun onPreExecute() {
        migrationView!!.migrating.visibility = View.GONE
        migrationView.progress.visibility = View.VISIBLE
        migrationView.console_output.post { migrationView.console_output.text = migrationView.resources.getText(R.string.starting) }
    }

    override fun doInBackground(vararg voids: Void?): Void? {
        for (strings in strings) if (isNotCanceled) {
            val s = strings[0] + "--->" + strings[1]
            println(s)
            migrationView!!.console_output.post { migrationView.console_output.text = s }
            val novelPage = formatter!!.parseNovel(strings[1], true){}
            var mangaCount = 0
            val novelID = DatabaseIdentification.getNovelIDFromNovelURL(strings[1])
            for (novelChapter in novelPage.chapters) if (isNotCanceled && !Database.DatabaseChapter.isNotInChapters(novelChapter.link)) {
                mangaCount++
                println("Adding #" + mangaCount + ": " + novelChapter.link)
                Database.DatabaseChapter.addToChapters(novelID, novelChapter)
            }
            if (isNotCanceled) {
                migrationView.page_count.post { migrationView.page_count.text = "" }
                val oldID = DatabaseIdentification.getNovelIDFromNovelURL(strings[0])
                Database.DatabaseNovels.migrateNovel(oldID, strings[1], formatter.formatterID, novelPage, Database.DatabaseNovels.getStatus(oldID).a)
            }
        }
        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        migrationView?.finish()
    }

}