package com.github.doomsdayrs.apps.shosetsu.ui.novel.async;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.ErrorView;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;

import org.jsoup.nodes.Document;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromNovelURL;
import static com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper.docFromURL;

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

/**
 * Shosetsu
 * 17 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 * <p>
 * This task loads a novel for the novel fragment
 * </p>
 */
public class NovelLoader extends NovelLoaderHolder {
    private String novelURL;
    private Formatter formatter;
    private NovelPage novelPage;
    private int novelID;

    ErrorView errorView;

    private final boolean loadAll;


    /**
     * Constructor
     *
     * @param novelFragment reference to the fragment
     */
    public NovelLoader(NovelFragment novelFragment, ErrorView errorView, boolean loadAll) {
        super(novelFragment, null);
        this.novelURL = novelFragment.novelURL;
        this.formatter = novelFragment.formatter;
        this.novelID = novelFragment.novelID;
        this.novelPage = novelFragment.novelPage;
        this.loadAll = loadAll;
        this.errorView = errorView;
    }


    private NovelLoader(NovelLoader novelLoader) {
        super(novelLoader.novelFragment, novelLoader.novelFragmentInfo);
        this.novelURL = novelLoader.novelURL;
        assert (novelLoader.formatter != null);
        this.formatter = novelLoader.formatter;
        this.novelPage = novelLoader.novelPage;
        this.novelID = novelLoader.novelID;
        this.loadAll = novelLoader.loadAll;
        this.errorView = novelLoader.errorView;
    }

    /**
     * Background process
     *
     * @param voids activity to work with
     * @return if completed
     */
    @Override

    protected Boolean doInBackground(Activity... voids) {
        Activity activity = voids[0];
        Log.d("Loading", String.valueOf(novelURL));
        errorView.activity.runOnUiThread(() -> errorView.errorView.setVisibility(View.GONE));

        try {
            Document document = docFromURL(novelURL, formatter.hasCloudFlare());
            assert (document != null);
            novelPage = formatter.parseNovel(document);
            if (!activity.isDestroyed() && !Database.DatabaseNovels.inDatabase(novelID)) {
                Database.DatabaseNovels.addToLibrary(formatter.getID(), novelPage, novelURL, com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD.getA());
            }
            //TODO The getNovelID in this method likely will cause slowdowns due to IO
            int novelID = getNovelIDFromNovelURL(novelURL);
            for (NovelChapter novelChapter : novelPage.novelChapters)
                if (!activity.isDestroyed() && !Database.DatabaseChapter.inChapters(novelChapter.link))
                    Database.DatabaseChapter.addToChapters(novelID, novelChapter);


            Log.d("Loaded Novel:", novelPage.title);
            return true;
        } catch (Exception e) {
            errorView.activity.runOnUiThread(() -> errorView.errorView.setVisibility(View.VISIBLE));
            errorView.activity.runOnUiThread(() -> errorView.errorMessage.setText(e.getMessage()));
            errorView.activity.runOnUiThread(() -> errorView.errorButton.setOnClickListener(view -> refresh(activity)));
            e.printStackTrace();

        }
        return false;
    }

    private void refresh(Activity activity) {
        new NovelLoader(this).execute(activity);
    }

    @Override
    protected void onCancelled() {
        onPostExecute(false);
    }


}