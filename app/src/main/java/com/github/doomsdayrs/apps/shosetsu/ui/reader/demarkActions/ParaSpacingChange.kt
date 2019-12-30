package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.DemarkAction
import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.ChapterView

class ParaSpacingChange(private val chapterReader: ChapterView) : DemarkAction {
    override fun action(spared: Int) {
        Utilities.changeParagraphSpacing(spared)
        chapterReader.setUpReader()
    }

}