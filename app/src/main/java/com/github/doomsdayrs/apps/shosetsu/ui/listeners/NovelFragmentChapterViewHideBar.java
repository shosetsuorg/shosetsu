package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragmentChapterViewHideBar implements View.OnClickListener {
    private final Toolbar toolbar;
    private boolean visible = true;

    public NovelFragmentChapterViewHideBar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    @Override
    public void onClick(View v) {
        if (visible) {
            toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            visible = !visible;
        } else {
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
            visible = !visible;
        }
    }
}