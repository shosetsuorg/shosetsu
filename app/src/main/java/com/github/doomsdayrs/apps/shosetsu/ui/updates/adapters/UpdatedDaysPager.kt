package com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseUpdates
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdateFragment
import org.joda.time.DateTime
import java.util.*

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
 */ /**
 * shosetsu
 * 20 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatedDaysPager(fm: FragmentManager, behavior: Int, private val fragments: ArrayList<UpdateFragment>) : FragmentPagerAdapter(fm, behavior) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val dateTime = DateTime(fragments[position].date)
        if (dateTime == DatabaseUpdates.trimDate(DateTime(System.currentTimeMillis()))) {
            return "Today"
        } else if (dateTime == DatabaseUpdates.trimDate(DateTime(System.currentTimeMillis())).minusDays(1)) {
            return "Yesterday"
        }
        return dateTime.dayOfMonth.toString() + "/" + dateTime.monthOfYear + "/" + dateTime.year
    }

    override fun getCount(): Int {
        return fragments.size
    } // --Commented out by Inspection START (12/22/19 11:10 AM):

}