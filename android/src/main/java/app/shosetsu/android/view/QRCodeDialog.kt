package app.shosetsu.android.view

import android.content.Context
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import app.shosetsu.android.view.compose.ShosetsuCompose
import app.shosetsu.android.R
import kotlinx.coroutines.flow.Flow

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
 * @since 06 / 03 / 2022
 * @author Doomsdayrs
 */


@OptIn(ExperimentalAnimationGraphicsApi::class)
fun openQRCodeShareDialog(
	context: Context,
	owner: LifecycleOwner,
	stateOwner: SavedStateRegistryOwner,
	flow: Flow<ImageBitmap?>
) {
	ComposeDialog(context, owner, stateOwner).apply {
		setContentView(ComposeView(context).apply {


			setViewCompositionStrategy(
				ViewCompositionStrategy.DisposeOnLifecycleDestroyed(owner)
			)


			setContent {
				ShosetsuCompose {
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.fillMaxHeight(.4f)
					) {
						val map by flow.collectAsState(null)

						Box(
							modifier = Modifier.padding(16.dp),
							contentAlignment = Alignment.Center
						) {
							if (map != null) {
								Image(
									map!!,
									"",
									modifier = Modifier
										.background(androidx.compose.ui.graphics.Color.White)
										.padding(16.dp)
								)
							} else {
								val image =
									AnimatedImageVector.animatedVectorResource(R.drawable.animated_refresh)

								Image(
									rememberAnimatedVectorPainter(image, false),
									stringResource(R.string.loading),
								)
							}
						}

					}
				}
			}
		})

	}.show()
}