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
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters.NewChapterReaderAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions.IndentChange;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions.ParaSpacingChange;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions.ReaderChange;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions.TextSizeChange;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewChapterView;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.demarkMenuItems;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isReaderNightMode;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isTapToScroll;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInBrowser;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInWebview;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setTextSize;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.swapReaderColor;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.toggleBookmarkChapter;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.toggleTapToScroll;


/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NewChapterReader extends AppCompatActivity {
    private final Utilities.DemarkAction[] demarkActions = {new TextSizeChange(this), new ParaSpacingChange(this), new IndentChange(this), new ReaderChange(this)};
    // Order of values. Small,Medium,Large
    private final MenuItem[] textSizes = new MenuItem[3];
    // Order of values. Non,Small,Medium,Large
    private final MenuItem[] paragraphSpaces = new MenuItem[4];
    // Order of values. Non,Small,Medium,Large
    private final MenuItem[] indentSpaces = new MenuItem[4];
    // Order of values. Default, Markdown
    private final MenuItem[] readers = new MenuItem[2];
    MenuItem bookmark, tap_to_scroll;

    ViewPager viewPager;
    public Toolbar toolbar;


    // NovelData
    public int[] chapterIDs;
    public Formatter formatter;
    public int novelID;
    public NewChapterView currentView;
    int currentChapterID = -1;


    /**
     * Creates the option menu (on the top toolbar)
     *
     * @param menu Menu reference to fill
     * @return if made
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.toolbar_chapter_view, menu);
        // Night mode
        menu.findItem(R.id.chapter_view_nightMode).setChecked(isReaderNightMode());

        //  Bookmark
        {
            bookmark = menu.findItem(R.id.chapter_view_bookmark);
            currentView.bookmarked = Database.DatabaseChapter.isBookMarked(currentView.CHAPTER_ID);
            updateBookmark();
        }


        // Tap To Scroll
        {
            tap_to_scroll = menu.findItem(R.id.tap_to_scroll);
            tap_to_scroll.setChecked(isTapToScroll());
        }


        // Text size
        {
            textSizes[0] = menu.findItem(R.id.chapter_view_textSize_small);
            textSizes[1] = menu.findItem(R.id.chapter_view_textSize_medium);
            textSizes[2] = menu.findItem(R.id.chapter_view_textSize_large);

            switch ((int) Settings.ReaderTextSize) {
                default:
                    setTextSize(14);
                case 14:
                    textSizes[0].setChecked(true);
                    break;
                case 17:
                    textSizes[1].setChecked(true);
                    break;
                case 20:
                    textSizes[2].setChecked(true);
                    break;
            }
        }

        // Paragraph Space
        {
            paragraphSpaces[0] = menu.findItem(R.id.chapter_view_paragraphSpace_none);
            paragraphSpaces[1] = menu.findItem(R.id.chapter_view_paragraphSpace_small);
            paragraphSpaces[2] = menu.findItem(R.id.chapter_view_paragraphSpace_medium);
            paragraphSpaces[3] = menu.findItem(R.id.chapter_view_paragraphSpace_large);

            paragraphSpaces[Settings.paragraphSpacing].setChecked(true);
        }

        // Indent Space
        {
            indentSpaces[0] = menu.findItem(R.id.chapter_view_indent_none);
            indentSpaces[1] = menu.findItem(R.id.chapter_view_indent_small);
            indentSpaces[2] = menu.findItem(R.id.chapter_view_indent_medium);
            indentSpaces[3] = menu.findItem(R.id.chapter_view_indent_large);

            indentSpaces[Settings.indentSize].setChecked(true);
        }

        /* Reader
        {

            readers[0] = menu.findItem(R.id.reader_0);
            readers[1] = menu.findItem(R.id.reader_1);
            readerType = getReaderType(novelID);

            switch (readerType) {
                case 1:
                    demarkMenuItems(readers, 1, null);
                    break;
                case 0:
                case -1:
                    demarkMenuItems(readers, 0, null);
                    break;
                case -2:
                default:
                    throw new RuntimeException("Invalid chapter?!? How are you reading this without the novel loaded in");
            }
        }*/
        return true;
    }

    public void updateBookmark() {
        if (bookmark != null)
            if (currentView.bookmarked)
                bookmark.setIcon(R.drawable.ic_bookmark_black_24dp);
            else bookmark.setIcon(R.drawable.ic_bookmark_border_black_24dp);
    }

    /**
     * What to do when an menu item is selected
     *
     * @param item item selected
     * @return true if processed
     */
    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        Log.d("item", item.toString());
        switch (item.getItemId()) {
            default:
                return false;
            case R.id.chapter_view_nightMode:
                if (!item.isChecked()) {
                    swapReaderColor();
                    currentView.setUpReader();
                } else {
                    swapReaderColor();
                    currentView.setUpReader();
                }
                item.setChecked(!item.isChecked());
                return true;
            case R.id.tap_to_scroll:
                tap_to_scroll.setChecked(toggleTapToScroll());
                return true;
            case R.id.chapter_view_bookmark:
                currentView.bookmarked = toggleBookmarkChapter(currentView.CHAPTER_ID);
                updateBookmark();
                return true;
            case R.id.chapter_view_textSize_small:
                demarkMenuItems(indentSpaces, 0, demarkActions[0]);
                return true;
            case R.id.chapter_view_textSize_medium:
                demarkMenuItems(textSizes, 1, demarkActions[0]);
                return true;
            case R.id.chapter_view_textSize_large:
                demarkMenuItems(textSizes, 2, demarkActions[0]);
                return true;

            case R.id.chapter_view_paragraphSpace_none:
                demarkMenuItems(paragraphSpaces, 0, demarkActions[1]);
                return true;
            case R.id.chapter_view_paragraphSpace_small:
                demarkMenuItems(paragraphSpaces, 1, demarkActions[1]);
                return true;
            case R.id.chapter_view_paragraphSpace_medium:
                demarkMenuItems(paragraphSpaces, 2, demarkActions[1]);
                return true;
            case R.id.chapter_view_paragraphSpace_large:
                demarkMenuItems(paragraphSpaces, 3, demarkActions[1]);
                return true;

            case R.id.chapter_view_indent_none:
                demarkMenuItems(indentSpaces, 0, demarkActions[2]);
                return true;
            case R.id.chapter_view_indent_small:
                demarkMenuItems(indentSpaces, 1, demarkActions[2]);
                return true;
            case R.id.chapter_view_indent_medium:
                demarkMenuItems(indentSpaces, 2, demarkActions[2]);
                return true;
            case R.id.chapter_view_indent_large:
                demarkMenuItems(indentSpaces, 3, demarkActions[2]);
                return true;

            case R.id.browser:
                openInBrowser(this, currentView.CHAPTER_URL);
                return true;
            case R.id.webview:
                openInWebview(this, currentView.CHAPTER_URL);
                return true;
            case R.id.reader_0:
                demarkMenuItems(readers, 0, demarkActions[3]);
                return true;
            case R.id.reader_1:
                demarkMenuItems(readers, 1, demarkActions[3]);
                return true;

        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outPersistentState.putIntArray("chapters", chapterIDs);
        outPersistentState.putInt("novelID", novelID);
        outPersistentState.putInt("formatter", formatter.getID());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_chapter_reader);

        viewPager = findViewById(R.id.viewpager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        NewChapterReaderAdapter newChapterReaderAdapter = new NewChapterReaderAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);
        viewPager.setAdapter(newChapterReaderAdapter);

        if (currentChapterID != -1)
            viewPager.setCurrentItem(findCurrentPosition(currentChapterID));
    }

    public int findCurrentPosition(int id) {
        for (int x = 0; x < chapterIDs.length; x++)
            if (chapterIDs[x] == id)
                return x;
        return -1;
    }
}
