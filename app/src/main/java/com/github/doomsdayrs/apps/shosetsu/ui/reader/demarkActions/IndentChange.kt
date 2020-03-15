package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.DeMarkAction
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterView

class IndentChange(private val chapterReader: ChapterView) : DeMarkAction {
    override fun action(spared: Int) {
        Utilities.changeIndentSize(spared)
        chapterReader.setUpReader()
    }

}