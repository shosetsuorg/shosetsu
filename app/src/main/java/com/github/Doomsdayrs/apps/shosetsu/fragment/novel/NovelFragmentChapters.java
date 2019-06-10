package com.github.Doomsdayrs.apps.shosetsu.fragment.novel;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.adapters.catalogue.CatalogueCardsAdapter;
import com.github.Doomsdayrs.apps.shosetsu.adapters.novel.NovelChaptersAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NovelFragmentChapters extends Fragment {

    private Formatter formatter;
    private String URL;
    private FragmentManager fragmentManager;

    public List<NovelChapter> novelChapters;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;

    public NovelFragmentChapters() {
        setHasOptionsMenu(true);
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreate", "NovelFragmentChapters");
        View view = inflater.inflate(R.layout.fragment_novel_chapters, container, false);
        recyclerView = view.findViewById(R.id.fragment_novel_chapters_recycler);
        setNovels(novelChapters);
        this.context = container.getContext();
        return view;
    }

    public void setNovels(List<NovelChapter> novels) {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);

        adapter = new NovelChaptersAdapter(novels,fragmentManager,formatter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

}
