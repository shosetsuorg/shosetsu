package app.shosetsu.android.view.uimodels.settings

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import app.shosetsu.android.view.uimodels.settings.base.BottomSettingsItemData

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
 */

/**
 * shosetsu
 * 19 / 08 / 2020
 */
class CustomBottomSettingData(id: Int) : BottomSettingsItemData(id) {

	/**
	 * Custom view
	 */
	lateinit var customView: () -> View

	override fun bindView(holder: ViewHolder, payloads: List<Any>) {
		super.bindView(holder, payloads)
		holder.bottomField.addView(customView(), MATCH_PARENT, MATCH_PARENT)
	}

}