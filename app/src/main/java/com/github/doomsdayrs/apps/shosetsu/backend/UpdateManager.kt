package com.github.doomsdayrs.apps.shosetsu.backend

import android.content.Context
import com.github.doomsdayrs.apps.shosetsu.backend.async.ChapterUpdater
import needle.CancelableTask
import needle.Needle
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
 */
/**
 * Shosetsu
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
object UpdateManager {
    private var chapterUpdater: ChapterUpdater? = null
    @JvmStatic
    fun init(novelCards: ArrayList<Int>, context: Context) {
        if (chapterUpdater == null) {
            chapterUpdater = ChapterUpdater(novelCards, context)
            Needle.onBackgroundThread().execute(chapterUpdater as CancelableTask)
        } else {
            if (chapterUpdater!!.isCanceled) {
                chapterUpdater = ChapterUpdater(novelCards, context)
                Needle.onBackgroundThread().execute(chapterUpdater as CancelableTask)
            }
        }
    }
}