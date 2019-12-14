package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions;

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setTextSize;

public class TextSizeChange implements Utilities.DemarkAction {
    private final NewChapterReader chapterReader;

    public TextSizeChange(NewChapterReader chapterReader) {
        this.chapterReader = chapterReader;
    }

    @Override
    public void action(int spared) {
        int[] a = {14, 17, 20};
        setTextSize(a[spared]);
        chapterReader.currentView.setUpReader();
    }
}
