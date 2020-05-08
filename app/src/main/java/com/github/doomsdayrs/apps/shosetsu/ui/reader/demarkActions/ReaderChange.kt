package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.DeMarkAction
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterView

class ReaderChange(private val chapterReader: ChapterView) : DeMarkAction {
    override fun action(spared: Int) { //chapterReader.readerType = spared;
        DatabaseNovels.setReaderType(chapterReader.chapterReader!!.novelID, spared)
        chapterReader.setUpReader()
    }

}