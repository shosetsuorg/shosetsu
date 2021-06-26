package app.shosetsu.android.view.uimodels.settings

import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import app.shosetsu.android.view.uimodels.settings.base.RightSettingsItemData
import app.shosetsu.android.view.widget.TriState
import com.github.doomsdayrs.apps.shosetsu.databinding.SettingsItemBinding

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
 * Shosetsu
 *
 * @since 25 / 06 / 2021
 * @author Doomsdayrs
 */
class TriStateButtonSettingData(id: Int) : RightSettingsItemData(id) {
	@DrawableRes
	var checkedRes: Int = 0

	@DrawableRes
	var uncheckedRes: Int = 0

	@DrawableRes
	var ignoredRes: Int = 0

	var state: TriState.State = TriState.State.IGNORED

	var onStateChanged: (TriState.State) -> Unit = {}

	override fun bindBinding(holder: SettingsItemBinding, payloads: List<Any>) {
		super.bindBinding(holder, payloads)
		holder.triStateIcon.isVisible = true
		holder.triStateIcon.checkedRes = checkedRes
		holder.triStateIcon.uncheckedRes = uncheckedRes
		holder.triStateIcon.ignoredRes = ignoredRes
		holder.triStateIcon.state = state
		holder.triStateIcon.onStateChangeListeners.add(onStateChanged)
	}

	override fun unbindBinding(holder: SettingsItemBinding) {
		super.unbindBinding(holder)
		holder.triStateIcon.isVisible = false
		holder.triStateIcon.checkedRes = 0
		holder.triStateIcon.uncheckedRes = 0
		holder.triStateIcon.ignoredRes = 0
		holder.triStateIcon.state = TriState.State.IGNORED
		holder.triStateIcon.onStateChangeListeners.remove(onStateChanged)
	}
}