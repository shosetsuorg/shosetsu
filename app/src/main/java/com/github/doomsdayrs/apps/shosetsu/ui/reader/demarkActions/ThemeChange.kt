package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import android.content.Intent
import com.github.doomsdayrs.apps.shosetsu.backend.DeMarkAction
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.common.consts.Broadcasts
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader

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
 * 01 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */

class ThemeChange(private val viewChapterReader: ChapterReader) : DeMarkAction {
	override fun action(spared: Int) {
		Settings.readerTheme = spared
		val intent = Intent()
		intent.action = Broadcasts.BC_CHAPTER_VIEW_THEME_CHANGE
		viewChapterReader.sendBroadcast(intent)
	}

}