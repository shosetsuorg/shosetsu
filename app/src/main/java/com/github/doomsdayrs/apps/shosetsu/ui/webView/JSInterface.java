package com.github.doomsdayrs.apps.shosetsu.ui.webView;

import android.app.AlertDialog;
import android.content.Context;
import android.webkit.JavascriptInterface;

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
 * shosetsu
 * 05 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class JSInterface {
    private final Context ctx;

    JSInterface(Context ctx) {
        this.ctx = ctx;
    }

    @JavascriptInterface
    public void showHTML(String html) {
        new AlertDialog.Builder(ctx).setTitle("HTML").setMessage(html)
                .setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
    }
}
