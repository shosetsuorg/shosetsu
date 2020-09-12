package app.shosetsu.android.view.builder

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.drawerlayout.widget.DrawerLayout
import app.shosetsu.android.view.base.SecondDrawerController
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.layout.drawer_layout
import com.google.android.material.navigation.NavigationView


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
 * ====================================================================
 */

/**
 * shosetsu
 * 06 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 *
 * All added views are
 */
class SDBuilder(
		private val navigationView: NavigationView,
		private val drawerLayout: DrawerLayout,
		secondDrawerController: SecondDrawerController,
) : SDViewBuilder(navigationView, secondDrawerController) {

	private val parentView = inflater.inflate(drawer_layout, navigationView, false)

	override fun build(): View {
		parentView.findViewById<Button>(R.id.accept).setOnClickListener {
			secondDrawerController.handleConfirm(layout)
			drawerLayout.closeDrawer(viewGroup)
		}
		parentView.findViewById<LinearLayout>(R.id.linearLayout).addView(layout)
		parentView.findViewById<Button>(R.id.reset).setOnClickListener {
			viewGroup.removeAllViews()
			secondDrawerController.createDrawer(navigationView, drawerLayout)
			drawerLayout.closeDrawer(viewGroup)
		}
		return parentView
	}
}