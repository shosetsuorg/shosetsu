package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup;
/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
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
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.async.BackupProcess;
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.async.RestoreProcess;

public class BackupSettings extends Fragment {


    public BackupSettings() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "BackupSettings");
        View view = inflater.inflate(R.layout.settings_backup, container, false);
        Button backup = view.findViewById(R.id.settings_backup_now);
        backup.setOnClickListener(view1 -> new BackupProcess(getContext()).execute());
        Button restore = view.findViewById(R.id.settings_restore_now);
        restore.setOnClickListener(view1 -> performFileSelection());
        return view;
    }


    private void performFileSelection() {
        Toast.makeText(getContext(), "Please make sure this is on the main storage, SD card storage is not functional yet", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 69);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 69 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null && data.getData().getPath() != null) {
                String path = data.getData().getPath();
                System.out.println(path);
                int i = path.lastIndexOf(".");
                if (i > -1) {
                    String fileEnding = path.substring(i + 1);
                    if (fileEnding.equalsIgnoreCase("shoback")) {
                        Log.i("Selected Folder", "Uri: " + path);
                        new RestoreProcess(path.substring(path.indexOf(":") + 1), getContext()).execute();
                    } else
                        Toast.makeText(getContext(), "Invalid file to use!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}