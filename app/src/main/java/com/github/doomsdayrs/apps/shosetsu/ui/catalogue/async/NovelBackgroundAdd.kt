package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async

import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.NovelCardViewHolder
import kotlinx.android.synthetic.main.fragment_catalogue.*

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
 */
/**
 * shosetsu
 * 06 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class NovelBackgroundAdd(private val novelCardsViewHolder: NovelCardViewHolder?) : AsyncTask<View?, Void, Void>() {
    override fun doInBackground(vararg views: View?): Void? {
        try {
            if (novelCardsViewHolder != null && Database.DatabaseNovels.isNotInNovels(novelCardsViewHolder.url)) {
                Database.DatabaseNovels.addToLibrary(novelCardsViewHolder.formatter.formatterID, novelCardsViewHolder.formatter.parseNovel(WebViewScrapper.docFromURL(novelCardsViewHolder.url, novelCardsViewHolder.formatter.hasCloudFlare)!!), novelCardsViewHolder.url, com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD.a)
                views[0]?.post { Toast.makeText(views[0]!!.context, "Added " + novelCardsViewHolder.title.text.toString(), Toast.LENGTH_SHORT).show() }
            }
            if (novelCardsViewHolder != null && Database.DatabaseNovels.isNotBookmarked(novelCardsViewHolder.novelID)) {
                views[0]?.post { Toast.makeText(views[0]!!.context, "Already in the library", Toast.LENGTH_SHORT).show() }
            } else {
                if (novelCardsViewHolder != null) {
                    Database.DatabaseNovels.bookMark(Database.DatabaseIdentification.getNovelIDFromNovelURL(novelCardsViewHolder.url))
                    views[0]?.post { Toast.makeText(views[0]!!.context, "Added " + novelCardsViewHolder.title.text.toString(), Toast.LENGTH_SHORT).show() }
                }
            }
        } catch (e: Exception) {
            if (novelCardsViewHolder != null) {
                views[0]?.post { Toast.makeText(views[0]!!.context, "Failed to add to library: " + novelCardsViewHolder.title.text.toString(), Toast.LENGTH_LONG).show() }
                views[0]?.post { Log.e("NovelBackgroundAdd", novelCardsViewHolder.title.text.toString() + " : " + e.message) }
            }
        }
        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        novelCardsViewHolder?.catalogueFragment?.recyclerView?.post {
            novelCardsViewHolder.catalogueFragment?.catalogueAdapter?.notifyDataSetChanged()
        }
    }

}