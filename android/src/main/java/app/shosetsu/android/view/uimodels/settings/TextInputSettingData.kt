package app.shosetsu.android.view.uimodels.settings

import android.text.Editable
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import app.shosetsu.android.view.uimodels.settings.base.BottomSettingsItemData
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
 * shosetsu
 * 22 / 07 / 2020
 */
class TextInputSettingData(id: Int) : BottomSettingsItemData(id) {
	var initialText: String = ""

	/** @see doAfterTextChanged */
	var onTextChanged: (Editable) -> Unit = { _: Editable -> }

	override fun bindBinding(holder: SettingsItemBinding, payloads: List<Any>) {
		super.bindBinding(holder, payloads)
		holder.settingsItemDesc.isVisible = false
		holder.textInputEditText.apply {
			isVisible = true
			if (descRes != -1) setHint(descRes) else if (descText.isNotEmpty()) hint = descText
			setText(initialText)
			doAfterTextChanged { it?.let(onTextChanged) }
		}
	}

	override fun unbindBinding(holder: SettingsItemBinding) {
		super.unbindBinding(holder)
		holder.settingsItemDesc.isVisible = true
		holder.textInputEditText.apply {
			isVisible = false
			hint = null
			doAfterTextChanged { }
			text = null
		}
	}

}