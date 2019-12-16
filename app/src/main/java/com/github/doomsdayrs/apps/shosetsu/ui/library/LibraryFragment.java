package com.github.doomsdayrs.apps.shosetsu.ui.library;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Update_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.library.adapter.LibraryNovelAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.library.listener.LibrarySearchQuery;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.calculateNoOfColumns;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.serializeToString;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setActivityTitle;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.getNovelTitle;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class LibraryFragment extends Fragment {
    @NonNull
    public ArrayList<Integer> libraryNovelCards = new ArrayList<>();
    public ArrayList<Integer> selectedNovels = new ArrayList<>();
    public static boolean changedData = false;


    public boolean contains(@NonNull int i) {
        for (Integer I : selectedNovels)
            if (I == i)
                return true;
        return false;
    }


    private Context context;
    public RecyclerView recyclerView;
    public LibraryNovelAdapter libraryNovelCardsAdapter;
    public Menu menu;

    /**
     * Constructor
     */
    public LibraryFragment() {
        setHasOptionsMenu(true);
    }

    private void readFromDB() {
        libraryNovelCards = Database.DatabaseNovels.getIntLibrary();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            libraryNovelCards.sort((novel, t1) -> getNovelTitle(novel).compareTo(getNovelTitle(t1)));
        } else {
            bubbleSortA_Z();
        }
    }

    private void bubbleSortA_Z() {
        for (int i = libraryNovelCards.size() - 1; i > 1; i--) {
            for (int j = 0; j < i; j++) {
                if (getNovelTitle(libraryNovelCards.get(j)).compareTo(getNovelTitle(libraryNovelCards.get(j + 1))) > 0)
                    swapValues(j, j + 1);
            }
        }
    }

    private void swapValues(int indexOne, int indexTwo) {
        int i = libraryNovelCards.get(indexOne);
        libraryNovelCards.set(indexOne, libraryNovelCards.get(indexTwo));
        libraryNovelCards.set(indexTwo, i);
    }

    /**
     * Sets the cards to display
     */
    public void setLibraryCards(ArrayList<Integer> novelCards) {
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(false);
            RecyclerView.LayoutManager library_layoutManager;
            library_layoutManager = new GridLayoutManager(context, calculateNoOfColumns(getContext(), 200), RecyclerView.VERTICAL, false);

            libraryNovelCardsAdapter = new LibraryNovelAdapter(novelCards, this);
            recyclerView.setLayoutManager(library_layoutManager);
            recyclerView.setAdapter(libraryNovelCardsAdapter);
        }
    }

    @Nullable
    public MenuInflater getInflater() {
        return new MenuInflater(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Library", "Paused");
        selectedNovels = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        selectedNovels = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Library", "Resumed");
        if (LibraryFragment.changedData) {
            Log.d("Library", "Updating data");
            libraryNovelCards = Database.DatabaseNovels.getIntLibrary();
            changedData = !changedData;
        }
        libraryNovelCardsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntegerArrayList("selected", selectedNovels);
        outState.putIntegerArrayList("lib", libraryNovelCards);
    }

    /**
     * Creates view
     *
     * @param inflater           inflates layouts and shiz
     * @param container          container of this fragment
     * @param savedInstanceState save file
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setActivityTitle(getActivity(), "Library");
        Log.d("Library", "creating");
        if (savedInstanceState == null)
            readFromDB();
        else {
            ArrayList<Integer> novelIDs = savedInstanceState.getIntegerArrayList("lib"), selectedIDs = savedInstanceState.getIntegerArrayList("selected");
            if (novelIDs != null) {
                libraryNovelCards = novelIDs;
            }
            selectedNovels = selectedIDs;

        }
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        recyclerView = view.findViewById(R.id.fragment_library_recycler);
        this.context = Objects.requireNonNull(container).getContext();
        setLibraryCards(libraryNovelCards);
        return view;
    }


    /**
     * Creates the option menu
     *
     * @param menu     menu to fill
     * @param inflater inflater of layouts and shiz
     */
    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        this.menu = menu;
        menu.clear();
        if (selectedNovels.size() <= 0) {
            inflater.inflate(R.menu.toolbar_library, menu);
            SearchView searchView = (SearchView) menu.findItem(R.id.library_search).getActionView();
            if (searchView != null) {
                searchView.setOnQueryTextListener(new LibrarySearchQuery(this));
                searchView.setOnCloseListener(() -> {
                    setLibraryCards(libraryNovelCards);
                    return false;
                });
            }
        } else
            inflater.inflate(R.menu.toolbar_library_selected, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updater_now:
                Update_Manager.init(libraryNovelCards, context);
                return true;
            case R.id.chapter_select_all:
                for (int i : libraryNovelCards)
                    if (!contains(i))
                        selectedNovels.add(i);
                recyclerView.post(() -> libraryNovelCardsAdapter.notifyDataSetChanged());
                return true;

            case R.id.chapter_deselect_all:
                selectedNovels = new ArrayList<>();
                recyclerView.post(() -> libraryNovelCardsAdapter.notifyDataSetChanged());
                if (getInflater() != null)
                    onCreateOptionsMenu(menu, getInflater());
                return true;

            case R.id.remove_from_library:
                for (int i : selectedNovels) {
                    Database.DatabaseNovels.unBookmark(i);
                    libraryNovelCards.remove(i);
                }
                selectedNovels = new ArrayList<>();
                recyclerView.post(() -> libraryNovelCardsAdapter.notifyDataSetChanged());
                return true;

            case R.id.source_migrate:
                Intent intent = new Intent(getActivity(), MigrationView.class);
                try {
                    intent.putExtra("selected", serializeToString(selectedNovels));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.putExtra("target", 1);
                startActivity(intent);
                return true;

        }
        return false;
    }
}
