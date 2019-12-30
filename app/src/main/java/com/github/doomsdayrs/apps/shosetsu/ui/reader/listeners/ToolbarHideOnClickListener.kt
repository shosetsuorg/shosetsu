package com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.Toolbar

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
 */
/**
 * Shosetsu
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
class ToolbarHideOnClickListener(private val toolbar: Toolbar?) : View.OnClickListener {
    override fun onClick(v: View) {
        toolbar?.let {
            if (it.y == 0f) toolbar.animate().translationY(-it.bottom.toFloat()).setInterpolator(AccelerateInterpolator()).start() else it.animate().translationY(0f).setInterpolator(DecelerateInterpolator()).start()
        }
    }
}