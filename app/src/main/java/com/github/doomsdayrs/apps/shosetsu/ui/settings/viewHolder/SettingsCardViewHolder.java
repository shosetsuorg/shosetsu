package com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder;
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
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.settings.listener.OnSettingsCardClick;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types;

public class SettingsCardViewHolder extends RecyclerView.ViewHolder {
    final TextView library_card_title;
    final CardView cardView;
    final FragmentManager fragmentManager;

    public SettingsCardViewHolder(@NonNull View itemView, FragmentManager fragmentManager) {
        super(itemView);
        library_card_title = itemView.findViewById(R.id.recycler_settings_title);
        cardView = itemView.findViewById(R.id.settings_card);
        this.fragmentManager = fragmentManager;
    }

    public void setType(Types type) {
        cardView.setOnClickListener(new OnSettingsCardClick(type, fragmentManager));
        library_card_title.setText(type.toString());
    }
}