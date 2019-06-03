package com.github.Doomsdayrs.apps.shosetsu.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.Doomsdayrs.apps.shosetsu.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;;

public class NovelFragment extends Fragment {
    boolean incrementChapters;

    ArrayList<NovelChapter> novelChapters;

    Formatter formatter;
    String URL;
    ImageView imageView;
    TextView title;
    TextView author;
    TextView description;

    public NovelFragment() {
        setHasOptionsMenu(true);
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
        incrementChapters = formatter.isIncrementingChapterList();
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novel, container, false);
        imageView = view.findViewById(R.id.fragment_novel_image);
        title = view.findViewById(R.id.fragment_novel_title);
        author = view.findViewById(R.id.fragment_novel_author);
        description = view.findViewById(R.id.fragment_novel_description);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    class fillData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                NovelPage novelPage = formatter.parseNovel(URL);
                novelChapters.addAll(novelPage.novelChapters);
                title.setText(novelPage.title);
                author.setText(Arrays.toString(novelPage.authors));
                description.setText(novelPage.description);

                Glide.with(getContext())
                        .asBitmap()
                        .load(novelPage.imageURL)
                        .into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
