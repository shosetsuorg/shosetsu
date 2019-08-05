package com.github.doomsdayrs.apps.shosetsu.ui.settings.types;
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
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;

public class BackupSettings extends Fragment {


    public BackupSettings() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "BackupSettings");
        View view = inflater.inflate(R.layout.settings_backup, container, false);
        Button backup = view.findViewById(R.id.settings_backup_now);
        backup.setOnClickListener(view1 -> new Database.backUP(getContext()).execute());
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
                        new Database.restore(path.substring(path.indexOf(":") + 1), getContext()).execute();
                    } else
                        Toast.makeText(getContext(), "Invalid file to use!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}