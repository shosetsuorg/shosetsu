package com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters

import android.util.Log
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdateController
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdatesController
import com.github.doomsdayrs.apps.shosetsu.common.ext.getString
import com.github.doomsdayrs.apps.shosetsu.common.ext.trimDate
import org.joda.time.DateTime

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
 */ /**
 * shosetsu
 * 20 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatedDaysPager(private val updateController: UpdatesController, val controllers: Array<UpdateController>) : RouterPagerAdapter(updateController) {
	override fun getPageTitle(position: Int): CharSequence? {
		val dateTime = DateTime(controllers[position].date)
		if (dateTime == DateTime(System.currentTimeMillis()).trimDate()) {
			return updateController.getString(R.string.today, "Today")
		} else if (dateTime == DateTime(System.currentTimeMillis()).trimDate().minusDays(1)) {
			return updateController.getString(R.string.yesterday, "Yesterday")
		}
		return dateTime.dayOfMonth.toString() + "/" + dateTime.monthOfYear + "/" + dateTime.year
	}

	override fun configureRouter(router: Router, position: Int) {
		if (!router.hasRootController()) {
			Log.d("SwapScreen", controllers[position].toString())
			val controller = controllers[position]
			router.setRoot(RouterTransaction.with(controller))
		}
	}

	override fun getCount(): Int {
		return controllers.size
	}
}