package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.content.Intent
import android.net.Uri
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.TextAssetReader.Target.DISCLAIMER
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.TextAssetReader.Target.LICENSE
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getString
import com.github.doomsdayrs.apps.shosetsu.variables.ext.withFadeTransaction

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
 * ====================================================================
 */
/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class InfoSettings : SettingsSubController() {
    override val settings by lazy {
        arrayListOf(
                SettingsItemData(SettingsItemData.SettingsType.INFORMATION)
                        .setTitle(R.string.version)
                        .setDescription(BuildConfig.VERSION_NAME),
                SettingsItemData(SettingsItemData.SettingsType.INFORMATION)
                        .setTitle(R.string.report_bug)
                        .setDescription(R.string.report_bug_link)
                        .setOnClickListener { onClickReportBug() },
                SettingsItemData(SettingsItemData.SettingsType.INFORMATION)
                        .setTitle(R.string.author)
                        .setDescription(R.string.author_name)
                        .setOnClickListener { onClickAuthor() },
                SettingsItemData(SettingsItemData.SettingsType.INFORMATION)
                        .setTitle(R.string.disclaimer)
                        .setOnClickListener { onClickDisclaimer() },
                SettingsItemData(SettingsItemData.SettingsType.INFORMATION)
                        .setTitle(R.string.license)
                        .setOnClickListener { onClickLicense() }
        )
    }

    private fun onClickReportBug() = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.report_bug_link))))
    private fun onClickAuthor() = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.author_github))))
    private fun onClickDisclaimer() = router.pushController(TextAssetReader(DISCLAIMER.bundle).withFadeTransaction())
    private fun onClickLicense() = router.pushController(TextAssetReader(LICENSE.bundle).withFadeTransaction())
}