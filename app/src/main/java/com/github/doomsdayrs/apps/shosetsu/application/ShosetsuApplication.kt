package com.github.doomsdayrs.apps.shosetsu.application

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import app.shosetsu.lib.ShosetsuLib
import app.shosetsu.lib.shosetsuGlobals
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.DBHelper
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications
import com.github.doomsdayrs.apps.shosetsu.common.consts.ShortCuts
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.di.*
import com.github.doomsdayrs.apps.shosetsu.di.datasource.cacheDataSouceModule
import com.github.doomsdayrs.apps.shosetsu.di.datasource.fileDataSourceModule
import com.github.doomsdayrs.apps.shosetsu.di.datasource.localDataSouceModule
import com.github.doomsdayrs.apps.shosetsu.di.datasource.remoteDataSouceModule
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtLibRepository
import com.github.doomsdayrs.apps.shosetsu.viewmodel.factory.ViewModelFactory
import okhttp3.OkHttpClient
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
import org.kodein.di.generic.instance
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
	private val extLibRepository by instance<IExtLibRepository>()
	private val okHttpClient by instance<OkHttpClient>()

	override val kodein: Kodein by Kodein.lazy {
		bind<ViewModelFactory>() with singleton { ViewModelFactory(applicationContext) }
		import(othersModule)
		import(cacheDataSouceModule)
		import(localDataSouceModule)
		import(remoteDataSouceModule)
		import(fileDataSourceModule)
		import(networkModule)
		import(databaseModule)
		import(repositoryModule)
		import(useCaseModule)
		import(viewModelsModule)
		bind<ShosetsuSettings>() with singleton { ShosetsuSettings(applicationContext) }
		import(androidXModule(this@ShosetsuApplication))
	}

	override fun attachBaseContext(base: Context?) {
		super.attachBaseContext(base)
		setupACRA()
		Notifications.createChannels(this)
		ShortCuts.createShortcuts(this)

	}

	override fun onCreate() {
		super.onCreate()
		ShosetsuLib.httpClient = okHttpClient
		ShosetsuLib.libLoader = libLoader@{ name ->
			Log.i("LibraryLoaderSync", "Loading:\t$name")
			return@libLoader when (val result = extLibRepository.blockingLoadExtLibrary(name)) {
				is HResult.Success -> {
					val l = try {
						shosetsuGlobals().load(result.data)
					} catch (e: Error) {
						throw e
					}
					l.call()
				}
				else -> {
					if (result is HResult.Error)
						Log.e(logID(), "[${result.code}]\t${result.message}")
					null
				}
			}
		}

		// OLD DB TO NEW
		DBHelper(this@ShosetsuApplication).writableDatabase
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

	override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {}
}