package com.github.doomsdayrs.apps.shosetsu.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.backend.settings.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsCard;

import java.util.ArrayList;
import java.util.Objects;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsCardViewHolder> {
    private final ArrayList<SettingsCard> settingsCards;
    private final FragmentManager fragmentManager;

    public SettingsAdapter(ArrayList<SettingsCard> settingsCards, FragmentManager fragmentManager) {
        this.settingsCards = settingsCards;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public SettingsCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_settings_card, viewGroup, false);
        return new SettingsCardViewHolder(view, fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsCardViewHolder settingsCardViewHolder, int i) {
        SettingsCard settingsCard = settingsCards.get(i);
        settingsCardViewHolder.setType(settingsCard.ID);
    }

    @Override
    public int getItemCount() {
        return settingsCards.size();
    }

    static class SettingsCardViewHolder extends RecyclerView.ViewHolder {
        final TextView library_card_title;
        final CardView cardView;
        final FragmentManager fragmentManager;

        SettingsCardViewHolder(@NonNull View itemView, FragmentManager fragmentManager) {
            super(itemView);
            library_card_title = itemView.findViewById(R.id.recycler_settings_title);
            cardView = itemView.findViewById(R.id.settings_card);
            this.fragmentManager = fragmentManager;
        }

        void setType(Types type) {
            cardView.setOnClickListener(new onSettingsClick(type, fragmentManager));
            library_card_title.setText(type.toString());
        }
    }

    static class onSettingsClick implements View.OnClickListener {
        final Types type;
        final FragmentManager fragmentManager;

        onSettingsClick(Types id, FragmentManager fragmentManager) {
            type = id;
            this.fragmentManager = fragmentManager;
        }

        @Override
        public void onClick(View v) {
            switch (type) {
                case VIEW: {
                    Toast.makeText(v.getContext(), "View", Toast.LENGTH_SHORT).show();
                    fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, new viewSettings()).commit();
                }
                break;
                case CREDITS: {
                    Toast.makeText(v.getContext(), "Credits", Toast.LENGTH_SHORT).show();
                }
                break;
                case ADVANCED: {
                    Toast.makeText(v.getContext(), "Advanced", Toast.LENGTH_SHORT).show();
                    fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, new advancedSettings()).commit();
                }
                break;
                case DOWNLOAD: {
                    Toast.makeText(v.getContext(), "Download", Toast.LENGTH_SHORT).show();
                    fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, new downloadSettings()).commit();
                }
                break;
                case BACKUP: {
                    Toast.makeText(v.getContext(), "Backup", Toast.LENGTH_SHORT).show();
                    fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, new backupSettings()).commit();
                }
                break;
                default: {
                }
            }
        }
    }

    //TODO Add text size options
    public static class viewSettings extends Fragment {
        CheckBox checkBox;

        public viewSettings() {
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.d("OnCreateView", "ViewSettings");
            View view = inflater.inflate(R.layout.fragment_settings_view, container, false);
            checkBox = view.findViewById(R.id.reader_nightMode_checkbox);
            checkBox.setChecked(!SettingsController.isReaderLightMode());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> SettingsController.swapReaderColor());
            return view;
        }
    }

    public static class backupSettings extends Fragment {
        Button button;

        public backupSettings() {
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.d("OnCreateView", "BackupSettings");
            View view = inflater.inflate(R.layout.fragment_settings_backup, container, false);
            button = view.findViewById(R.id.fragment_settings_backup_now);
            button.setOnClickListener(view1 -> Database.backupDatabase());
            return view;
        }
    }

    public static class downloadSettings extends Fragment {

        TextView textView;

        public downloadSettings() {
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.d("OnCreateView", "DownloadSettings");
            View view = inflater.inflate(R.layout.fragment_settings_download, container, false);
            textView = view.findViewById(R.id.fragment_settings_download_dir);
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

    //TODO Add text size options
    public static class advancedSettings extends Fragment {
        CheckBox checkBox;

        public advancedSettings() {
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.d("OnCreateView", "ViewSettings");
            View view = inflater.inflate(R.layout.fragment_settings_view, container, false);
            checkBox = view.findViewById(R.id.reader_nightMode_checkbox);
            checkBox.setChecked(!SettingsController.isReaderLightMode());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> SettingsController.swapReaderColor());
            return view;
        }
    }

}
