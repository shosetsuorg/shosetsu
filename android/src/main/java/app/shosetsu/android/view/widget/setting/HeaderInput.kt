package app.shosetsu.android.view.widget.setting

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import app.shosetsu.lib.Filter
import com.github.doomsdayrs.apps.shosetsu.databinding.DrawerDividerBinding

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
 * 03 / 03 / 2021
 */
class HeaderInput @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
) : LinearLayoutCompat(context, attrs) {
	init {
		orientation = VERTICAL
	}

	constructor(
		filter: Filter.Header,
		context: Context,
	) : this(context) {
		addView(
			TextView(context).apply {
				text = filter.name
			}
		)
		addView(DrawerDividerBinding.inflate(LayoutInflater.from(context)).root)
	}
}