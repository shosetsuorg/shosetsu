package com.github.doomsdayrs.apps.shosetsu.ui.migration;/*
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
 * shosetsu
 * 19 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters.MigratingMapAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters.MigratingNovelAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters.MigrationViewCatalogueAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.async.MigrationViewLoad;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.async.Transfer;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueCard;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import java.io.IOException;
import java.util.ArrayList;

public class MigrationView extends AppCompatActivity {
    Transfer t;

    private ArrayList<CatalogueCard> catalogues = null;


    public ArrayList<NovelCard> novels = new ArrayList<>();
    public ArrayList<ArrayList<Novel>> novelResults = new ArrayList<>();

    private ArrayList<String[]> confirmedMappings = new ArrayList<>();

    public int target = -1;
    public int selection = 0;
    public int secondSelection = -1;

    public ProgressBar progressBar;
    public TextView output;
    public TextView pageCount;

    public ConstraintLayout targetSelection;
    public ConstraintLayout migration;

    public RecyclerView selectedNovels;
    public RecyclerView.Adapter selectedNovelsAdapters;

    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView mappingNovels;
    public RecyclerView.Adapter mappingNovelsAdapter;

    private Button cancel;
    private Button confirm;


    private MigrationViewLoad load = null;

    public MigrationView() {
    }

    @Override
    protected void onDestroy() {
        if (t != null) {
            t.setC(false);
            t.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        try {
            novels = (ArrayList<NovelCard>) Database.deserialize(intent.getStringExtra("selected"));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.migrate_source_view);

        // Fills in dummy data
        {
            for (int x = 0; x < novels.size(); x++)
                novelResults.add(new ArrayList<>());
        }

        // Sets selected novels
        {
            selectedNovels = findViewById(R.id.selection_view);
            setUpSelectedNovels();
        }

        // Sets the novels to map
        {
            swipeRefreshLayout = findViewById(R.id.mapping_view_refresh);
            mappingNovels = findViewById(R.id.mapping_view);
            setUpMappingNovels();
        }

        // Sets cancel button
        {
            cancel = findViewById(R.id.cancel);
            cancel.setOnClickListener(view -> {
                secondSelection = -1;
                refresh();
            });
            cancel.setOnLongClickListener(view -> {
                load.cancel(true);
                finish();
                return true;
            });
        }

        // Sets confirm button
        {
            confirm = findViewById(R.id.confirm);
            confirm.setOnClickListener(view -> {
                if (secondSelection != -1) {
                    //Adds mapping targets
                    {
                        String[] map = new String[2];
                        map[0] = novels.get(selection).novelURL;
                        map[1] = novelResults.get(selection).get(secondSelection).link;
                        confirmedMappings.add(map);
                    }
                    novelResults.remove(selection);
                    novels.remove(selection);

                    if (selection != novels.size()) {
                        Log.d("Increment", "Increase");
                    } else if (selection - 1 != -1) {
                        Log.d("Increment", "Decrease");
                        selection--;
                    } else {
                        t = new Transfer(confirmedMappings, target, this);
                        t.execute();
                    }
                    secondSelection = -1;
                    refresh();
                } else
                    Toast.makeText(getApplicationContext(), "You need to select something!", Toast.LENGTH_SHORT).show();

            });
            confirm.setOnLongClickListener(view -> {
                load.cancel(true);
                //TODO replace with close activity
                return true;
            });
        }

        progressBar = findViewById(R.id.progress);
        output = findViewById(R.id.console_output);
        pageCount = findViewById(R.id.page_count);

        if (catalogues == null) {
            catalogues = DefaultScrapers.getAsCatalogue();
        }

        targetSelection = findViewById(R.id.target_selection);
        migration = findViewById(R.id.migrating);
        RecyclerView recyclerView = findViewById(R.id.catalogues_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.Adapter adapter = new MigrationViewCatalogueAdapter(catalogues, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //fillData();
    }

    public void fillData() {
        if (load == null)
            load = new MigrationViewLoad(this);
        if (load.isCancelled()) {
            load = new MigrationViewLoad(this);
        }
        load.execute();
    }


    private void setUpSelectedNovels() {
        selectedNovelsAdapters = new MigratingNovelAdapter(this);
        selectedNovels.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        selectedNovels.setAdapter(selectedNovelsAdapters);
    }


    public void refresh() {
        selectedNovels.post(selectedNovelsAdapters::notifyDataSetChanged);
        mappingNovels.post(mappingNovelsAdapter::notifyDataSetChanged);
    }

    private void setUpMappingNovels() {
        mappingNovelsAdapter = new MigratingMapAdapter(this);
        mappingNovels.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mappingNovels.setAdapter(mappingNovelsAdapter);
    }
}
