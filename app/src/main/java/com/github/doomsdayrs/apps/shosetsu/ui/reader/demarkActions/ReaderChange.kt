package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.DemarkAction
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels
import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.ChapterView

class ReaderChange(private val chapterReader: ChapterView) : DemarkAction {
    override fun action(spared: Int) { //chapterReader.readerType = spared;
        DatabaseNovels.setReaderType(chapterReader.chapterReader!!.novelID, spared)
        chapterReader.setUpReader()
    }

}