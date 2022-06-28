package app.shosetsu.android.view

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.launchUI
import app.shosetsu.android.view.compose.ShosetsuCompose
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.delay

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

@OptIn(ExperimentalMaterialApi::class)
fun openShareMenu(
	context: Context,
	owner: LifecycleOwner,
	stateOwner: SavedStateRegistryOwner,
	shareBasicURL: () -> Unit,
	shareQRCode: () -> Unit
) {
	ComposeBottomSheetDialog(context, owner, stateOwner).apply bottomSheet@{
		setContentView(ComposeView(context).apply {
			setViewCompositionStrategy(
				ViewCompositionStrategy.DisposeOnLifecycleDestroyed(owner)
			)

			setContent {
				ShosetsuCompose {
					Column(
						modifier = Modifier.padding(start = 16.dp)
					) {
						Box(
							modifier = Modifier.height(56.dp),
							contentAlignment = Alignment.CenterStart
						) {
							Text(
								stringResource(R.string.share),
								style = MaterialTheme.typography.body1,
								modifier = Modifier.alpha(0.8f)
							)
						}

						Card(
							onClick = {
								launchIO {
									delay(100)
									launchUI {
										shareBasicURL()
									}
								}
								this@bottomSheet.dismiss()
							},
							modifier = Modifier
								.height(56.dp)
								.fillMaxWidth(),
							shape = RectangleShape,
							backgroundColor = Color.Transparent,
							elevation = 0.dp
						) {
							Row(
								verticalAlignment = Alignment.CenterVertically,
							) {
								Icon(
									painterResource(
										R.drawable.ic_baseline_link_24
									),
									"",
									modifier = Modifier.padding(end = 8.dp)
								)
								Text(
									stringResource(R.string.menu_share_url),
									style = MaterialTheme.typography.body1
								)
							}
						}
						Card(
							onClick = {
								launchIO {
									delay(100)
									launchUI {
										shareQRCode()
									}
								}
								this@bottomSheet.dismiss()
							},
							modifier = Modifier
								.height(56.dp)
								.fillMaxWidth(),
							shape = RectangleShape,
							backgroundColor = Color.Transparent,
							elevation = 0.dp
						) {
							Row(
								verticalAlignment = Alignment.CenterVertically
							) {
								Icon(
									painterResource(
										R.drawable.ic_baseline_qr_code_24
									),
									"",
									modifier = Modifier.padding(end = 8.dp)
								)
								Text(
									stringResource(R.string.menu_share_qr),
									style = MaterialTheme.typography.body1
								)
							}
						}
					}

				}
			}
		})
	}.show()
}