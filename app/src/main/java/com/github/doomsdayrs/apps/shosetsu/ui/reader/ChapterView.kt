package com.github.doomsdayrs.apps.shosetsu.ui.reader

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings.ReaderThemes

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
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */


@ColorInt
fun Context.getReaderBackgroundColor(settings: ShosetsuSettings): Int {
	return when (settings.readerTheme) {
		ReaderThemes.NIGHT.i, ReaderThemes.DARK.i -> Color.BLACK
		ReaderThemes.LIGHT.i -> Color.WHITE
		ReaderThemes.SEPIA.i -> ContextCompat.getColor(this, R.color.wheat)
		ReaderThemes.DARKI.i -> Color.DKGRAY
		ReaderThemes.CUSTOM.i -> settings.readerCustomTextColor
		else -> Color.BLACK
	}
}

@ColorInt
fun getReaderTextColor(settings: ShosetsuSettings): Int {
	return when (settings.readerTheme) {
		ReaderThemes.NIGHT.i -> Color.WHITE
		ReaderThemes.LIGHT.i, ReaderThemes.SEPIA.i -> Color.BLACK
		ReaderThemes.DARK.i -> Color.GRAY
		ReaderThemes.DARKI.i -> Color.LTGRAY
		ReaderThemes.CUSTOM.i -> settings.readerCustomBackColor
		else -> Color.WHITE
	}
}