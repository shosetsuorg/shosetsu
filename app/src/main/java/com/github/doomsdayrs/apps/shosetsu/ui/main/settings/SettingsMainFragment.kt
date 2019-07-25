package com.github.doomsdayrs.apps.shosetsu.ui.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.SettingsAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.Statics
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsCard

import java.util.ArrayList
import java.util.Objects

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
/**
 * Constructor
 * TODO, Create custom option menu for settings to search specific ones
 */
class SettingsMainFragment : Fragment() {

    /**
     * Save data of view before destroyed
     *
     * @param outState output save
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    /**
     * Creates view
     *
     * @param inflater           inflates layouts and shiz
     * @param container          container of this fragment
     * @param savedInstanceState save file
     * @return View
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "SettingsMainFragment")
        Statics.mainActionBar.title = "Settings"
        val view = inflater.inflate(R.layout.settings, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.settings_recycler)


        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(Objects.requireNonNull(container).context)
            val adapter = SettingsAdapter(cards, fragmentManager)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }

        return view
    }

    companion object {
        private val cards = ArrayList<SettingsCard>()

        init {
            cards.add(SettingsCard(Types.DOWNLOAD))
            cards.add(SettingsCard(Types.VIEW))
            cards.add(SettingsCard(Types.ADVANCED))
            cards.add(SettingsCard(Types.INFO))
            cards.add(SettingsCard(Types.BACKUP))
        }
    }
}// setHasOptionsMenu(true);


