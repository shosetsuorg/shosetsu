package com.github.Doomsdayrs.apps.shosetsu.fragment.novel;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.main.DefaultScrapers;
import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.apps.shosetsu.R;

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
    TextView textView;
    Formatter formatter;
    String URL;
    String text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_novel_chapter_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        URL = getIntent().getStringExtra("url");
        formatter = DefaultScrapers.formatters.get(getIntent().getIntExtra("formatter", -1) - 1);
        textView = findViewById(R.id.fragment_novel_chapter_view_text);
        if (URL == null)
            textView.setText(text);
        else {
            try {
                textView.setText(new getNovel().execute(this).get().replaceAll("\n", "\n\n"));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(toolbar);
        textView.setOnClickListener(new click(toolbar));

    }


    static class click implements View.OnClickListener {
        Toolbar toolbar;
        boolean visible = true;


        click(Toolbar toolbar) {
            this.toolbar = toolbar;
        }

        @Override
        public void onClick(View v) {
            if (visible){
                toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                visible = !visible;
            }
            else {
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
