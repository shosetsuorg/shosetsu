package app.shosetsu.android.ui.about

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import app.shosetsu.android.common.enums.TextAsset.DISCLAIMER
import app.shosetsu.android.common.enums.TextAsset.LICENSE
import app.shosetsu.android.common.ext.shosetsuPush
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.settings.sub.TextAssetReader
import app.shosetsu.android.view.controller.ViewedController
import app.shosetsu.android.viewmodel.abstracted.AAboutViewModel
import app.shosetsu.common.consts.*
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerAboutBinding

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
 * 01 / 10 / 2020
 */
class AboutController : ViewedController<ControllerAboutBinding>() {
	override val viewTitleRes: Int = R.string.about
	private val viewModel: AAboutViewModel by viewModel()

	override fun onViewCreated(view: View) {
		binding.apply {
			versionTag.text = BuildConfig.VERSION_NAME
			appUpdateCheck.setOnClickListener { viewModel.appUpdateCheck() }
			website.setOnClickListener { openWebsite() }
			github.setOnClickListener { openGithub() }
			extensions.setOnClickListener { openExtensions() }
			discord.setOnClickListener { openDiscord() }
			patreon.setOnClickListener { openPatreon() }
			sourceLicensesCard.setOnClickListener { onClickLicense() }
			disclaimerCard.setOnClickListener { onClickDisclaimer() }
		}
	}

	private fun openSite(url: String) {
		startActivity(Intent(ACTION_VIEW, Uri.parse(url)))
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

	override fun bindView(inflater: LayoutInflater): ControllerAboutBinding =
		ControllerAboutBinding.inflate(inflater)

	private fun onClickLicense() =
		router.shosetsuPush(TextAssetReader(LICENSE))

	private fun onClickDisclaimer() =
		router.shosetsuPush(TextAssetReader(DISCLAIMER))

	override fun handleErrorResult(e: HResult.Error) {}
}