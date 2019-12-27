package com.github.doomsdayrs.apps.shosetsu.backend

import android.content.Context
import android.os.AsyncTask
import com.github.doomsdayrs.apps.shosetsu.backend.async.NewChapterUpdater
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
 * Shosetsu
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
object UpdateManager {
    private var chapterUpdater: NewChapterUpdater? = null
    @JvmStatic
    fun init(novelCards: ArrayList<Int>, context: Context) {
        if (chapterUpdater == null) {
            chapterUpdater = NewChapterUpdater(novelCards, context)
            chapterUpdater!!.execute()
        } else {
            if (chapterUpdater!!.isCancelled || chapterUpdater!!.status == AsyncTask.Status.FINISHED) {
                chapterUpdater = NewChapterUpdater(novelCards, context)
                chapterUpdater!!.execute()
            }
            if (chapterUpdater!!.status == AsyncTask.Status.PENDING) chapterUpdater!!.execute()
        }
    }
}