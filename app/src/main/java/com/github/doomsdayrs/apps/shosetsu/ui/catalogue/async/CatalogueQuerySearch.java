package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async;

import android.os.AsyncTask;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CatalogueQuerySearch extends AsyncTask<String, Void, ArrayList<CatalogueNovelCard>> {
    private CatalogueFragment catalogueFragment;

    public CatalogueQuerySearch(CatalogueFragment catalogueFragment) {
        this.catalogueFragment = catalogueFragment;
    }

    /**
     * Search catalogue
     * @param strings ignored
     * @return List of results
     */
    @Override
    protected ArrayList<CatalogueNovelCard> doInBackground(String... strings) {
        ArrayList<CatalogueNovelCard> result = new ArrayList<>();
        try {
            List<Novel> novels = catalogueFragment.formatter.search(strings[0]);
            for (Novel novel : novels)
                result.add(new CatalogueNovelCard(novel.imageURL, novel.title, novel.link));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}