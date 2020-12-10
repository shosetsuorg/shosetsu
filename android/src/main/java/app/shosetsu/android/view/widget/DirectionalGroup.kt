package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.children

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
 */

/**
 * shosetsu
 * 24 / 11 / 2020
 */
class DirectionalGroup @JvmOverloads constructor(
		context: Context,
		attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
	private val buttons: List<TriStateButton> =
			children.filterIsInstance<TriStateButton>().toList()

	private fun buttonClicked(triStateButton: TriStateButton) {
		buttons.filterNot { it == triStateButton }.forEach {
			it.isChecked = false
			it.isActive = false
		}
		if (triStateButton.isActive)
			triStateButton.toggle()
		else triStateButton.toggleIsActive()
	}
}