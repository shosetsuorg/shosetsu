package com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects;
// TODO: Change to new license/disclaimer.
/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/hXtreme
 */

import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.doomsdayrs.apps.shosetsu.R;

public class SettingsItem {
    protected View itemView;
    protected TextView itemTitle;
    protected TextView itemDesc;

    public SettingsItem(@NonNull View view){
        itemView = view;
        itemTitle = itemView.findViewById(R.id.settings_item_title);
        if (itemTitle == null){
            // TODO: Log error & quit gracefully
        }
        itemDesc = itemView.findViewById(R.id.settings_item_desc);
        if (itemDesc == null){
            // TODO: Log error & quit gracefully
        }
    }

    public void invalidate(){
        itemView.invalidate();
    }

    public void setTitle(int titleResid) {
        itemTitle.setText(titleResid);
    }

    public void setDesc(int descResid) {
        itemDesc.setText(descResid);
    }

    public void setTitle(@NonNull String title) {
        itemTitle.setText(title);
    }

    public void setDesc(@NonNull String desc) {
        itemDesc.setText(desc);
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        itemView.setOnClickListener(onClickListener);
    }
}
