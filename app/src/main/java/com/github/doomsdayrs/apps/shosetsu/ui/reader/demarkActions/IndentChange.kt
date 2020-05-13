package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.DeMarkAction
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader

class IndentChange(private val chapterReader: ChapterReader) : DeMarkAction {
	override fun action(spared: Int) {
		Settings.ReaderIndentSize = spared
		//chapterReader.setUpReader()
	}
}