package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.ViewedController
import java.io.BufferedReader
import java.io.InputStreamReader


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
class TextAssetReader(val bundleI: Bundle) : ViewedController(bundleI) {
    companion object {
        const val logID = "TextAssetReader"
        const val BUNDLE_KEY: String = "target"
    }

    enum class Target(val bundle: Bundle) {
        LICENSE(bundleOf(Pair(BUNDLE_KEY, "license"))),
        DISCLAIMER(bundleOf(Pair(BUNDLE_KEY, "disclaimer")));
    }

    override val layoutRes: Int = R.layout.large_reader
    private var type: String = ""
    private var message: String = ""

    fun handleB() {
        val string = StringBuilder()
        Log.d(logID, "Setting Message")
        type = bundleI.getString(BUNDLE_KEY, "license")
        activity?.let {
            val reader = BufferedReader(InputStreamReader(activity!!.assets.open("$type.txt")))
            // do reading, usually loop until end of file reading
            var mLine: String? = reader.readLine()
            while (mLine != null) {
                string.append("\n").append(mLine)
                mLine = reader.readLine()
            }

            reader.close()
        }
        message = string.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("m", message)
        outState.putString("t", type)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        message = savedInstanceState.getString("m", "")
        type = savedInstanceState.getString("t", "")
    }

    override fun onViewCreated(view: View) {
        if (message.isEmpty())
            handleB()
        Utilities.setActivityTitle(activity, type.capitalize())
        view.findViewById<TextView>(R.id.textView).text = message
    }

}