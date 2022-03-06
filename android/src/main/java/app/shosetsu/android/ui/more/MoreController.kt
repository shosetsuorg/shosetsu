package app.shosetsu.android.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.makeSnackBar
import app.shosetsu.android.common.ext.shosetsuPush
import app.shosetsu.android.ui.about.AboutController
import app.shosetsu.android.ui.backup.BackupSettings
import app.shosetsu.android.ui.downloads.DownloadsController
import app.shosetsu.android.ui.repository.RepositoryController
import app.shosetsu.android.ui.settings.ComposeSettingsController
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.CollapsedToolBarController
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode

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
 * shosetsu
 * 12 / 09 / 2020
 *
 * Option for download queue
 */
class ComposeMoreController
	: ShosetsuController(), CollapsedToolBarController {

	override val viewTitleRes: Int = R.string.more

	override fun onViewCreated(view: View) {}

	lateinit var scanQrCode: ActivityResultLauncher<Nothing?>

	fun handleResult(result: QRResult) {
		when (result) {
			is QRResult.QRSuccess -> {

			}
			QRResult.QRUserCanceled -> {

			}
			QRResult.QRMissingPermission -> {

			}
			is QRResult.QRError -> {
				logE("Failed to scan qr code", result.exception)
			}
		}
	}

	override fun onLifecycleCreate(owner: LifecycleOwner, registry: ActivityResultRegistry) {
		super.onLifecycleCreate(owner, registry)
		scanQrCode = registry.register("controller_more_qr_code", ScanQRCode(), ::handleResult)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setViewTitle()
		setContent {
			MdcTheme {
				MoreContent(
					{
						makeSnackBar(R.string.style_wait)?.show()
					},
					{
						scanQrCode.launch(null)
					}
				) {
					router.shosetsuPush(it)
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MoreItemContent(
	@StringRes title: Int,
	@DrawableRes drawableRes: Int,
	onClick: () -> Unit
) {
	Card(
		shape = RectangleShape,
		backgroundColor = colorResource(android.R.color.transparent),
		onClick = onClick,
		modifier = Modifier.fillMaxWidth(),
		elevation = 0.dp
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				painterResource(drawableRes),
				null,
				modifier = Modifier
					.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 24.dp)
					.size(24.dp),
				tint = colorResource(R.color.colorAccent)
			)
			Text(stringResource(title))
		}
	}
}

@Preview
@Composable
fun PreviewMoreContent() {
	MoreContent({}, {}) { }
}

@Composable
fun MoreContent(
	showStyleBar: () -> Unit,
	showQRCode: () -> Unit,
	pushController: (Controller) -> Unit
) {
	Column(
		modifier = Modifier.fillMaxSize().scrollable(rememberScrollState(), Orientation.Vertical)
	) {
		Card(
			elevation = 0.dp,
			shape = RectangleShape
		) {
			Image(
				painterResource(R.drawable.shou_icon_thick),
				stringResource(R.string.app_name),
				modifier = Modifier.fillMaxWidth()
			)
		}
		Divider()
		Column(
			modifier = Modifier.fillMaxSize()
		) {
			MoreItemContent(R.string.downloads, R.drawable.download) {
				pushController(DownloadsController())
			}

			MoreItemContent(R.string.backup, R.drawable.restore) {
				pushController(BackupSettings())
			}

			MoreItemContent(R.string.repositories, R.drawable.add_shopping_cart) {
				pushController(RepositoryController())
			}

			MoreItemContent(R.string.styles, R.drawable.ic_baseline_style_24) {
				showStyleBar()
			}

			MoreItemContent(R.string.qr_code_scan, R.drawable.ic_baseline_qr_code_scanner_24) {
				showQRCode()
			}

			MoreItemContent(R.string.settings, R.drawable.settings) {
				pushController(ComposeSettingsController())
			}

			MoreItemContent(R.string.about, R.drawable.info_outline) {
				pushController(AboutController())
			}

		}
	}
}