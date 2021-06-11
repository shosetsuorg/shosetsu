package app.shosetsu.android.ui.intro

import android.Manifest.permission
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.shosetsu.android.common.ext.readAsset
import app.shosetsu.android.ui.splash.SplashScreen.Companion.INTRO_CODE
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.IntroLicenseBinding
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide

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
 * 15 / 03 / 2020
 */
class IntroductionActivity : IntroActivity() {
	internal class License : Fragment(R.layout.intro_license) {
		private var message = ""

		private lateinit var introLicenseBinding: IntroLicenseBinding

		override fun onSaveInstanceState(outState: Bundle) {
			outState.putString("message", message)
		}

		override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
		): View? =
			super.onCreateView(inflater, container, savedInstanceState)?.also {
				introLicenseBinding = IntroLicenseBinding.bind(it)
			}

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
			if (message.isEmpty() && savedInstanceState == null)
				message = activity?.readAsset("license-gplv3.txt") ?: ""
			else if (message.isEmpty() && savedInstanceState != null)
				message = savedInstanceState.getString("message", "")
			introLicenseBinding.title.text = message
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setNavigationPolicy(
			object : NavigationPolicy {
				override fun canGoForward(position: Int): Boolean = true
				override fun canGoBackward(position: Int): Boolean = false
			}
		)

		addSlide(
			SimpleSlide.Builder()
				.title(R.string.intro_title_greet)
				.background(R.color.colorPrimary)
				.image(R.drawable.shou_icon)
				.build()
		)

		addSlide(
			SimpleSlide.Builder()
				.title((R.string.intro_what_is_app))
				.description((R.string.intro_what_is_app_desc))
				.background(R.color.colorPrimary)
				.build()
		)

		addSlide(
			FragmentSlide.Builder()
				.background(R.color.colorPrimary)
				.fragment(License())
				.build()
		)

		addSlide(
			SimpleSlide.Builder()
				.title((R.string.intro_perm_title))
				.description((R.string.intro_perm_desc))
				.background(R.color.colorPrimary)
				.permissions(
					arrayOf(
						permission.READ_EXTERNAL_STORAGE,
						permission.WRITE_EXTERNAL_STORAGE
					)
				)
				.build()
		)

		addSlide(SimpleSlide.Builder()
			.title((R.string.intro_happy_end))
			.description((R.string.intro_happy_end_desc))
			.background(R.color.colorPrimary)
			.buttonCtaClickListener { finishActivity(INTRO_CODE) }
			.build())
	}
}