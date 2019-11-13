package com.github.doomsdayrs.apps.shosetsu.backend;
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

import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * shosetsu
 * 12 / 11 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class ErrorView {
    public final Activity activity;
    public final ConstraintLayout errorView;
    public final TextView errorMessage;
    public final Button errorButton;

    public ErrorView(Activity activity, ConstraintLayout errorView, TextView errorMessage, Button errorButton) {
        this.activity = activity;
        this.errorView = errorView;
        this.errorMessage = errorMessage;
        this.errorButton = errorButton;
    }
}
