package com.github.doomsdayrs.apps.shosetsu.ui.catalogue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.WebviewCookieHandler;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters.CatalogueAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async.CataloguePageLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueHitBottom;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueRefresh;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueSearchQuery;
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.calculateNoOfColumns;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO fix issue with not loading
public class CatalogueFragment extends Fragment {
    public ArrayList<CatalogueNovelCard> catalogueNovelCards = new ArrayList<>();
    public Formatter formatter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView library_view;
    public int currentMaxPage = 1;
    public boolean isInSearch = false;
    private Context context;

    public CatalogueAdapter catalogueAdapter;
    public ProgressBar bottomProgressBar;

    private boolean dontRefresh = false;
    public boolean isQuery = false;

    public ConstraintLayout errorView;
    public TextView errorMessage;
    public Button errorButton;
    public TextView empty;

    /**
     * Constructor
     */
    public CatalogueFragment() {
        setHasOptionsMenu(true);
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("list", catalogueNovelCards);
        if (formatter != null)
            outState.putInt("formatter", formatter.getID());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Resume", "HERE");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Pause", "HERE");
        dontRefresh = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dontRefresh = false;
    }

    /**
     * Creates view
     *
     * @param inflater           inflates layouts and shiz
     * @param container          container of this fragment
     * @param savedInstanceState save file
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "CatalogueFragment");
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        {
            library_view = view.findViewById(R.id.fragment_catalogue_recycler);
            swipeRefreshLayout = view.findViewById(R.id.fragment_catalogue_refresh);
            bottomProgressBar = view.findViewById(R.id.fragment_catalogue_progress_bottom);
            errorView = view.findViewById(R.id.network_error);
            errorMessage = view.findViewById(R.id.error_message);
            errorButton = view.findViewById(R.id.error_button);
            empty = view.findViewById(R.id.fragment_catalogue_empty);
        }

        if (savedInstanceState != null) {
            catalogueNovelCards = (ArrayList<CatalogueNovelCard>) savedInstanceState.getSerializable("list");
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"));
        }
        Statics.mainActionBar.setTitle(formatter.getName());
        swipeRefreshLayout.setOnRefreshListener(new CatalogueRefresh(this));
        this.context = Objects.requireNonNull(container).getContext();


        if (savedInstanceState == null && !dontRefresh) {
            Log.d("Process", "Loading up latest");
            setLibraryCards(catalogueNovelCards);
            if (catalogueNovelCards.size() > 0) {
                catalogueNovelCards = new ArrayList<>();
                catalogueAdapter.notifyDataSetChanged();
            }
            if (!formatter.hasCloudFlare())
                new CataloguePageLoader(this).execute();
            else webView();
        } else
            setLibraryCards(catalogueNovelCards);

        return view;
    }

    private void webView() {
        Intent intent = new Intent(getActivity(), WebViewApp.class);
        intent.putExtra("url", formatter.getLatestURL(0));
        intent.putExtra("action", 1);
        startActivityForResult(intent, 42);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 42) {
            //TODO, Pass cookies from webview to okhttp
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new WebviewCookieHandler())
                    .build();
            formatter.setClient(client);
            new CataloguePageLoader(this).execute();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_library, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.library_search).getActionView();
        searchView.setOnQueryTextListener(new CatalogueSearchQuery(this));
        searchView.setOnCloseListener(() -> {
            isQuery = false;
            isInSearch = false;
            setLibraryCards(catalogueNovelCards);
            return true;
        });
    }


    public void setLibraryCards(ArrayList<CatalogueNovelCard> recycleCards) {
        if (library_view != null) {
            library_view.setHasFixedSize(false);
            RecyclerView.LayoutManager library_layoutManager;

            if (getContext() != null) {
                library_layoutManager = new GridLayoutManager(context, calculateNoOfColumns(getContext(), 200), RecyclerView.VERTICAL, false);

                catalogueAdapter = new CatalogueAdapter(recycleCards, this, formatter);
                library_view.setLayoutManager(library_layoutManager);
                library_view.addOnScrollListener(new CatalogueHitBottom(this));
                library_view.setAdapter(catalogueAdapter);
            }
        }
    }
}
