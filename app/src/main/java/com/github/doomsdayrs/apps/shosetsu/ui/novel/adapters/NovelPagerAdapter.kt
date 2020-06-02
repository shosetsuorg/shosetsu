package com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters

import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.getNovelID
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelChaptersController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelInfoController

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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class NovelPagerAdapter(private val novelController: NovelController)
	: RouterPagerAdapter(novelController) {
	companion object {
		private const val INFO_CONTROLLER = 0
		private const val CHAPTERS_CONTROLLER = 1
		private const val TRACK_CONTROLLER = 2
	}

	private val titles by lazy {
		ArrayAdapter(
				novelController.context!!,
				android.R.layout.simple_spinner_item,
				novelController.resources!!.getStringArray(R.array.novel_fragment_names)
		)
	}

	override fun configureRouter(router: Router, position: Int) {
		if (!router.hasRootController()) {
			Log.d("Swap Screen", titles.getItem(position) ?: "Unknown")

			val controller = when (position) {
				INFO_CONTROLLER -> {
					NovelInfoController(bundleOf(
							BUNDLE_NOVEL_ID to novelController.bundle.getNovelID()
					))
				}
				CHAPTERS_CONTROLLER -> {
					NovelChaptersController(bundleOf(
							BUNDLE_NOVEL_ID to novelController.bundle.getNovelID()
					))
				}
				else -> error("Wrong position $position")
			}
			router.setRoot(RouterTransaction.with(controller))
		}
	}

	override fun getCount(): Int = 2

	override fun getPageTitle(position: Int): CharSequence? = titles.getItem(position)
}