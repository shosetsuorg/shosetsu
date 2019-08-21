package com.github.doomsdayrs.apps.shosetsu.ui.updates;
/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.Update;
import com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters.UpdatersAdapter;

import java.util.ArrayList;

/**
 * shosetsu
 * 20 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class UpdateFragment extends Fragment {
    public long date = -1;
    ArrayList<Update> updates = new ArrayList<>();
    RecyclerView recyclerView;
    UpdatersAdapter updatersAdapter;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("date", date);
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.updates_list, container, false);
        if (date == -1)
            date = savedInstanceState.getLong("date");

        updates = Database.DatabaseUpdates.getTimeBetween(date + 86400000, date);
        recyclerView = view.findViewById(R.id.recycler_update);
        updatersAdapter = new UpdatersAdapter(updates, getActivity());
        chapterSetUp();

        Log.d("Updates on this day: ", updates.toString());
        return view;
    }

    public void chapterSetUp() {
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(false);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            updatersAdapter.setHasStableIds(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(updatersAdapter);
            recyclerView.post(updatersAdapter::notifyDataSetChanged);
        }
    }
}
