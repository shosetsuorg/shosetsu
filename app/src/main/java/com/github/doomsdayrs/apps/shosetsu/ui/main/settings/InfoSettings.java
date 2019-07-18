package com.github.doomsdayrs.apps.shosetsu.ui.main.settings;
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

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.R;

public class InfoSettings extends Fragment {

    private void onClickAppVer(View v){
        // TODO: Check for update
        Toast.makeText(v.getContext(), "AppVer", Toast.LENGTH_SHORT).show();
    }

    private void onClickReportBug(View v){
        // TODO: Display the link after consultation
        Toast.makeText(v.getContext(), "ReportBug", Toast.LENGTH_SHORT).show();
    }

    private void onClickAuthor(View v){
        // TODO: Add link to github
        Toast.makeText(v.getContext(), "Author", Toast.LENGTH_SHORT).show();
    }

    private void onClickDisclaimer(View v){
        // TODO: Show full disclaimer on click
        Toast.makeText(v.getContext(), "Disclaimer", Toast.LENGTH_SHORT).show();
    }

    private void onClickLicense(View v){
         // TODO: Show full license on click
        Toast.makeText(v.getContext(), "License", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "ViewSettings");
        View settingsInfoView = inflater.inflate(R.layout.settings_info, container, false);

        // Setup App version
        SettingsItem appVerItem = new SettingsItem(settingsInfoView.findViewById(R.id.settings_info_app_version));
        appVerItem.setTitle(R.string.version);
        appVerItem.setOnClickListener(this::onClickAppVer);
        // TODO: Add the app version number after consultation

        // Setup Report Bug
        SettingsItem reportBugItem = new SettingsItem(settingsInfoView.findViewById(R.id.settings_info_report_bug));
        reportBugItem.setTitle(R.string.report_bug);
        reportBugItem.setOnClickListener(this::onClickReportBug);

        // Setup Author
        SettingsItem authorItem = new SettingsItem(settingsInfoView.findViewById(R.id.settings_info_author));
        authorItem.setTitle(R.string.author);
        authorItem.setDesc(R.string.author_name);
        authorItem.setOnClickListener(this::onClickAuthor);

        // Setup Disclaimer
        SettingsItem disclaimerItem = new SettingsItem(settingsInfoView.findViewById(R.id.settings_info_disclaimer));
        disclaimerItem.setTitle(R.string.disclaimer);
        disclaimerItem.setOnClickListener(this::onClickDisclaimer);

        // Setup License
        SettingsItem licenseItem = new SettingsItem(settingsInfoView.findViewById(R.id.settings_info_license));
        licenseItem.setTitle(R.string.license);
        licenseItem.setOnClickListener(this::onClickLicense);

        return settingsInfoView;
    }
}
