package com.github.doomsdayrs.apps.shosetsu.backend.tracking

import android.content.res.Resources
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
 * Shosetsu
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
enum class Trackers(private val internalName: String, val id: Int) {
    ANILIST(com.github.doomsdayrs.apps.shosetsu.backend.tracking.Trackers.Companion.getString(R.string.anilist), 1),
    MYANIMELIST(com.github.doomsdayrs.apps.shosetsu.backend.tracking.Trackers.Companion.getString(R.string.myanimelist), 2);

    override fun toString(): String {
        return "Trackers{" +
                "name='" + internalName + '\'' +
                ", id=" + id +
                '}'
    }

    companion object {
        private fun getString(id: Int): String {
            return Resources.getSystem().getString(id)
        }
    }

}