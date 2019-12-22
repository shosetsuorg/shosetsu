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

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader;

/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
public abstract class NewReader extends RecyclerView.ViewHolder {
    final NewChapterReader newChapterReader;

    NewReader(@NonNull View itemView, NewChapterReader newChapterReader) {
        super(itemView);
        this.newChapterReader = newChapterReader;
    }

    public abstract void setText(String text);

    public abstract void bind();
}
