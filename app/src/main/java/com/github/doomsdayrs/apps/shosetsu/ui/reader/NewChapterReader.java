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

import android.os.BaseBundle;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters.NewChapterReaderAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewChapterView;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;

import java.util.List;


/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NewChapterReader extends AppCompatActivity {
    //private final Utilities.DemarkAction[] demarkActions = {new TextSizeChange(this), new ParaSpacingChange(this), new IndentChange(this), new ReaderChange(this)};


    // Order of values. Small,Medium,Large
    private final MenuItem[] textSizes = new MenuItem[3];
    // Order of values. Non,Small,Medium,Large
    private final MenuItem[] paragraphSpaces = new MenuItem[4];
    // Order of values. Non,Small,Medium,Large
    private final MenuItem[] indentSpaces = new MenuItem[4];
    // Order of values. Default, Markdown
    private final MenuItem[] readers = new MenuItem[2];

    ViewPager2 viewPager2;
    public Toolbar toolbar;

    // NovelData
    public int[] chapterIDs;
    public Formatter formatter;
    public int novelID;
    public NewChapterView currentView;
    int currentChapterID = -1;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outPersistentState.putIntArray("chapters", chapterIDs);
        outPersistentState.putInt("novelID", novelID);
        outPersistentState.putInt("formatter", formatter.getID());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("OnCreate", "Only Bundle");
        onCreate(savedInstanceState, null);
    }

    private void restoreInstanceState(BaseBundle bundle) {
        formatter = DefaultScrapers.getByID(bundle.getInt("formatter"));
        novelID = bundle.getInt("novelID");
        chapterIDs = bundle.getIntArray("chapters");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        setContentView(R.layout.new_chapter_reader);

        viewPager2 = findViewById(R.id.viewpager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (persistentState != null) {
            restoreInstanceState(persistentState);
        } else if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
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
        if (currentChapterID != -1)
            viewPager2.setCurrentItem(findCurrentPosition(currentChapterID));
    }

    public int findCurrentPosition(int id) {
        for (int x = 0; x < chapterIDs.length; x++)
            if (chapterIDs[x] == id)
                return x;
        return -1;
    }
}
