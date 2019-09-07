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
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.Update;
import com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters.UpdatedChaptersAdapter;

import java.util.ArrayList;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseUpdates.getTimeBetween;

/**
 * shosetsu
 * 20 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class UpdateFragment extends Fragment {
    public long date = -1;
    private ArrayList<String> novels = new ArrayList<>();

    private ArrayList<Update> updates = new ArrayList<>();
    private RecyclerView recyclerView;

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
            if (savedInstanceState != null)
                date = savedInstanceState.getLong("date");

        try {
            updates = getTimeBetween(date, date + 86399999);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Update update : updates)
            if (!novels.contains(update.NOVEL_URL))
                novels.add(update.NOVEL_URL);

        recyclerView = view.findViewById(R.id.recycler_update);
        chapterSetUp();

        Log.d("Updates on this day: ", "" + updates.size());
        return view;
    }

    private void chapterSetUp() {
        if (recyclerView != null) {
            UpdatedChaptersAdapter updatersAdapter = new UpdatedChaptersAdapter(updates, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(updatersAdapter);
            recyclerView.post(updatersAdapter::notifyDataSetChanged);
        }
    }
}
