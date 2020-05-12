package com.github.doomsdayrs.apps.shosetsu.viewmodel.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.TextAssetReader
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.TextAssetReader.Target.DISCLAIMER
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.TextAssetReader.Target.LICENSE
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType.INFORMATION
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ISettingsInfoViewModel

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
 * 12 / May / 2020
 */
class SettingsInfoViewModel(
		val activity: Activity,
		val router: Router
) : ISettingsInfoViewModel() {
	override val settings: ArrayList<SettingsItemData> by lazy {
		arrayListOf(
				SettingsItemData(INFORMATION, 0)
						.setTitle(R.string.version)
						.setDescription(BuildConfig.VERSION_NAME),
				SettingsItemData(INFORMATION, 1)
						.setTitle(R.string.report_bug)
						.setDescription(R.string.report_bug_link)
						.setOnClickListener { onClickReportBug() },
				SettingsItemData(INFORMATION, 2)
						.setTitle(R.string.author)
						.setDescription(R.string.author_name)
						.setOnClickListener { onClickAuthor() },
				SettingsItemData(INFORMATION, 3)
						.setTitle(R.string.disclaimer)
						.setOnClickListener { onClickDisclaimer() },
				SettingsItemData(INFORMATION, 4)
						.setTitle(R.string.license)
						.setOnClickListener { onClickLicense() }
		)
	}

	private fun onClickReportBug() = activity.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(activity.getString(R.string.report_bug_link))
	))

	private fun onClickAuthor() = activity.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(activity.getString(R.string.author_github))
	))

	private fun onClickDisclaimer() =
			router.pushController(TextAssetReader(DISCLAIMER.bundle).withFadeTransaction())

	private fun onClickLicense() =
			router.pushController(TextAssetReader(LICENSE.bundle).withFadeTransaction())
}