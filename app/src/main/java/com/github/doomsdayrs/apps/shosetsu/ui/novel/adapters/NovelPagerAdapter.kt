package com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters

import android.util.Log
import android.widget.ArrayAdapter
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentInfo
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context

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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class NovelPagerAdapter(router: NovelController, private val fragments: List<Controller>) : RouterPagerAdapter(router) {
    companion object {
        const val INFO_CONTROLLER = 0
        const val CHAPTERS_CONTROLLER = 1
        const val TRACK_CONTROLLER = 2
    }

    private val titles = ArrayAdapter(router.context!!, android.R.layout.simple_spinner_item, router.resources!!.getStringArray(R.array.novel_fragment_names))

    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) {
            Log.d("SwapScreen", fragments[position].toString())
            val controller = when (position) {
                INFO_CONTROLLER -> {
                    NovelFragmentInfo()
                }
                CHAPTERS_CONTROLLER -> {
                    NovelFragmentChapters()
                }
                else -> error("Wrong position $position")
            }
            router.setRoot(RouterTransaction.with(controller))
        }
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles.getItem(position)
    }

}