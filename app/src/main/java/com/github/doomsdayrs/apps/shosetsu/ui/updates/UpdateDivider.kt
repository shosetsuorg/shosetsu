package com.github.doomsdayrs.apps.shosetsu.ui.updates

import android.content.Context
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView

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
 * 24 / 08 / 2020
 */
class UpdateDivider(context: Context) : RecyclerView.ItemDecoration() {
	private val view: View = context.getSystemService<LayoutInflater>()!!.inflate(
			-1, null, false
	)

}