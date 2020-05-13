package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.DeMarkAction
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader

class TextSizeChange(private val chapterReader: ChapterReader) : DeMarkAction {
	override fun action(spared: Int) {
		val a = intArrayOf(14, 17, 20)
		Settings.readerTextSize = (a[spared].toFloat())
		//chapterReader.setUpReader()
	}

}