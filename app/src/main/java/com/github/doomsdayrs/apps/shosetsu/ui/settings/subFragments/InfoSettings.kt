package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsItem

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
class InfoSettings : Fragment() {
    private fun onClickAppVer(v: View) { // TODO: Add the app version number after consultation
        Toast.makeText(v.context, "AppVer", Toast.LENGTH_SHORT).show()
    }

    private fun onClickReportBug(v: View) {
        Toast.makeText(v.context, "ReportBug", Toast.LENGTH_SHORT).show()
        val bugReportLink = getString(R.string.report_bug_link)
        val bugReportingIntent = Intent(Intent.ACTION_VIEW, Uri.parse(bugReportLink))
        startActivity(bugReportingIntent)
    }

    private fun onClickAuthor(v: View) {
        Toast.makeText(v.context, "Author", Toast.LENGTH_SHORT).show()
        val authorGitHubLink = getString(R.string.author_github)
        val authorGitHubIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authorGitHubLink))
        startActivity(authorGitHubIntent)
    }

    private fun onClickDisclaimer(v: View) { // TODO: Show full disclaimer on click
        Toast.makeText(v.context, "Disclaimer", Toast.LENGTH_SHORT).show()
    }

    private fun onClickLicense(v: View) { // TODO: Show full license on click
        Toast.makeText(v.context, "License", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "ViewSettings")
        val settingsInfoView = inflater.inflate(R.layout.settings_info, container, false)
        // Setup App version
        val appVerItem = SettingsItem(settingsInfoView.findViewById(R.id.settings_info_app_version))
        appVerItem.setTitle(R.string.version)
        appVerItem.setDesc(BuildConfig.VERSION_NAME)
        appVerItem.setOnClickListener { v: View -> onClickAppVer(v) }
        // Setup Report Bug
        val reportBugItem = SettingsItem(settingsInfoView.findViewById(R.id.settings_info_report_bug))
        reportBugItem.setTitle(R.string.report_bug)
        reportBugItem.setDesc(R.string.report_bug_link)
        reportBugItem.setOnClickListener { v: View -> onClickReportBug(v) }
        // Setup Author
        val authorItem = SettingsItem(settingsInfoView.findViewById(R.id.settings_info_author))
        authorItem.setTitle(R.string.author)
        authorItem.setDesc(R.string.author_name)
        authorItem.setOnClickListener { v: View -> onClickAuthor(v) }
        // Setup Disclaimer
        val disclaimerItem = SettingsItem(settingsInfoView.findViewById(R.id.settings_info_disclaimer))
        disclaimerItem.setTitle(R.string.disclaimer)
        disclaimerItem.setOnClickListener { v: View -> onClickDisclaimer(v) }
        // Setup License
        val licenseItem = SettingsItem(settingsInfoView.findViewById(R.id.settings_info_license))
        licenseItem.setTitle(R.string.license)
        licenseItem.setOnClickListener { v: View -> onClickLicense(v) }
        return settingsInfoView
    }
}