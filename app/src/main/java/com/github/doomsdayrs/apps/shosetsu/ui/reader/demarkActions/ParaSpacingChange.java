package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions;

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.changeParagraphSpacing;

public class ParaSpacingChange implements Utilities.DemarkAction {
    private final ChapterReader chapterReader;

    public ParaSpacingChange(ChapterReader chapterReader) {
        this.chapterReader = chapterReader;
    }

    @Override
    public void action(int spared) {
        changeParagraphSpacing(spared);
        chapterReader. setUpReader();
    }

}
