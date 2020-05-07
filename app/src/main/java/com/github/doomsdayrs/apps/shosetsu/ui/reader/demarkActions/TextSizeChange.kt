package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.DeMarkAction
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterView

class TextSizeChange(private val chapterReader: ChapterView) : DeMarkAction {
	override fun action(spared: Int) {
		val a = intArrayOf(14, 17, 20)
		Settings.readerTextSize = (a[spared].toFloat())
		chapterReader.setUpReader()
	}

}