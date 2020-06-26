package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.content.Intent
import android.net.Uri
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.dsl.*

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
class InfoSettings : SettingsSubController() {
	override val settings: List<SettingsItemData> by settingsList {
		infoSettingData(0) {
			title { R.string.version }
			description { BuildConfig.VERSION_NAME }
		}
		infoSettingData(1) {
			title { (R.string.report_bug) }
			description { R.string.report_bug_link }
			onClick { onClickReportBug() }
		}
		infoSettingData(2) {
			title { R.string.author }
			description { R.string.author_name }
			onClick { onClickAuthor() }
		}
		infoSettingData(3) {
			title { R.string.disclaimer }
			onClick { onClickDisclaimer() }

		}
		infoSettingData(4) {
			title { R.string.license }
			onClick { onClickLicense() }
		}
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
			router.pushController(TextAssetReader(TextAssetReader.Target.DISCLAIMER.bundle).withFadeTransaction())

	private fun onClickLicense() =
			router.pushController(TextAssetReader(TextAssetReader.Target.LICENSE.bundle).withFadeTransaction())
}