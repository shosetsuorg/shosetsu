package app.shosetsu.android.ui.browse

import android.view.LayoutInflater
import android.view.View
import app.shosetsu.android.ui.browse.adapter.BrowsePagerAdapter
import app.shosetsu.android.view.base.PushCapableController
import app.shosetsu.android.view.base.TabbedController
import app.shosetsu.android.view.base.ViewedController
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerBrowseBinding
import com.google.android.material.tabs.TabLayout

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
 *
 * Contains a viewpager
 * One to go browse, the other to install extensions
 */
class BrowseController : ViewedController<ControllerBrowseBinding>(),
		TabbedController, PushCapableController {
	override val viewTitleRes: Int = R.string.browse

	var adapter: BrowsePagerAdapter? = null
	lateinit var tabLayout: TabLayout
	lateinit var pushController: (Controller) -> Unit

	override fun onViewCreated(view: View) {
		adapter = BrowsePagerAdapter(this)
		binding.viewpager.adapter = adapter
		binding.viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

		tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
			override fun onTabSelected(tab: TabLayout.Tab) {
				binding.viewpager.currentItem = tab.position
			}

			override fun onTabUnselected(tab: TabLayout.Tab) {}
			override fun onTabReselected(tab: TabLayout.Tab) {}
		})

		tabLayout.post { tabLayout.setupWithViewPager(binding.viewpager) }

	}

	override fun bindView(inflater: LayoutInflater): ControllerBrowseBinding =
			ControllerBrowseBinding.inflate(inflater)

	override fun acceptTabLayout(tabLayout: TabLayout) {
		this.tabLayout = tabLayout
	}

	override fun acceptPushing(pushController: (Controller) -> Unit) {
		this.pushController = pushController
	}
}