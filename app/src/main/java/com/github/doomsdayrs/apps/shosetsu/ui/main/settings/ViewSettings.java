package com.github.doomsdayrs.apps.shosetsu.ui.main.settings;
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
 * @author github.com/doomsdayrs
 */

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;

import java.util.ArrayList;
import java.util.List;

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

    CheckBox nightMode;
    Spinner paragraphSpacing;
    Spinner textSize;
    Spinner indentSize;

    public ViewSettings() {
    }

    @Nullable
    @Override
    public android.view.View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "ViewSettings");
        android.view.View view = inflater.inflate(R.layout.settings_view, container, false);
        nightMode = view.findViewById(R.id.reader_nightMode_checkbox);
        paragraphSpacing = view.findViewById(R.id.reader_paragraphSpacing);
        textSize = view.findViewById(R.id.reader_textSize);
        indentSize = view.findViewById(R.id.reader_indentSize);

        nightMode.setChecked(!SettingsController.isReaderLightMode());
        nightMode.setOnCheckedChangeListener((buttonView, isChecked) -> SettingsController.swapReaderColor());

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
                            SettingsController.setTextSize(size);
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
                            SettingsController.changeParagraphSpacing(i);
                            adapterView.setSelection(i);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                SettingsController.changeParagraphSpacing(spaceBack);
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
                            SettingsController.changeIndentSize(i);
                            adapterView.setSelection(i);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                SettingsController.changeIndentSize(spaceBack);
                indentSize.setSelection(Settings.indentSize);
            }
        }

        return view;
    }
}
