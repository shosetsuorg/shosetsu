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

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.migration.MigratingMapAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.migration.MigratingNovelAdapter;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import java.io.IOException;
import java.util.ArrayList;

public class MigrationView {
    public ArrayList<NovelCard> novels = new ArrayList<>();
    public ArrayList<ArrayList<Novel>> novelResults = new ArrayList<>();

    private final Formatter targetFormat;
    public int selection = 0;

    public Dialog dialog;

    private RecyclerView selectedNovels;
    private RecyclerView.Adapter selectedNovelsAdapters;

    private RecyclerView mappingNovels;
    private RecyclerView.Adapter mappingNovelsAdapter;

    private Button cancel;
    private Button confirm;


    private Load load = new Load(this);

    public MigrationView(Context context, ArrayList<NovelCard> novels, int targetSite) {
        this.novels = novels;

        // Fills in dummy data
        for (int x = 0; x < novels.size(); x++)
            novelResults.add(new ArrayList<>());

        this.targetFormat = DefaultScrapers.formatters.get(targetSite);
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.migrate_source_view);

        // Sets selected novels
        selectedNovels = dialog.findViewById(R.id.selection_view);
        setUpSelectedNovels();

        // Sets the novels to map
        mappingNovels = dialog.findViewById(R.id.mapping_view);
        setUpMappingNovels();

        // Sets cancel button
        cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnLongClickListener(view -> {
            load.cancel(true);
            dialog.cancel();
            return true;
        });

        // Sets confirm button
        confirm = dialog.findViewById(R.id.confirm);
        confirm.setOnLongClickListener(view -> {
            load.cancel(true);
            dialog.cancel();
            return true;
        });

        // Sets dismiss button
        dialog.setOnCancelListener(dialogInterface -> load.cancel(true));

        // Displays and loads
        dialog.show();
        fillData();
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void fillData() {
        if (load.isCancelled()) {
            load = new Load(this);
        }
        load.execute();
    }


    private void setUpSelectedNovels() {
        selectedNovelsAdapters = new MigratingNovelAdapter(this);
        selectedNovels.setLayoutManager(new LinearLayoutManager(dialog.getContext()));
        selectedNovels.setAdapter(selectedNovelsAdapters);
    }

    private void setUpMappingNovels() {
        mappingNovelsAdapter = new MigratingMapAdapter(this);
        mappingNovels.setLayoutManager(new LinearLayoutManager(dialog.getContext()));
        mappingNovels.setAdapter(mappingNovelsAdapter);
    }

    static class Load extends AsyncTask<Void, Void, Void> {

        MigrationView migrationView;

        public Load(MigrationView migrationView) {
            this.migrationView = migrationView;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (NovelCard novel : migrationView.novels) {
                try {
                    ArrayList<Novel> novels = (ArrayList<Novel>) migrationView.targetFormat.search(novel.title);
                    migrationView.novelResults.set(migrationView.novels.indexOf(novel), novels);
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
            migrationView.mappingNovels.post(() -> migrationView.mappingNovelsAdapter.notifyDataSetChanged());
        }
    }
}
