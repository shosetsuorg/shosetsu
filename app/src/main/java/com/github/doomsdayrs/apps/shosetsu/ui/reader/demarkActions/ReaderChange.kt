package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.DeMarkAction
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader

class ReaderChange(private val chapterReader: ChapterReader) : DeMarkAction {
	override fun action(spared: Int) {
		//chapterReader.readerType = spared;
	}
}