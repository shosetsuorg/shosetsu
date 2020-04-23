package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async

import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.NovelListingViewHolder
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast

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
class NovelBackgroundAdd(private val novelCardsViewHolder: NovelListingViewHolder?) : AsyncTask<View?, Void, Void>() {
    override fun doInBackground(vararg views: View?): Void? {
        try {
            if (novelCardsViewHolder != null && Database.DatabaseNovels.isNotInNovels(novelCardsViewHolder.url!!)) {
                Database.DatabaseNovels.addNovelToDatabase(novelCardsViewHolder.formatter.formatterID,
                        novelCardsViewHolder.formatter.parseNovel(novelCardsViewHolder.url!!, false) {},
                        novelCardsViewHolder.url!!,
                        com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD.a)
                views[0]?.post {
                    views[0]?.context?.toast("Added ${novelCardsViewHolder.title.text}")
                }
            }
            if (novelCardsViewHolder != null && Database.DatabaseNovels.isNovelBookmarked(novelCardsViewHolder.novelID)) {
                views[0]?.post {
                    views[0]?.context?.toast("Already in the library")
                }
            } else {
                if (novelCardsViewHolder != null) {
                    Database.DatabaseNovels.bookmarkNovel(Database.DatabaseIdentification.getNovelIDFromNovelURL(novelCardsViewHolder.url!!))
                    views[0]?.post {
                        views[0]?.context?.toast("Added ${novelCardsViewHolder.title.text}")
                    }
                }
            }
        } catch (e: Exception) {
            if (novelCardsViewHolder != null) {
                views[0]?.post {
                    views[0]?.context?.toast("Failed to add to library: ${novelCardsViewHolder.title.text}")
                }
                views[0]?.post { Log.e("NovelBackgroundAdd", novelCardsViewHolder.title.text.toString() + " : " + e.message) }
            }
        }
        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        novelCardsViewHolder?.catalogueFragment?.recyclerView?.post {
            novelCardsViewHolder.catalogueFragment.catalogueAdapter.notifyDataSetChanged()
        }
    }

}