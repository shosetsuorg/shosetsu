package com.github.doomsdayrs.apps.shosetsu.ui.novel.listeners

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.github.doomsdayrs.apps.shosetsu.ui.novel.async.NewNovelLoader
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentInfo

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
 * 06 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class NovelFragmentUpdate(private val novelFragmentInfo: NovelFragmentInfo) : OnRefreshListener {
    override fun onRefresh() {
        if (novelFragmentInfo.novelFragment != null && novelFragmentInfo.novelFragment!!.formatter != null) NewNovelLoader(novelFragmentInfo.novelFragment!!.novelURL, novelFragmentInfo.novelFragment!!.novelID, novelFragmentInfo.novelFragment!!.formatter!!, novelFragmentInfo.novelFragment, false)
                .execute()
    }

}