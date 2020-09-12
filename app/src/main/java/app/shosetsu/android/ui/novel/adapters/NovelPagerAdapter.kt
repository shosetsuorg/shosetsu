package app.shosetsu.android.ui.novel.adapters

import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.viewpager.widget.ViewPager
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.ext.context
import app.shosetsu.android.common.ext.getNovelID
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.ui.novel.pages.NovelChaptersController
import app.shosetsu.android.ui.novel.pages.NovelInfoController
import app.shosetsu.android.view.base.FABController
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

	private val controllers: Array<Controller> = arrayOf(
			NovelInfoController(bundleOf(
					BUNDLE_NOVEL_ID to novelController.args.getNovelID()
			)),
			NovelChaptersController(bundleOf(
					BUNDLE_NOVEL_ID to novelController.args.getNovelID()
			))
	)


	override fun configureRouter(router: Router, position: Int) {
		if (!router.hasRootController()) {
			router.setRoot(RouterTransaction.with(controllers[position]))
		}
	}

	override fun getCount(): Int = controllers.size

	override fun getPageTitle(position: Int): CharSequence? = titles.getItem(position)

	inner class PageController(
			private val fab: FloatingActionButton,
	) : ViewPager.OnPageChangeListener {
		private var currentPosition = 0

		override fun onPageScrollStateChanged(state: Int) {
		}

		override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
		}

		override fun onPageSelected(position: Int) {
			if (currentPosition != position) {
				Log.d(logID(), "Current position is $currentPosition")
				Log.d(logID(), "TheNext position is $position")

				val currentController = controllers[currentPosition]
				if (currentController is FABController) {
					currentController.hideFAB(fab)
					currentController.resetFAB(fab)
				}

				val newController = controllers[position]
				if (newController is FABController) {
					newController.setFABIcon(fab)
					newController.manipulateFAB(fab)
					newController.showFAB(fab)
				}
				currentPosition = position
			}
		}
	}
}