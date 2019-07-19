package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;

import com.github.doomsdayrs.apps.shosetsu.backend.async.CataloguePageLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.main.catalogue.CatalogueFragment;

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
 * Shosetsu
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CatalogueRefresh implements SwipeRefreshLayout.OnRefreshListener {
    private final CatalogueFragment catalogueFragment;

    public CatalogueRefresh(CatalogueFragment catalogueFragment) {
        this.catalogueFragment = catalogueFragment;
    }

    @Override
    public void onRefresh() {
        catalogueFragment.swipeRefreshLayout.setRefreshing(true);

        catalogueFragment.catalogueNovelCards = new ArrayList<>();
        catalogueFragment.currentMaxPage = 1;
        Log.d("FragmentRefresh", "Refreshing catalogue data");
        new CataloguePageLoader(catalogueFragment).execute();


    }
}
