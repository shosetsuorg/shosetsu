package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.main.MainActivity
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubFragment
import com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter.SettingItemsAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.variables.toast
import kotlinx.android.synthetic.main.settings.*

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
class InfoSettings : SettingsSubFragment() {
    override val settings: ArrayList<SettingsItemData> = arrayListOf(
            SettingsItemData(SettingsItemData.SettingsType.INFORMATION)
                    .setTitle(R.string.version)
                    .setDescription(BuildConfig.VERSION_NAME)
                    .setOnClickListener { v: View -> onClickAppVer(v) },
            SettingsItemData(SettingsItemData.SettingsType.INFORMATION)
                    .setTitle(R.string.version)
                    .setDescription(BuildConfig.VERSION_NAME)
                    .setOnClickListener { v: View -> onClickAppVer(v) },
            SettingsItemData(SettingsItemData.SettingsType.INFORMATION)
                    .setTitle(R.string.report_bug)
                    .setDescription(R.string.report_bug_link)
                    .setOnClickListener { v: View -> onClickReportBug(v) },
            SettingsItemData(SettingsItemData.SettingsType.INFORMATION)
                    .setTitle(R.string.author)
                    .setDescription(R.string.author_name)
                    .setOnClickListener { v: View -> onClickAuthor(v) },
            SettingsItemData(SettingsItemData.SettingsType.INFORMATION)
                    .setTitle(R.string.disclaimer)
                    .setOnClickListener { v: View -> onClickDisclaimer(v) },
            SettingsItemData(SettingsItemData.SettingsType.INFORMATION)
                    .setTitle(R.string.license)
                    .setOnClickListener { onClickLicense() }
    )


    private fun onClickAppVer(v: View) { // TODO: Add the app version number after consultation
        v.context.toast("AppVer")
    }

    private fun onClickReportBug(v: View) {
        v.context.toast("ReportBug")
        val bugReportLink = getString(R.string.report_bug_link)
        val bugReportingIntent = Intent(Intent.ACTION_VIEW, Uri.parse(bugReportLink))
        startActivity(bugReportingIntent)
    }

    private fun onClickAuthor(v: View) {
        v.context.toast("Author")
        val authorGitHubLink = getString(R.string.author_github)
        val authorGitHubIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authorGitHubLink))
        startActivity(authorGitHubIntent)
    }

    private fun onClickDisclaimer(v: View) { // TODO: Show full disclaimer on click
        v.context.toast("Disclaimer")
    }

    private fun onClickLicense() { // TODO: Show full license on click
        (activity as MainActivity).transitionView(LicenseReader())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "ViewSettings")
        return inflater.inflate(R.layout.settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SettingItemsAdapter(settings)
    }
}