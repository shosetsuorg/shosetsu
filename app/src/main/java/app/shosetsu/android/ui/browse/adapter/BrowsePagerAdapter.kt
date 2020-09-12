package app.shosetsu.android.ui.browse.adapter

import android.widget.ArrayAdapter
import app.shosetsu.android.common.ext.context
import app.shosetsu.android.ui.browse.BrowseController
import app.shosetsu.android.ui.catalogue.CatalogsController
import app.shosetsu.android.ui.extensions.ExtensionsController
import app.shosetsu.android.view.base.PushCapableController
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.R

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
 * shosetsu
 * 12 / 09 / 2020
 */
class BrowsePagerAdapter(
		private val browseController: BrowseController
) : RouterPagerAdapter(browseController) {
	private val controllers: Array<Controller> by lazy {
		arrayOf(
				CatalogsController(),
				ExtensionsController()
		)
	}

	private val titles by lazy {
		ArrayAdapter(
				browseController.context!!,
				android.R.layout.simple_spinner_item,
				browseController.resources!!.getStringArray(R.array.browse_tab_names)
		)
	}

	override fun getPageTitle(position: Int): CharSequence? = titles.getItem(position)

	override fun getCount(): Int = 2

	override fun configureRouter(router: Router, position: Int) {
		if (!router.hasRootController()) {
			router.setRoot(RouterTransaction.with(controllers[position].also { c ->

				if (c is PushCapableController) c.acceptPushing {
					browseController.pushController(it)
				}

			}))
		}
	}

}