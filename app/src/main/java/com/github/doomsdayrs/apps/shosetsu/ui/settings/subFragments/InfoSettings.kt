package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.content.Intent
import android.net.Uri
import android.view.View
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
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
 * Shosetsu
 * 9 / June / 2019
 */
class InfoSettings : SettingsSubController() {
	val viewModel: ISettingsInfoViewModel by viewModel()
	override val settings by lazy { viewModel.settings }

	override fun onViewCreated(view: View) {
		settings[1].setOnClickListener { onClickReportBug() }
		settings[2].setOnClickListener { onClickAuthor() }
		settings[3].setOnClickListener { onClickDisclaimer() }
		settings[4].setOnClickListener { onClickLicense() }

		super.onViewCreated(view)
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