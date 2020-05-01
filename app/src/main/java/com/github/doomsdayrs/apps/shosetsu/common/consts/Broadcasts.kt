package com.github.doomsdayrs.apps.shosetsu.common.consts

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
 * 08 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */
object Broadcasts {
    /** Tells receiver to update it's recycler view's adapter */
    const val BC_NOTIFY_DATA_CHANGE = "notifyDataChange"

    /** BroadCasted by [com.github.doomsdayrs.apps.shosetsu.backend.services.DownloadService]*/
    const val BC_DOWNLOADS_MARK_ERROR = "markError"
    const val BC_DOWNLOADS_REMOVE = "removeItem"
    const val BC_DOWNLOADS_TOGGLE = "toggleStatus"
    const val BC_DOWNLOADS_RECEIVED_URL = "chapterURL"

    const val BC_CHAPTER_ADDED = "chapterAdded"

    /** Target is [com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelChaptersController]*/
    const val BC_RELOAD_CHAPTERS_FROM_DB = "reloadChaptersFromDatabase"

    const val BC_CHAPTER_VIEW_THEME_CHANGE = "changeChapterViewTheme"
}