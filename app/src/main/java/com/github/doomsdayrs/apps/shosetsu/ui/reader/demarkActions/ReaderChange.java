package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions;

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.setReaderType;

public class ReaderChange implements Utilities.DemarkAction {
    private final NewChapterReader chapterReader;

    public ReaderChange(NewChapterReader chapterReader) {
        this.chapterReader = chapterReader;
    }

    @Override
    public void action(int spared) {
        //chapterReader.readerType = spared;
        setReaderType(chapterReader.novelID, spared);
        chapterReader.currentView.setUpReader();
    }

}
