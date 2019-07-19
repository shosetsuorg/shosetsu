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
import android.os.AsyncTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.migration.MigratingMapAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.migration.MigratingNovelAdapter;

import java.util.ArrayList;

public class MigrationView {
    public ArrayList<Novel> novels = new ArrayList<>();
    public Novel[][] novelResults = new Novel[][]{};
    private Dialog dialog;

    private RecyclerView selectedNovels;
    private RecyclerView.Adapter selectedNovelsAdapters;


    private RecyclerView mappingNovels;
    private RecyclerView.Adapter mappingNovelsAdapter;

    public MigrationView() {
        dialog.setContentView(R.layout.migrate_source_view);
        selectedNovels = dialog.findViewById(R.id.selection_view);
        mappingNovels = dialog.findViewById(R.id.mapping_view);
    }

    public void setNovels(ArrayList<Novel> novels) {
        this.novels = novels;
    }

    public void display() {
        dialog.show();
        setUpSelectedNovels();
        setUpMappingNovels();
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void fillData() {

    }


    private void setUpSelectedNovels() {
        if (selectedNovels != null) {
            selectedNovels.setHasFixedSize(true);
            selectedNovelsAdapters = new MigratingNovelAdapter(this);
            mappingNovels.setLayoutManager(new LinearLayoutManager(dialog.getContext()));
            selectedNovels.setAdapter(selectedNovelsAdapters);
        }
    }

    private void setUpMappingNovels() {
        if (mappingNovels != null) {
            mappingNovels.setHasFixedSize(true);
            mappingNovelsAdapter = new MigratingMapAdapter(new ArrayList<>());
            mappingNovels.setLayoutManager(new LinearLayoutManager(dialog.getContext()));
            mappingNovels.setAdapter(mappingNovelsAdapter);
        }
    }

    static class Load extends AsyncTask<Void, Void, Void> {

        MigrationView migrationView;

        public Load(MigrationView migrationView) {
            this.migrationView = migrationView;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            migrationView.novelResults = new Novel[migrationView.novels.size()][];
            for (int x = 0; x < migrationView.novels.size();x++) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            migrationView.mappingNovels.post(() -> migrationView.mappingNovelsAdapter.notifyDataSetChanged());
        }
    }
}
