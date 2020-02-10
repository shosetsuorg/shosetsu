package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.DeMarkAction
import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.ChapterView
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Settings

class ParaSpacingChange(private val chapterReader: ChapterView) : DeMarkAction {
    override fun action(spared: Int) {
        Settings.paragraphSpacing = (spared)
        chapterReader.setUpReader()
    }

}