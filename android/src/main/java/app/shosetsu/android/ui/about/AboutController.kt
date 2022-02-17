package app.shosetsu.android.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.shosetsu.android.common.enums.TextAsset
import app.shosetsu.android.common.ext.shosetsuPush
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.settings.sub.TextAssetReader
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.viewmodel.abstracted.AAboutViewModel
import app.shosetsu.common.consts.*
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme

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
 * @since 21 / 10 / 2021
 * @author Doomsdayrs
 */
class AboutController : ShosetsuController() {

	override val viewTitleRes: Int = R.string.about

	private val viewModel: AAboutViewModel by viewModel()

	override fun onViewCreated(view: View) {}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setViewTitle()
		setContent {
			MdcTheme {
				AboutContent(
					currentVersion = BuildConfig.VERSION_NAME,
					onCheckForAppUpdate = viewModel::appUpdateCheck,
					onOpenWebsite = ::openWebsite,
					onOpenSource = ::openGithub,
					onOpenExtensions = ::openExtensions,
					onOpenDiscord = ::openDiscord,
					onOpenPatreon = ::openPatreon,
					onOpenLicense = ::onClickLicense,
					onOpenDisclaimer = ::onClickDisclaimer
				)
			}
		}
	}

	private fun onClickLicense() =
		router.shosetsuPush(TextAssetReader(TextAsset.LICENSE))

	private fun onClickDisclaimer() =
		router.shosetsuPush(TextAssetReader(TextAsset.DISCLAIMER))

	private fun openSite(url: String) {
		startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
	}

	private fun openWebsite() =
		openSite(URL_WEBSITE)

	private fun openExtensions() =
		openSite(URL_GITHUB_EXTENSIONS)

	private fun openDiscord() =
		openSite(URL_DISCORD)

	private fun openPatreon() =
		openSite(URL_PATREON)

	private fun openGithub() =
		openSite(URL_GITHUB_APP)
}

@ExperimentalMaterialApi
@Preview
@Composable
fun PreviewAboutContent() {
	MdcTheme {
		AboutContent(
			currentVersion = BuildConfig.VERSION_NAME,
			onCheckForAppUpdate = {},
			onOpenWebsite = {},
			onOpenSource = {},
			onOpenExtensions = {},
			onOpenDiscord = {},
			onOpenPatreon = {},
			onOpenLicense = {},
			onOpenDisclaimer = {}
		)
	}
}

@ExperimentalMaterialApi
@Composable
fun AboutItem(
	@StringRes titleRes: Int,
	@StringRes descriptionRes: Int? = null,
	description: String? = null,
	@DrawableRes iconRes: Int? = null,
	onClick: () -> Unit = {}
) {
	Card(
		onClick = onClick,
		elevation = 0.dp,
		shape = RectangleShape,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Start,
			modifier = Modifier.padding(16.dp)
		) {
			if (iconRes != null)
				Image(painterResource(iconRes), null)

			Column(
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.Start,
				modifier = Modifier.fillMaxWidth()
			) {
				Text(stringResource(titleRes))

				if (descriptionRes != null)
					Text(
						stringResource(descriptionRes),
						fontSize = dimensionResource(R.dimen.sub_text_size).value.sp,
						modifier = Modifier.alpha(0.7f)
					)
				else if (description != null)
					Text(
						description,
						fontSize = dimensionResource(R.dimen.sub_text_size).value.sp,
						modifier = Modifier.alpha(0.7f)
					)
			}
		}
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AboutContent(
	currentVersion: String,
	onCheckForAppUpdate: () -> Unit,
	onOpenWebsite: () -> Unit,
	onOpenSource: () -> Unit,
	onOpenExtensions: () -> Unit,
	onOpenDiscord: () -> Unit,
	onOpenPatreon: () -> Unit,
	onOpenLicense: () -> Unit,
	onOpenDisclaimer: () -> Unit
) {
	Column(
		modifier = Modifier.fillMaxSize()
	) {
		AboutItem(
			R.string.version,
			description = currentVersion
		)
		AboutItem(
			R.string.check_for_app_update,
			onClick = onCheckForAppUpdate
		)
		Divider()
		AboutItem(
			R.string.website,
			R.string.website_url,
			onClick = onOpenWebsite
		)
		AboutItem(
			R.string.github,
			R.string.github_url,
			onClick = onOpenSource
		)
		AboutItem(
			R.string.extensions,
			R.string.extensions_url,
			onClick = onOpenExtensions
		)
		AboutItem(
			R.string.discord,
			R.string.discord_url,
			onClick = onOpenDiscord
		)
		AboutItem(
			R.string.patreon,
			R.string.patreon_url,
			onClick = onOpenPatreon
		)
		AboutItem(
			R.string.source_licenses,
			onClick = onOpenLicense
		)
		AboutItem(
			R.string.disclaimer,
			onClick = onOpenDisclaimer
		)
	}
}