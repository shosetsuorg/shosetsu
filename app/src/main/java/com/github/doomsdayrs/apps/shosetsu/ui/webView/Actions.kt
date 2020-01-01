package com.github.doomsdayrs.apps.shosetsu.ui.webView

import java.util.*

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
enum class Actions(val action: Int) {
    VIEW(0), CLOUD_FLARE(1);

    companion object {
        @kotlin.jvm.JvmField
        val actions = ArrayList<Actions>()

        init {
            actions.add(VIEW)
            actions.add(CLOUD_FLARE)
        }
    }

}