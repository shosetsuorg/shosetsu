package app.shosetsu.android.view.uimodels.settings

import android.view.View
import app.shosetsu.android.view.uimodels.settings.base.TextRequiringSettingData

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
 * 25 / 06 / 2020
 */
class TextSettingData(id: Int) : TextRequiringSettingData(id) {
	override fun bindView(holder: ViewHolder, payloads: List<Any>) {
		super.bindView(holder, payloads)
		with(holder) {
			if (textID != -1)
				textView.setText(textID)
			else
				textView.text = textText
			textView.visibility = View.VISIBLE
			textView.setOnClickListener(textViewOnClickListener)
		}
	}

	override fun unbindView(holder: ViewHolder) {
		super.unbindView(holder)
		with(holder) {
			textView.text = null
			textView.setOnClickListener(null)
		}
	}
}