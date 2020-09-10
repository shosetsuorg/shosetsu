package com.github.doomsdayrs.apps.shosetsu.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.activity.MainActivity
import com.github.doomsdayrs.apps.shosetsu.backend.initPreferences
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchUI
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.requestPerms
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.InitializeExtensionsUseCase
import com.github.doomsdayrs.apps.shosetsu.ui.intro.IntroductionActivity
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
		const val INTRO_CODE: Int = 1944
		private var firstOpen = true
	}

	override val kodein: Kodein by closestKodein()
	val settings: ShosetsuSettings by instance()

	lateinit var textView: TextView

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == INTRO_CODE) startBoot()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		this.requestPerms()
		super.onCreate(savedInstanceState)
		initPreferences(this, settings)
		textView = findViewById(R.id.title)
		// Settings setup
		if (settings.showIntro) {
			Log.i(logID(), "First time, Launching activity")
			startActivityForResult(Intent(
					this,
					IntroductionActivity::class.java
			), INTRO_CODE)
		} else {
			startBoot()
		}
	}

	private fun progressUpdate(string: String) = launchUI {
		textView.post { textView.text = string }
	}


	private val useCase by instance<InitializeExtensionsUseCase>()


	private fun startBoot() {
		launchIO scope@{
			if (firstOpen) {
				progressUpdate("Setting up the application")
				useCase.invoke { progressUpdate(it) }
				firstOpen = false
				launchUI {
					with(this@SplashScreen) {
						val intent = Intent(this, MainActivity::class.java)
						Log.i(logID(), "Passing Intent ${this.intent.action}")
						intent.action = this.intent.action
						this.intent.extras?.let { intent.putExtras(it) }
						startActivity(intent)
						progressUpdate("Finished! Going to app now~")
						finish()
					}
				}
			} else {
				Log.i(logID(), "Broadcasting intent ${intent.action}")
				sendBroadcast(Intent(intent.action))
				finish()
			}
		}
	}

}