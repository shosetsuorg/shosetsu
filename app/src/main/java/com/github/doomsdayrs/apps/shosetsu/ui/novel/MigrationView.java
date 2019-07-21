package com.github.doomsdayrs.apps.shosetsu.ui.novel;/*
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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.migration.MigratingMapAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.migration.MigratingNovelAdapter;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import java.io.IOException;
import java.util.ArrayList;

public class MigrationView extends AppCompatActivity {
    public ArrayList<NovelCard> novels = new ArrayList<>();
    public ArrayList<ArrayList<Novel>> novelResults = new ArrayList<>();

    private Formatter targetFormat;
    public int selection = 0;


    private RecyclerView selectedNovels;
    private RecyclerView.Adapter selectedNovelsAdapters;

    public RecyclerView mappingNovels;
    public RecyclerView.Adapter mappingNovelsAdapter;

    private Button cancel;
    private Button confirm;


    private Load load;

    public MigrationView() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        try {
            novels = (ArrayList<NovelCard>) Database.deserialize(intent.getStringExtra("selected"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        targetFormat = DefaultScrapers.formatters.get(intent.getIntExtra("target", 0));
        setContentView(R.layout.migrate_source_view);

        // Fills in dummy data
        for (int x = 0; x < novels.size(); x++)
            novelResults.add(new ArrayList<>());

        // Sets selected novels
        selectedNovels = findViewById(R.id.selection_view);
        setUpSelectedNovels();

        // Sets the novels to map
        mappingNovels = findViewById(R.id.mapping_view);
        setUpMappingNovels();

        // Sets cancel button
        cancel = findViewById(R.id.cancel);
        cancel.setOnLongClickListener(view -> {
            load.cancel(true);
            //TODO replace with close activity
            return true;
        });

        // Sets confirm button
        confirm = findViewById(R.id.confirm);
        confirm.setOnLongClickListener(view -> {
            load.cancel(true);
            //TODO replace with close activity
            return true;
        });

        load = new Load(novels, targetFormat, novelResults, mappingNovels, mappingNovelsAdapter);
        fillData();
    }



    public void fillData() {
        if (load.isCancelled()) {
            load = new Load(novels, targetFormat, novelResults, mappingNovels, mappingNovelsAdapter);
        }
        load.execute();
    }


    private void setUpSelectedNovels() {
        selectedNovelsAdapters = new MigratingNovelAdapter(this);
        selectedNovels.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        selectedNovels.setAdapter(selectedNovelsAdapters);
    }

    private void setUpMappingNovels() {
        mappingNovelsAdapter = new MigratingMapAdapter(this);
        mappingNovels.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mappingNovels.setAdapter(mappingNovelsAdapter);
    }

    static class Load extends AsyncTask<Void, Void, Void> {

        final ArrayList<NovelCard> novels;
        final Formatter targetFormat;
        final ArrayList<ArrayList<Novel>> novelResults;

        @SuppressLint("StaticFieldLeak")
        final RecyclerView mappingNovels;
        final RecyclerView.Adapter mappingNovelsAdapter;

        Load(ArrayList<NovelCard> novels, Formatter targetFormat, ArrayList<ArrayList<Novel>> novelResults, RecyclerView mappingNovels, RecyclerView.Adapter mappingNovelsAdapter) {
            this.novels = novels;
            this.targetFormat = targetFormat;
            this.novelResults = novelResults;
            this.mappingNovels = mappingNovels;
            this.mappingNovelsAdapter = mappingNovelsAdapter;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int x = 0; x < novels.size(); x++) {
                try {
                    // Retrieves search results
                    ArrayList<Novel> N = (ArrayList<Novel>) targetFormat.search(novels.get(x).title);

                    // Sets the results
                    novelResults.set(x, N);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mappingNovels.post(mappingNovelsAdapter::notifyDataSetChanged);
        }
    }
}
