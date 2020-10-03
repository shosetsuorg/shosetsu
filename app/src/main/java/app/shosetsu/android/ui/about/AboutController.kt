package app.shosetsu.android.ui.about

import android.view.LayoutInflater
import android.view.View
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.settings.sub.TextAssetReader
import app.shosetsu.android.view.base.PushCapableController
import app.shosetsu.android.view.base.ViewedController
import app.shosetsu.android.viewmodel.abstracted.AAboutViewModel
import com.bluelinelabs.conductor.Controller
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
class AboutController : ViewedController<ControllerAboutBinding>(), PushCapableController {
	override val viewTitleRes: Int = R.string.about
	private val viewModel: AAboutViewModel by viewModel()
	lateinit var pushController: (Controller) -> Unit

	override fun onViewCreated(view: View) {
		binding.apply {
			versionTag.text = BuildConfig.VERSION_NAME
			appUpdateCheck.setOnClickListener {
				viewModel.appUpdateCheck()
			}
			website.setOnClickListener {
				viewModel.openWebsite()
			}
			github.setOnClickListener {
				viewModel.openGithub()
			}
			extensions.setOnClickListener {
				viewModel.openExtensions()
			}
			discord.setOnClickListener {
				viewModel.openDiscord()
			}
			patreon.setOnClickListener {
				viewModel.openPatreon()
			}
			sourceLicenses.setOnClickListener {
				onClickLicense()
			}
		}
	}

	override fun acceptPushing(pushController: (Controller) -> Unit) {
		this.pushController = pushController
	}

	override fun bindView(inflater: LayoutInflater): ControllerAboutBinding =
			ControllerAboutBinding.inflate(inflater)

	private fun onClickLicense() =
			pushController(TextAssetReader(TextAssetReader.Target.LICENSE.bundle))
}