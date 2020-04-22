package com.github.doomsdayrs.apps.shosetsu.ui

import android.app.Application
import android.content.Context
import android.net.Uri.encode
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Notifications
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraDialog
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.HttpSenderConfigurationBuilder
import org.acra.data.StringFormat
import org.acra.sender.HttpSender
import java.util.*

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
 * ====================================================================
 */

/**
 * shosetsu
 * 28 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
@AcraCore(buildConfigClass = BuildConfig::class)
@AcraDialog(resCommentPrompt = R.string.crashCommentPromt, resText = R.string.crashDialogText, resTheme = R.style.AppTheme_CrashReport)
class Shosetsu : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // Sets prefrences
        Settings.settings = getSharedPreferences("view", 0)
        Settings.readerSettings = getSharedPreferences("reader", 0)
        Settings.formatterSettings = getSharedPreferences("formatter", 0)

        val config = CoreConfigurationBuilder(this)
        config.setBuildConfigClass(BuildConfig::class.java).setReportFormat(StringFormat.JSON)

        config.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder::class.java)
                .setHttpMethod(HttpSender.Method.POST)
                .setUri("https://technojo4.com/acra.php")
                .setEnabled(true)

        //config.getPluginConfigurationBuilder(MailSenderConfigurationBuilder::class.java)
        //        .setMailTo("shoset.su@yandex.com")
        //        .setReportAsFile(true)
        //        .setSubject("#Crash Report#")
        //        .setEnabled(true)
        //        .setReportFileName(android.os.Build.MODEL + " " + Calendar.getInstance().time.toString())

        //TODO add custom content for reports
        // > Must Contain [ReportField] Report_ID, APP_VERSION_CODE, APP_VERSION_NAME, PACKAGE_NAME, ANDROID_VERSION, STACK_TRACE, USER_COMMENT, LOGCAT, INSTALLATION_ID
        // > All other [ReportField] constants are optional
        // > Yes there will be an option to submit ALL your data to me

        ACRA.init(this, config)
        Notifications.createChannels(this)
    }
}