package app.shosetsu.android.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.savedstate.SavedStateRegistryOwner
import app.shosetsu.android.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme
import io.github.g0dkar.qrcode.QRCode
import io.github.g0dkar.qrcode.render.QRCodeCanvasFactory
import kotlinx.coroutines.flow.MutableStateFlow

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
fun createQRCodeDialog(
	context: Context,
	owner: SavedStateRegistryOwner,
	content: String
): ComposeDialog =
	ComposeDialog(context, owner).apply {
		setContentView(ComposeView(context).apply {


			setViewCompositionStrategy(
				ViewCompositionStrategy.DisposeOnLifecycleDestroyed(owner)
			)

			val flow: MutableStateFlow<ImageBitmap?> = MutableStateFlow(null)

			launchIO {
				val code = QRCode(content)
				val encoding = code.encode()
				QRCodeCanvasFactory.AVAILABLE_IMPLEMENTATIONS["android.graphics.Bitmap"] =
					{ width, height -> AndroidQRCodeDrawable(width, height) }

				val size = code.computeImageSize(
					QRCode.DEFAULT_CELL_SIZE,
					QRCode.DEFAULT_MARGIN,
				)
				val bytes = code.render(
					qrCodeCanvas = AndroidQRCodeDrawable(size, size),
					rawData = encoding,
					brightColor = Color.WHITE,
					darkColor = Color.BLACK,
					marginColor = Color.WHITE
				).toByteArray()

				val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

				flow.emit(bitmap.asImageBitmap())
			}

			setContent {
				MdcTheme {
					Card(
						modifier = Modifier.fillMaxWidth().fillMaxHeight(.4f)
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
									modifier = Modifier.background(androidx.compose.ui.graphics.Color.White)
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

	}