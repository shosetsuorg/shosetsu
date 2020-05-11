package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async

import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.CListingViewHolder

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
class NovelBackgroundAdd(private val cCardsViewHolder: CListingViewHolder?) : AsyncTask<View?, Void, Void>() {
	override fun doInBackground(vararg views: View?): Void? {
		try {
			if (cCardsViewHolder != null && Database.DatabaseNovels.isNotInNovels(cCardsViewHolder.url!!)) {
				Database.DatabaseNovels.addNovelToDatabase(cCardsViewHolder.formatter.formatterID,
						cCardsViewHolder.formatter.parseNovel(cCardsViewHolder.url!!, false) {},
						cCardsViewHolder.url!!,
						com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus.UNREAD.a)
				views[0]?.post {
					views[0]?.context?.toastOnUI("Added ${cCardsViewHolder.title.text}")
				}
			}
			if (cCardsViewHolder != null && Database.DatabaseNovels.isNovelBookmarked(cCardsViewHolder.novelID)) {
				views[0]?.post {
					views[0]?.context?.toastOnUI("Already in the library")
				}
			} else {
				if (cCardsViewHolder != null) {
					Database.DatabaseNovels.bookmarkNovel(Database.DatabaseIdentification.getNovelIDFromNovelURL(cCardsViewHolder.url!!))
					views[0]?.post {
						views[0]?.context?.toastOnUI("Added ${cCardsViewHolder.title.text}")
					}
				}
			}
		} catch (e: Exception) {
			if (cCardsViewHolder != null) {
				views[0]?.post {
					views[0]?.context?.toastOnUI("Failed to add to library: ${cCardsViewHolder.title.text}")
				}
				views[0]?.post { Log.e("NovelBackgroundAdd", cCardsViewHolder.title.text.toString() + " : " + e.message) }
			}
		}
		return null
	}

	override fun onPostExecute(aVoid: Void?) {
		cCardsViewHolder?.catalogFragment.recyclerView.post {
			cCardsViewHolder.catalogFragment.catalogueAdapter.notifyDataSetChanged()
		}
	}

}