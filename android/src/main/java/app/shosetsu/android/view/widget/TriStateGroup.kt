package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import app.shosetsu.android.view.widget.TriStateButton.State

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
class TriStateGroup @JvmOverloads constructor(
		context: Context,
		attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
	private val stateChangeListeners = ArrayList<(Int, State) -> Unit>()

	private val buttons: List<TriStateButton>
		get() = children.filterIsInstance<TriStateButton>().toList()

	override fun onViewAdded(child: View?) {
		super.onViewAdded(child)
		if (child is TriStateButton) {
			child.addOnClickListener {
				buttonClicked(child)
			}

			child.addOnStateChangeListener {
				if (it != State.IGNORED)
					stateChangeListeners.forEach { listener ->
						listener(child.id, it)
					}
			}
		}
	}

	override fun onViewRemoved(child: View?) {
		super.onViewRemoved(child)
		if (child is TriStateButton) {
			child.clearOnClickListeners()
			child.clearOnStateChangeListener()
		}
	}

	private fun buttonClicked(triStateButton: TriStateButton) {
		triStateButton.skipIgnored = true
		buttons.filterNot { it == triStateButton }.forEach {
			it.skipIgnored = false
			it.state = State.IGNORED
		}
	}

	/**
	 * When a state becomes something other then ignored for a certain button
	 */
	fun addOnStateChangeListener(listener: (
			@ParameterName("id") Int,
			@ParameterName("state") State
	) -> Unit) = stateChangeListeners.add(listener)
}