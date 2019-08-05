package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;

import java.io.IOException;
import java.util.ArrayList;

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
 * shosetsu
 * 22 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class MigrationViewLoad extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    final MigrationView migrationView;
    final Formatter targetFormat;

    public MigrationViewLoad(MigrationView migrationView) {
        this.migrationView = migrationView;
        this.targetFormat = DefaultScrapers.formatters.get(migrationView.target);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("Searching with", targetFormat.getName());
        for (int x = 0; x < migrationView.novels.size(); x++) {
            try {
                // Retrieves search results
                ArrayList<Novel> N = (ArrayList<Novel>) targetFormat.search(migrationView.novels.get(x).title);

                // Sets the results
                migrationView.novelResults.set(x, N);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        migrationView.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        migrationView.swipeRefreshLayout.setRefreshing(false);
        migrationView.mappingNovels.post(migrationView.mappingNovelsAdapter::notifyDataSetChanged);
    }
}
