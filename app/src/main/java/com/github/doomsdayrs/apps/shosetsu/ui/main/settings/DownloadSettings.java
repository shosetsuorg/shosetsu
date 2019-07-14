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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;

import java.util.Objects;

public class DownloadSettings extends Fragment {

    TextView textView;

    public DownloadSettings() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "DownloadSettings");
        View view = inflater.inflate(R.layout.settings_download, container, false);
        textView = view.findViewById(R.id.settings_download_dir);
        textView.setText(Download_Manager.shoDir);
        textView.setOnClickListener(view1 -> performFileSearch());
        return view;
    }

    private void setDir(String dir) {
        SettingsController.download.edit().putString("dir", dir).apply();
        Download_Manager.shoDir = dir;
        textView.setText(dir);
    }

    void performFileSearch() {
        Toast.makeText(getContext(), "Please make sure this is on the main storage, SD card storage is not functional yet", Toast.LENGTH_LONG).show();
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(Intent.createChooser(i, "Choose directory"), 42);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (data != null) {
                String path = Objects.requireNonNull(data.getData()).getPath();
                Log.i("Selected Folder", "Uri: " + path);
                setDir(path.substring(Objects.requireNonNull(path).indexOf(":") + 1));
            }
        }

    }
}