package com.github.doomsdayrs.apps.shosetsu.backend.async

import android.os.AsyncTask
import android.util.Log
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel

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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
@Deprecated("AsyncTask is bad")
class ChapterLoader(var formatter: Formatter, var novelURL: String) : AsyncTask<Void, Void, Boolean>() {

	private val finalChapters: ArrayList<Novel.Chapter> = ArrayList()


	public override fun doInBackground(vararg p0: Void?): Boolean {
		// loads page
		val novelPage: Novel.Info = formatter.parseNovel(novelURL, true) {}

		// Iterates through chapters
		for ((mangaCount, novelChapter) in novelPage.chapters.withIndex()) logAndAdd(novelChapter, mangaCount)
		return true
	}

	private fun logAndAdd(novelChapter: Novel.Chapter, mangaCount: Int) {
		Log.i("ChapterLoader", "Loading #$mangaCount: ${novelChapter.link}")
		finalChapters.add(novelChapter)
	}
}