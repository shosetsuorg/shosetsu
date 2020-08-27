package com.github.doomsdayrs.apps.shosetsu.application

import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications
import com.github.doomsdayrs.apps.shosetsu.common.utils.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.common.utils.base.IFormatterUtils
import com.github.doomsdayrs.apps.shosetsu.di.*
import com.github.doomsdayrs.apps.shosetsu.di.datasource.cacheDataSouceModule
import com.github.doomsdayrs.apps.shosetsu.di.datasource.fileDataSourceModule
import com.github.doomsdayrs.apps.shosetsu.di.datasource.localDataSouceModule
import com.github.doomsdayrs.apps.shosetsu.di.datasource.remoteDataSouceModule
import com.github.doomsdayrs.apps.shosetsu.viewmodel.factory.ViewModelFactory
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraDialog
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.HttpSenderConfigurationBuilder
import org.acra.data.StringFormat
import org.acra.sender.HttpSender
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

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
 */

/**
 * shosetsu
 * 28 / 01 / 2020
 */
@AcraCore(buildConfigClass = BuildConfig::class)
@AcraDialog(resCommentPrompt = R.string.crashCommentPromt, resText = R.string.crashDialogText, resTheme = R.style.AppTheme_CrashReport)
class ShosetsuApplication : Application(), LifecycleEventObserver, KodeinAware {
	override fun attachBaseContext(base: Context?) {
		super.attachBaseContext(base)

		setupACRA()
		Notifications.createChannels(this)
	}

	private fun setupACRA() {
		val config = CoreConfigurationBuilder(this)
		config.setBuildConfigClass(BuildConfig::class.java).setReportFormat(StringFormat.JSON)

		config.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder::class.java)
				.setHttpMethod(HttpSender.Method.POST)
				.setUri("https://technojo4.com/acra.php")
				.setEnabled(true)

		ACRA.init(this, config)
	}

	override val kodein: Kodein by Kodein.lazy {
		bind<ViewModelFactory>() with singleton { ViewModelFactory(applicationContext) }
		import(othersModule)
		import(cacheDataSouceModule)
		import(localDataSouceModule)
		import(remoteDataSouceModule)
		import(fileDataSourceModule)
		import(networkModule)
		bind<IFormatterUtils>() with singleton {
			FormatterUtils(applicationContext)
		}
		import(databaseModule)
		import(repositoryModule)
		import(useCaseModule)
		import(viewModelsModule)
		bind<ShosetsuSettings>() with singleton { ShosetsuSettings(applicationContext) }
		import(androidXModule(this@ShosetsuApplication))
	}

	override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {}
}