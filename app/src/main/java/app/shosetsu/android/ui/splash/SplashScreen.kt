package app.shosetsu.android.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.common.ext.requestPerms
import app.shosetsu.android.ui.intro.IntroductionActivity
import app.shosetsu.android.viewmodel.abstracted.ASplashScreenViewModel
import com.github.doomsdayrs.apps.shosetsu.R
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
 */


/**
 * Shosetsu
 * 9 / June / 2019
 */
class SplashScreen : AppCompatActivity(R.layout.splash_screen), KodeinAware {
	companion object {
		const val INTRO_CODE: Int = 1944
	}

	override val kodein: Kodein by closestKodein()
	private val viewModel: ASplashScreenViewModel by instance()


	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == INTRO_CODE) {
			viewModel.toggleShowIntro()
			startBoot()
		} else super.onActivityResult(requestCode, resultCode, data)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		this.requestPerms()
		super.onCreate(savedInstanceState)
		// Settings setup
		if (viewModel.showIntro())
			startActivityForResult(Intent(
					this,
					IntroductionActivity::class.java
			), INTRO_CODE)
		else startBoot()
	}

	private fun startBoot() {
		with(this@SplashScreen) {
			startActivity(Intent(this, MainActivity::class.java).apply {
				action = intent.action
				intent.extras?.let { putExtras(it) }
			})
			finish()
		}
	}
}