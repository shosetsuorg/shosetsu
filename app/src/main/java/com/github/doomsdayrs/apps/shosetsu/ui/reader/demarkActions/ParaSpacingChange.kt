package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.DeMarkAction
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader

class ParaSpacingChange(private val chapterReader: ChapterReader) : DeMarkAction {
	override fun action(spared: Int) {
		Settings.readerParagraphSpacing = (spared)
//		chapterReader.setUpReader()
	}
}