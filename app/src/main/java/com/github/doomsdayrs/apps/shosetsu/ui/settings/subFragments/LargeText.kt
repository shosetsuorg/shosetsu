package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.android.synthetic.main.large_reader.*
import java.io.BufferedReader
import java.io.InputStream
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
class LargeText : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.large_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var str = ""
        if (activity != null) {
            val inputStream: InputStream = activity!!.assets.open("license.text")
            val buf = StringBuffer()
            try {
                val reader = BufferedReader(InputStreamReader(inputStream))
                while (reader.readLine().also { str = it } != null) {
                    buf.append(str + "\n")
                }
            } finally {
                try {
                    inputStream.close()
                } catch (ignore: Throwable) {
                }
            }
        }
        textView.text = str
    }

}