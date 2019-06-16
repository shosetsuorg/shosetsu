package com.github.Doomsdayrs.apps.shosetsu.fragment.novel;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.main.DefaultScrapers;
import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.database.Database;
import com.github.Doomsdayrs.apps.shosetsu.settings.Settings;
import com.github.Doomsdayrs.apps.shosetsu.settings.SettingsController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragmentChapterView extends AppCompatActivity {
    Toolbar toolbar;
    ScrollView scrollView;
    TextView textView;
    Formatter formatter;
    String URL;
    String novelURL;
    String text;


    MenuItem bookmark;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("text", text);
        outState.putString("url", URL);
        outState.putInt("formatter", formatter.getID());
        outState.putString("novelURL", novelURL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_chapter_view, menu);
        // Night mode
        menu.findItem(R.id.chapter_view_nightMode).setChecked(!SettingsController.isReaderLightMode());

        // Bookmark
        bookmark = menu.findItem(R.id.chapter_view_bookmark);

        if (SettingsController.isBookMarked(URL)) {
            bookmark.setIcon(R.drawable.ic_bookmark_black_24dp);
            int y = SettingsController.getYBookmark(URL);
            Log.d("Loaded Scroll", Integer.toString(y));
            scrollView.setScrollY(y);
        }
        return true;
    }

    public void setThemeMode() {
        scrollView.setBackgroundColor(Settings.ReaderTextBackgroundColor);
        textView.setBackgroundColor(Settings.ReaderTextBackgroundColor);
        textView.setTextColor(Settings.ReaderTextColor);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null)
            return false;
        Log.d("item", item.toString());
        switch (item.getItemId()) {
            case R.id.chapter_view_nightMode: {
                if (!item.isChecked()) {
                    SettingsController.swapReaderColor();
                    setThemeMode();
                } else {
                    SettingsController.swapReaderColor();
                    setThemeMode();
                }
                item.setChecked(!item.isChecked());
                return true;
            }
            case R.id.chapter_view_textSize: {
                return true;
            }
            case R.id.chapter_view_bookmark: {
                JSONObject jsonObject = new JSONObject();
                try {
                    int y = scrollView.getScrollY();

                    Log.d("ScrollSave", Integer.toString(y));
                    jsonObject.put("y", y);

                    if (SettingsController.toggleBookmarkChapter(URL, jsonObject))
                        bookmark.setIcon(R.drawable.ic_bookmark_black_24dp);
                    else bookmark.setIcon(R.drawable.ic_bookmark_border_black_24dp);
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_novel_chapter_view);
        {
            novelURL = getIntent().getStringExtra("novelURL");
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            formatter = DefaultScrapers.formatters.get(getIntent().getIntExtra("formatter", -1) - 1);
            scrollView = findViewById(R.id.fragment_novel_scroll);
            textView = findViewById(R.id.fragment_novel_chapter_view_text);
            textView.setOnClickListener(new click(toolbar));
        }

        setThemeMode();

        if (savedInstanceState != null) {
            URL = savedInstanceState.getString("url");
            formatter = DefaultScrapers.formatters.get(savedInstanceState.getInt("formatter") - 1);
            text = savedInstanceState.getString("text");
        } else URL = getIntent().getStringExtra("url");


        if (getIntent().getBooleanExtra("downloaded", false))
            text = Database.getSaved(novelURL, URL).replaceAll("\n", "\n\n");
        else if (text == null)
            if (URL != null) {
                try {
                    text = new getNovel().execute(this).get().replaceAll("\n", "\n\n");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        textView.setText(text);
    }


    static class click implements View.OnClickListener {
        Toolbar toolbar;
        boolean visible = true;


        click(Toolbar toolbar) {
            this.toolbar = toolbar;
        }

        @Override
        public void onClick(View v) {
            if (visible) {
                toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                visible = !visible;
            } else {
                toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                visible = !visible;
            }
        }
    }

    static class getNovel extends AsyncTask<NovelFragmentChapterView, Void, String> {
        @Override
        protected String doInBackground(NovelFragmentChapterView... novelFragmentChapterViews) {
            try {
                return novelFragmentChapterViews[0].formatter.getNovelPassage(novelFragmentChapterViews[0].URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
