package com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders;
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

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.NovelFragmentChapterViewHideBar;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;

/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NewChapterView extends RecyclerView.ViewHolder {
    public final NewChapterReader newChapterReader;
    public String chapterURL;
    public int chapterID;

    public ScrollView scrollView;

    //public View coverView;
    // public ViewPager2 viewPager2;
    //public NewReader currentReader;
    private TextView textView;

    public boolean ready;
    public String unformattedText;
    public String text;


    @SuppressLint("ClickableViewAccessibility")
    public NewChapterView(NewChapterReader newChapterReader, @NonNull View itemView) {
        super(itemView);
        this.newChapterReader = newChapterReader;
        scrollView = itemView.findViewById(R.id.scrollView);
        textView = itemView.findViewById(R.id.textView);
        textView.setOnClickListener(new NovelFragmentChapterViewHideBar(newChapterReader.toolbar));
        //viewPager2 = itemView.findViewById(R.id.viewpager);
        //coverView = itemView.findViewById(R.id.viewCover);
        //coverView.setOnTouchListener((view, motionEvent) -> true);
    }


    public void setChapterURL(String chapterURL) {
        this.chapterURL = chapterURL;
    }

    public void setChapterID(int chapterID) {
        this.chapterID = chapterID;
    }

    public void setUpReader() {
        scrollView.setBackgroundColor(Settings.ReaderTextBackgroundColor);
        if (unformattedText != null) {
            StringBuilder replaceSpacing = new StringBuilder("\n");
            for (int x = 0; x < Settings.paragraphSpacing; x++)
                replaceSpacing.append("\n");

            for (int x = 0; x < Settings.indentSize; x++)
                replaceSpacing.append("\t");

            text = unformattedText.replaceAll("\n", replaceSpacing.toString());
            if (text.length() > 100)
                Log.d("TextSet", text.substring(0, 100).replace("\n", "\\n"));
            else if (text.length() > 0)
                Log.d("TextSet", text.substring(0, text.length() - 1).replace("\n", "\\n"));
            textView.setText(text);
            // viewPager2.post(() -> currentReader.setText(text));
        }
    }
}
