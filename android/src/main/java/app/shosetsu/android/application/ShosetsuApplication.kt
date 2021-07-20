package app.shosetsu.android.application

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.Configuration
import app.shosetsu.android.backend.database.DBHelper
import app.shosetsu.android.common.consts.Notifications
import app.shosetsu.android.common.consts.ShortCuts
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.di.*
import app.shosetsu.android.domain.usecases.StartRepositoryUpdateManagerUseCase
import app.shosetsu.android.viewmodel.factory.ViewModelFactory
import app.shosetsu.common.domain.repositories.base.IExtensionLibrariesRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.lib.ShosetsuSharedLib
import app.shosetsu.lib.lua.ShosetsuLuaLib
import app.shosetsu.lib.lua.shosetsuGlobals
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import okhttp3.OkHttpClient
import org.acra.config.dialog
import org.acra.config.httpSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.acra.sender.HttpSender.Method
import org.kodein.di.*
import org.kodein.di.android.x.androidXModule

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
class ShosetsuApplication : Application(), LifecycleEventObserver, DIAware,
	Configuration.Provider {
	private val extLibRepository by instance<IExtensionLibrariesRepository>()
	private val okHttpClient by instance<OkHttpClient>()
	private val startRepositoryUpdateManagerUseCase: StartRepositoryUpdateManagerUseCase by instance()

	override val di: DI by DI.lazy {
		bind<ViewModelFactory>() with singleton { ViewModelFactory(applicationContext) }
		import(othersModule)
		import(providersModule)
		import(dataSourceModule)
		import(networkModule)
		import(databaseModule)
		import(repositoryModule)
		import(useCaseModule)
		import(viewModelsModule)
		import(androidXModule(this@ShosetsuApplication))
	}

	override fun attachBaseContext(base: Context?) {
		super.attachBaseContext(base)
		setupACRA()
		Notifications.createChannels(this)
		ShortCuts.createShortcuts(this)
	}

	override fun onCreate() {
		ShosetsuSharedLib.httpClient = okHttpClient
		ShosetsuSharedLib.logger = { ext, arg ->
			Log.i(ext, arg)
		}
		ShosetsuLuaLib.libLoader = libLoader@{ name ->
			Log.i("LuaLibLoader", "Loading ($name)")
			return@libLoader when (val result = extLibRepository.blockingLoadExtLibrary(name)) {
				is HResult.Success -> {
					val l = try {
						shosetsuGlobals().load(result.data, "lib($name)")
					} catch (e: Error) {
						throw e
					}
					l.call()
				}
				else -> {
					if (result is HResult.Error)
						logE("[${result.code}]\t${result.message}", result.exception)
					null
				}
			}
		}

		// OLD DB TO NEW
		@Suppress("DEPRECATION")
		DBHelper(this@ShosetsuApplication).writableDatabase.close()

		startRepositoryUpdateManagerUseCase()
		super.onCreate()
	}

	private fun setupACRA() {
		initAcra {
			buildConfigClass = BuildConfig::class.java
			reportFormat = StringFormat.JSON
			dialog {
				commentPrompt = getString(R.string.crashCommentPromt)
				text = getString(R.string.crashDialogText)
				resTheme = R.style.AppTheme_CrashReport
			}
			httpSender {
				uri = "https://acra.shosetsu.app/report" /*best guess, you may need to adjust this*/
				basicAuthLogin = BuildConfig.acraUsername
				basicAuthPassword = BuildConfig.acraPassword
				httpMethod = Method.POST
			}
		}
	}

	override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {}

	override fun getWorkManagerConfiguration(): Configuration =
		Configuration.Builder().apply {
		}.build()
}