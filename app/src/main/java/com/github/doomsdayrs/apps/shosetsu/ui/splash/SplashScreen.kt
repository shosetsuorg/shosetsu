package com.github.doomsdayrs.apps.shosetsu.ui.splash

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import app.shosetsu.lib.ShosetsuLib
import app.shosetsu.lib.shosetsuGlobals
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.activity.MainActivity
import com.github.doomsdayrs.apps.shosetsu.backend.connectivityManager
import com.github.doomsdayrs.apps.shosetsu.backend.database.DBHelper
import com.github.doomsdayrs.apps.shosetsu.backend.initPreferences
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchUI
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.requestPerms
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtLibRepository
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.InitializeExtensionsUseCase
import com.github.doomsdayrs.apps.shosetsu.ui.intro.IntroductionActivity
import okhttp3.OkHttpClient
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance


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
class SplashScreen : AppCompatActivity(R.layout.splash_screen), KodeinAware {
	companion object {
		const val INTRO_CODE = 1944
	}

	override val kodein: Kodein by closestKodein()
	val settings: ShosetsuSettings by instance()

	lateinit var textView: TextView

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == INTRO_CODE) {
			startBoot()

			// Set so that debug versions are perm show intro
			if (!BuildConfig.DEBUG) settings.showIntro = false
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		this.requestPerms()
		super.onCreate(savedInstanceState)
		initPreferences(this, settings)
		textView = findViewById(R.id.title)
		// Sets up old SQL database
		DBHelper(this).writableDatabase

		// Settings setup
		connectivityManager = getSystemService<ConnectivityManager>()!!
		if (settings.showIntro) {
			Log.i(logID(), "First time, Launching activity")
			val i = Intent(this, IntroductionActivity::class.java)
			startActivityForResult(i, INTRO_CODE)
		} else {
			startBoot()
		}
	}

	private fun progressUpdate(string: String) = launchUI {
		textView.post { textView.text = string }
	}


	private val useCase by instance<InitializeExtensionsUseCase>()
	private val extLibRepository by instance<IExtLibRepository>()
	private val okHttpClient by instance<OkHttpClient>()

	private fun startBoot() {
		launchIO {
			progressUpdate("Setting up the application")
			useCase.invoke { progressUpdate(it) }
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
			launchUI {
				val intent = Intent(this@SplashScreen, MainActivity::class.java)
				intent.action = intent.action
				intent.extras?.let { intent.putExtras(it) }
				startActivity(intent)
				progressUpdate("Finished! Going to app now~")
				this@SplashScreen.finish()
			}
		}
	}

}