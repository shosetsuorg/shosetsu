package com.github.Doomsdayrs.apps.shosetsu.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.Doomsdayrs.api.novelreader_core.extensions.lang.en.novel_full.NovelFull;
import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.Doomsdayrs.apps.shosetsu.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;;

public class NovelFragment extends Fragment {
    boolean incrementChapters;

    ArrayList<NovelChapter> novelChapters = new ArrayList<>();

    static Formatter formatter;
    static String URL;
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
        try {
            String u = new fillData().execute().get(10, TimeUnit.SECONDS);
            Glide.with(getContext())
                    .asBitmap()
                    .load(u)
                    .into(imageView);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    class fillData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                NovelPage novelPage = formatter.parseNovel("http://novelfull.com" + URL);
                novelChapters.addAll(novelPage.novelChapters);
                title.setText(novelPage.title);
                author.setText(Arrays.toString(novelPage.authors));
                description.setText(novelPage.description);
                Log.d("Novel",novelPage.toString());
                return novelPage.imageURL;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
