package com.github.doomsdayrs.apps.shosetsu.ui.reader.async;

import android.os.AsyncTask;

import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.NewChapterView;

public class NewChapterReaderViewLoader extends AsyncTask<Object, Void, Void> {

    final NewChapterView newChapterView;

    public NewChapterReaderViewLoader(NewChapterView holder) {
        newChapterView = holder;
    }

    @Override
    protected Void doInBackground(Object... objects) {
        return null;
    }
}
