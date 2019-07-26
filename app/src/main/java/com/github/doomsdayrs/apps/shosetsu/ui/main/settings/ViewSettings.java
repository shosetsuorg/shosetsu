package com.github.doomsdayrs.apps.shosetsu.ui.main.settings;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsItem;

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
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 * @author github.com/hXtreme
 */

// TODO: Migrate to using PreferenceScreen and PreferenceGroup.
public class ViewSettings extends Fragment {
    static final List<String> textSizes = new ArrayList<>();
    static final List<String> paragraphSpaces = new ArrayList<>();
    static final List<String> indentSizes = new ArrayList<>();

    static {
        textSizes.add("Small");
        textSizes.add("Medium");
        textSizes.add("Large");

        paragraphSpaces.add("None");
        paragraphSpaces.add("Small");
        paragraphSpaces.add("Medium");
        paragraphSpaces.add("Large");

        indentSizes.add("None");
        indentSizes.add("Small");
        indentSizes.add("Medium");
        indentSizes.add("Large");
    }

    Spinner paragraphSpacing;
    Spinner textSize;
    Spinner indentSize;


    private void onClickNIghtMode(View v){
        SettingsItem nightMOdeItem = new SettingsItem(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(R.string.reader_night_mode);
        String[] states = {getString(R.string.on), getString(R.string.off)};
        builder.setItems(states,
                (dialogInterface, i) -> {
                    if (i == 0) SettingsController.INSTANCE.setNightNode();
                    else SettingsController.INSTANCE.unsetNightMode();

                    int nightModeStatus = SettingsController.INSTANCE.isReaderNightMode()?
                            R.string.on : R.string.off;
                    nightMOdeItem.setDesc(nightModeStatus);
                    nightMOdeItem.invalidate();
                }
        );
        builder.show();
    }

    private void onClickTextSize(View v){

    }

    private void onClickParaSpacing(View v){

    }

    private void onClickParaIndent(View v){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "ViewSettings");
        View settingsReaderView = inflater.inflate(R.layout.settings_view, container, false);

        // Setup Night Mode
        SettingsItem nightModeItem = new SettingsItem(settingsReaderView.findViewById(R.id.settings_reader_night_mode));
        nightModeItem.setTitle(R.string.reader_night_mode);
        int nightModeStatus = SettingsController.INSTANCE.isReaderNightMode()?
                R.string.on : R.string.off;
        nightModeItem.setDesc(nightModeStatus);
        nightModeItem.setOnClickListener(this::onClickNIghtMode);

        // Setup Text size
        SettingsItem textSizeItem = new SettingsItem(settingsReaderView.findViewById(R.id.settings_reader_text_size));
        textSizeItem.setTitle(R.string.text_size);
        // TODO: Get current Text size

        // Setup Paragraph Spacing
        SettingsItem paraSpacingItem = new SettingsItem(settingsReaderView.findViewById(R.id.settings_reader_para_spacing));
        paraSpacingItem.setTitle(R.string.spacing);
        // TODO: Get current Paragraph spacing

        // Setup Indent Size
        SettingsItem paraIndentItem = new SettingsItem(settingsReaderView.findViewById(R.id.settings_reader_para_indent));
        paraIndentItem.setTitle(R.string.indent_size);
        // TODO: Get current Indent size

        paragraphSpacing = settingsReaderView.findViewById(R.id.reader_paragraphSpacing);
        textSize = settingsReaderView.findViewById(R.id.reader_textSize);
        indentSize = settingsReaderView.findViewById(R.id.reader_indentSize);


        if (getContext() != null) {
            {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, textSizes);
                textSize.setAdapter(dataAdapter);
                switch ((int) Settings.ReaderTextSize) {
                    default:
                    case 14:
                        textSize.setSelection(0);
                        break;
                    case 17:
                        textSize.setSelection(1);
                        break;
                    case 20:
                        textSize.setSelection(2);
                        break;
                }

                textSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, android.view.View view, int i, long l) {
                        if (i >= 0 && i <= 2) {
                            int size = 14;
                            switch (i) {
                                case 0:
                                    break;
                                case 1:
                                    size = 17;
                                    break;
                                case 2:
                                    size = 20;
                                    break;
                            }
                            SettingsController.INSTANCE.setTextSize(size);
                            adapterView.setSelection(i);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
            {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, paragraphSpaces);
                //TODO figure out why the itemSelectedListner runs without being selected
                int spaceBack = Settings.paragraphSpacing;

                switch (Settings.paragraphSpacing) {
                    case 0:
                        paragraphSpacing.setSelection(0);
                        break;
                    case 1:
                        paragraphSpacing.setSelection(1);
                        break;
                    case 2:
                        paragraphSpacing.setSelection(2);
                        break;
                    case 3:
                        paragraphSpacing.setSelection(3);
                        break;
                }

                paragraphSpacing.setAdapter(dataAdapter);

                paragraphSpacing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, android.view.View view, int i, long l) {
                        if (i >= 0 && i <= 3) {
                            SettingsController.INSTANCE.changeParagraphSpacing(i);
                            adapterView.setSelection(i);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                SettingsController.INSTANCE.changeParagraphSpacing(spaceBack);
                switch (Settings.paragraphSpacing) {
                    case 0:
                        paragraphSpacing.setSelection(0);
                        break;
                    case 1:
                        paragraphSpacing.setSelection(1);
                        break;
                    case 2:
                        paragraphSpacing.setSelection(2);
                        break;
                    case 3:
                        paragraphSpacing.setSelection(3);
                        break;
                }

            }

            {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, indentSizes);
                //TODO figure out why the itemSelectedListner runs without being selected
                int spaceBack = Settings.indentSize;
                indentSize.setSelection(Settings.indentSize);
                indentSize.setAdapter(dataAdapter);
                indentSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, android.view.View view, int i, long l) {
                        if (i >= 0 && i <= 3) {
                            SettingsController.INSTANCE.changeIndentSize(i);
                            adapterView.setSelection(i);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                SettingsController.INSTANCE.changeIndentSize(spaceBack);
                indentSize.setSelection(Settings.indentSize);
            }
        }

        return settingsReaderView;
    }
}
