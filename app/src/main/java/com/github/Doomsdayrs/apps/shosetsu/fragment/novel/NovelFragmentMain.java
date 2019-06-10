package com.github.Doomsdayrs.apps.shosetsu.fragment.novel;

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

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NovelFragmentMain extends Fragment {
    boolean incrementChapters;


    static NovelPage novelPage;
    static Formatter formatter;
    static String URL;

    NovelFragmentChapters novelFragmentChapters;

    ImageView imageView;
    TextView title;
    TextView author;
    TextView description;

    public NovelFragmentMain() {
        setHasOptionsMenu(true);
    }

    public void setFormatter(Formatter formatter) {
        NovelFragmentMain.formatter = formatter;
        incrementChapters = formatter.isIncrementingChapterList();
    }

    public void setURL(String URL) {
        NovelFragmentMain.URL = URL;
    }

    public void setNovelFragmentChapters(NovelFragmentChapters novelFragmentChapters) {
        this.novelFragmentChapters = novelFragmentChapters;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreate", "NovelFragmentMain");
        View view = inflater.inflate(R.layout.fragment_novel_main, container, false);
        imageView = view.findViewById(R.id.fragment_novel_image);
        title = view.findViewById(R.id.fragment_novel_title);
        author = view.findViewById(R.id.fragment_novel_author);
        description = view.findViewById(R.id.fragment_novel_description);

        try {
            String u = new fillData().execute(this).get(40, TimeUnit.SECONDS);
            Picasso.get().load("http://novelfull.com" + u).into(imageView);
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


    static class fillData extends AsyncTask<NovelFragmentMain, Void, String> {
        @Override
        protected String doInBackground(NovelFragmentMain... novelFragmentMains) {
            if (novelPage == null) return null;
            novelFragmentMains[0].title.setText(novelPage.title);
            novelFragmentMains[0].author.setText(Arrays.toString(novelPage.authors));
            novelFragmentMains[0].description.setText(novelPage.description);
            novelFragmentMains[0].novelFragmentChapters.novelChapters = novelPage.novelChapters;
            return novelPage.imageURL;
        }
    }
}
