package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import kotlinx.android.synthetic.main.large_reader.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder


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
class LicenseReader : Fragment() {
    private var message = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("m", message)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Utilities.setActivityTitle(activity, "License")
        if (savedInstanceState != null) message = savedInstanceState.getString("m", "")
        return inflater.inflate(R.layout.large_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (message.isEmpty()) {
            val string = StringBuilder()
            if (activity != null) {
                var reader: BufferedReader? = null;
                try {
                    reader = BufferedReader(InputStreamReader(activity!!.assets.open("license.txt")));
                    // do reading, usually loop until end of file reading
                    var mLine: String? = reader.readLine()
                    while (mLine != null) {
                        Log.d("LargeText", "Line:\t$mLine")
                        string.append("\n").append(mLine)
                        mLine = reader.readLine()
                    }
                } catch (e: IOException) {
                    //log the exception
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (e: IOException) {
                            //log the exception
                        }
                    }
                }
            }
            message = string.toString()
        }
        textView.text = message
    }

}