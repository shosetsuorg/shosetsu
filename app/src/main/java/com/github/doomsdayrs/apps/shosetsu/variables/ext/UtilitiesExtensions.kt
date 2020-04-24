package com.github.doomsdayrs.apps.shosetsu.variables.ext

import android.app.Activity
import android.content.Intent
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.providers.database.entities.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.activity.MainActivity
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader
import com.github.doomsdayrs.apps.shosetsu.ui.search.SearchController
import com.github.doomsdayrs.apps.shosetsu.ui.webView.Actions
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp
import com.github.doomsdayrs.apps.shosetsu.variables.enums.ReadingStatus
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
 */

/**
 * shosetsu
 * 04 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 * Pre resquite requires chapter to already have been added to library
 *
 * @param activity     activity
 * @param novelChapter novel chapter
 * @param novelID      id of novel
 * @param formatterID  formatter
 */
@Throws(MissingResourceException::class)
fun openChapter(activity: Activity, novelChapter: ChapterEntity, novelID: Int, formatterID: Int) =
		openChapter(activity, novelChapter, novelID, formatterID, null)


@Throws(MissingResourceException::class)
private fun openChapter(activity: Activity, novelChapter: ChapterEntity, novelID: Int, formatterID: Int, chapters: Array<String>?) {
	val chapterID = novelChapter.id
	if (Settings.readerMarkingType == Settings.MarkingTypes.ONVIEW.i) {
		novelChapter
	}

	Database.DatabaseChapter.setChapterStatus(chapterID, ReadingStatus.READING)
	val intent = Intent(activity, ChapterReader::class.java)
	intent.putExtra("chapterID", chapterID)
	intent.putExtra("novelID", novelID)
	intent.putExtra("formatter", formatterID)
	intent.putExtra("chapters", chapters)
	activity.startActivity(intent)
}

fun search(activity: Activity, query: String) {
	val mainActivity = activity as MainActivity
	val searchFragment = SearchController()
	searchFragment.query = query
	mainActivity.transitionView(searchFragment)
}

fun openInWebview(activity: Activity, url: String) {
	val intent = Intent(activity, WebViewApp::class.java)
	intent.putExtra("url", url)
	intent.putExtra("action", Actions.VIEW.action)
	activity.startActivity(intent)
}