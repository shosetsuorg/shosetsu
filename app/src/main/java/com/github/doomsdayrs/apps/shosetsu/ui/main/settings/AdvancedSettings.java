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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;

import java.util.ArrayList;
import java.util.List;

//TODO add text size options
public class AdvancedSettings extends Fragment {
    static final List<String> strings = new ArrayList<>();

    static {
        strings.add("Light");
        strings.add("Dark");
        strings.add("Night");
    }

    Spinner spinner;

    public AdvancedSettings() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "ViewSettings");
        View view = inflater.inflate(R.layout.settings_advanced, container, false);
        spinner = view.findViewById(R.id.settings_advanced_spinner);
        // Creating adapter for spinner
        if (getContext() != null) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, strings);
            spinner.setAdapter(dataAdapter);
            spinner.setSelection(Settings.themeMode);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 0 && i <= 2) {
                    SettingsController.changeMode(getActivity(), i);
                    adapterView.setSelection(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return view;
    }
}