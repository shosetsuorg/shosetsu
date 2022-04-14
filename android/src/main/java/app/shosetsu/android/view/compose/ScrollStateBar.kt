package app.shosetsu.android.view.compose

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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

/**
 * Shosetsu
 *
 * @since 12 / 04 / 2022
 * @author Doomsdayrs
 */

@Composable
fun ScrollStateBar(scrollState: ScrollState, content: @Composable () -> Unit) {
	BoxWithConstraints(
		modifier = Modifier.fillMaxSize()
	) {
		val viewMaxHeight = constraints.maxHeight.toFloat()

		// Ensure divide by zero does not occur
		val scrollPercentage =
			if (scrollState.maxValue != 0) {
				scrollState.value / scrollState.maxValue.toFloat()
			} else {
				0f
			}

		val paddingSize =
			if (scrollPercentage < 0.98) {
				scrollPercentage * viewMaxHeight
			} else {
				.98f * viewMaxHeight
			}

		content()

		BoxWithConstraints(Modifier.fillMaxWidth()) {
			BoxWithConstraints(
				Modifier
					.align(Alignment.TopEnd)
					.fillMaxHeight()
			) {
				if (scrollState.isScrollInProgress)
					Box(
						Modifier
							.align(Alignment.TopEnd)
							.graphicsLayer {
								translationY = paddingSize
							}
							.padding(horizontal = 4.dp)
							.width(2.dp)
							.background(
								color = MaterialTheme.colors.primary,
								shape = MaterialTheme.shapes.medium
							).height(16.dp)
					)
			}
		}
	}
}