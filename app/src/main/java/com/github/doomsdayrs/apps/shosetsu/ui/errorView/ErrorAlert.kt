package com.github.doomsdayrs.apps.shosetsu.ui.errorView

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.TextView
import com.github.doomsdayrs.apps.shosetsu.R

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class ErrorAlert(context: Context, retryAction: (dialog: DialogInterface?, which: Int) -> Unit = { dialog: DialogInterface?, _: Int -> dialog?.dismiss() }) : AlertDialog.Builder(context) {

    private val view = (context as Activity).layoutInflater.inflate(R.layout.error_view, null)!!
    private val messageView: TextView = view.findViewById(R.id.error_message)

    init {
        setPositiveButton(R.string.retry, retryAction)
        setView(view)
    }

    override fun setMessage(message: CharSequence?): ErrorAlert {
        messageView.text = message
        return this
    }

}