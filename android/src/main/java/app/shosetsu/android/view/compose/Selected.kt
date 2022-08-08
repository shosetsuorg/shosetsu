package app.shosetsu.android.view.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

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

fun Modifier.selectedOutline(isSelected: Boolean) = composed {
    val secondary = MaterialTheme.colors.secondary
    if (isSelected) {
        drawBehind {
            val additional = 24.dp.value
            val offset = additional / 2
            val height = size.height + additional
            val width = size.width + additional
            drawRoundRect(
                color = secondary,
                topLeft = Offset(-offset, -offset),
                size = Size(width, height),
                cornerRadius = CornerRadius(offset),
            )
        }
    } else {
        this
    }
}