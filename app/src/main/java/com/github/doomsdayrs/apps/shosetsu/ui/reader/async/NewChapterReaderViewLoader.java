package com.github.doomsdayrs.apps.shosetsu.ui.reader.async;

import android.app.Activity;
import android.os.AsyncTask;

import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewChapterView;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getY;
import static com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper.docFromURL;

public class NewChapterReaderViewLoader extends AsyncTask<Object, Void, Void> {

    private final NewChapterView newChapterView;

    public NewChapterReaderViewLoader(NewChapterView holder) {
        newChapterView = holder;
    }

    @Override
    protected Void doInBackground(Object... objects) {

        Activity activity = newChapterView.newChapterReader;
        //activity.runOnUiThread(() -> chapterView.errorView.errorView.setVisibility(View.GONE));
        try {
            if (newChapterView.newChapterReader != null && newChapterView.newChapterReader.formatter != null && newChapterView.getScrollView() != null && newChapterView.newChapterReader.formatter != null) {
                newChapterView.unformattedText = newChapterView.newChapterReader.formatter.getNovelPassage(docFromURL(newChapterView.url, newChapterView.newChapterReader.formatter.getHasCloudFlare()));
                activity.runOnUiThread(newChapterView::setUpReader);
                activity.runOnUiThread(() -> {
                            if (newChapterView.getScrollView() != null) {
                                newChapterView.getScrollView().post(() -> newChapterView.getScrollView().scrollTo(0, getY(newChapterView.chapterID)));
                            }
                        }
                );
                activity.runOnUiThread(() -> newChapterView.ready = true);
            }
        } catch (
                Exception e) {
            // activity.runOnUiThread(() -> { chapterView.errorView.errorView.setVisibility(View.VISIBLE);chapterView.errorView.errorMessage.setText(e.getMessage());chapterView.errorView.errorButton.setOnClickListener(view -> new ReaderViewLoader(chapterView).execute()); });
        }
        return null;
    }

}
