package com.github.Doomsdayrs.apps.shosetsu.fragment.novel;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.apps.shosetsu.R;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class NovelFragmentChapterView extends Fragment {
    TextView textView;
    Formatter formatter;
    String URL;
    String text;

    public void setText(String text) {
        this.text = text;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novel_chapter_view, container, false);
        textView = view.findViewById(R.id.fragment_novel_chapter_view_text);
        if (URL == null)
            textView.setText(text);
        else {
            try {
                textView.setText(new getNovel().execute(this).get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return view;
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
