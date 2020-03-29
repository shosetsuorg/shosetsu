package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.DeMarkAction
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterView
import com.github.doomsdayrs.apps.shosetsu.backend.Settings

class ParaSpacingChange(private val chapterReader: ChapterView) : DeMarkAction {
    override fun action(spared: Int) {
        Settings.readerParagraphSpacing = (spared)
        chapterReader.setUpReader()
    }

}