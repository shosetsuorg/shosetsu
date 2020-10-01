package app.shosetsu.android.ui.settings.sub

import android.content.Intent
import android.net.Uri
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.settings.SettingsSubController
import app.shosetsu.android.ui.settings.sub.TextAssetReader.Target
import app.shosetsu.android.view.base.PushCapableController
import app.shosetsu.android.view.uimodels.settings.InfoSettingData
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.onClick
import app.shosetsu.android.viewmodel.abstracted.settings.AInfoSettingsViewModel
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R

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
 * Shosetsu
 * 9 / June / 2019
 */
class InfoSettings : SettingsSubController(), PushCapableController {
	override val viewTitleRes: Int = R.string.settings_info
	override val viewModel: AInfoSettingsViewModel by viewModel()

	private lateinit var pushController: (Controller) -> Unit

	override val adjustments: List<SettingsItemData>.() -> Unit = {
		find<InfoSettingData>(1)?.onClick { onClickReportBug() }
		find<InfoSettingData>(2)?.onClick { onClickAuthor() }
		find<InfoSettingData>(3)?.onClick { onClickDisclaimer() }
		find<InfoSettingData>(4)?.onClick { onClickLicense() }
	}

	private fun onClickReportBug() = activity?.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(activity!!.getString(R.string.report_bug_link))
	))

	private fun onClickAuthor() = activity?.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(activity!!.getString(R.string.author_github))
	))

	private fun onClickDisclaimer() =
			pushController(TextAssetReader(Target.DISCLAIMER.bundle))

	private fun onClickLicense() =
			pushController(TextAssetReader(Target.LICENSE.bundle))

	override fun acceptPushing(pushController: (Controller) -> Unit) {
		this.pushController = pushController
	}
}