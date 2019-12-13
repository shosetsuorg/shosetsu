package com.github.doomsdayrs.apps.shosetsu.ui.reader;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters.NewChapterReaderAdapter;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;

import java.util.List;

/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NewChapterReader extends AppCompatActivity {
    public int[] chapterIDs;
    ViewPager2 viewPager2;
    Formatter formatter;
    int novelID, currentChapterID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_chapter_reader);

        viewPager2 = findViewById(R.id.viewpager);


        if (savedInstanceState != null) {
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"));
            novelID = savedInstanceState.getInt("novelID");
            chapterIDs = savedInstanceState.getIntArray("chapters");
        } else {
            chapterIDs = getIntent().getIntArrayExtra("chapters");
            {
                int chapterID;
                chapterID = getIntent().getIntExtra("chapterID", -1);
                currentChapterID = chapterID;
            }
            novelID = getIntent().getIntExtra("novelID", -1);
            formatter = DefaultScrapers.getByID(getIntent().getIntExtra("formatter", -1));
        }

        if (chapterIDs == null) {
            List<Integer> integers = Database.DatabaseChapter.getChaptersOnlyIDs(novelID);
            chapterIDs = new int[integers.size()];
            for (int x = 0; x < integers.size(); x++)
                chapterIDs[x] = integers.get(x);
        }

        NewChapterReaderAdapter newChapterReaderAdapter = new NewChapterReaderAdapter(this);
        viewPager2.setAdapter(newChapterReaderAdapter);
        viewPager2.setCurrentItem(findCurrentPosition(currentChapterID));
    }

    public int findCurrentPosition(int id) {
        for (int x = 0; x < chapterIDs.length; x++)
            if (chapterIDs[x] == id)
                return x;
        return -1;
    }
}
