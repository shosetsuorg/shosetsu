package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions;

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.changeParagraphSpacing;

public class ParaSpacingChange implements Utilities.DemarkAction {
    private final NewChapterReader chapterReader;

    public ParaSpacingChange(NewChapterReader chapterReader) {
        this.chapterReader = chapterReader;
    }

    @Override
    public void action(int spared) {
        changeParagraphSpacing(spared);
        chapterReader.currentView.setUpReader();
    }

}
