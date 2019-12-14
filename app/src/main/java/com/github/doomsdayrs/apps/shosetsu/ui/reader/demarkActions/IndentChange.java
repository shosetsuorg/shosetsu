package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions;

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.changeIndentSize;

public class IndentChange implements Utilities.DemarkAction {
    private final NewChapterReader chapterReader;

    public IndentChange(NewChapterReader chapterReader) {
        this.chapterReader = chapterReader;
    }

    @Override
    public void action(int spared) {
        changeIndentSize(spared);
        chapterReader.currentView.setUpReader();
    }
}
