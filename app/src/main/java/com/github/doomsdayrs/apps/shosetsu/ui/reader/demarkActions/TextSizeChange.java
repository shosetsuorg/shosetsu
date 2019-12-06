package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions;

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setTextSize;

public class TextSizeChange implements Utilities.DemarkAction {
    private final ChapterReader chapterReader;

    public TextSizeChange(ChapterReader chapterReader) {
        this.chapterReader = chapterReader;
    }


    @Override
    public void action(int spared) {
        int[] a = {14, 17, 20};
        setTextSize(a[spared]);
        chapterReader.currentView.setUpReader();
    }
}
