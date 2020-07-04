package com.github.doomsdayrs.apps.shosetsu.ui.updates
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

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_DATE
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters.UpdatedNovelsAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.UpdateUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IUpdatesViewModel
import java.util.*

/**
 * shosetsu
 * 20 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdateController(bundle: Bundle)
	: RecyclerController<UpdatedNovelsAdapter, UpdateUI>() {
	val updatesViewModel: IUpdatesViewModel by viewModel()

	var date: Long = bundle.getLong(BUNDLE_DATE)

	val novelIDs = ArrayList<Int>()

	override fun onViewCreated(view: View) {}

	override fun updateUI(list: List<UpdateUI>) {
		super.updateUI(list)
		with(list) {
			filter { !novelIDs.contains(it.novelID) }
					.forEach { novelIDs.add(it.novelID) }
		}
	}

	override fun difAreItemsTheSame(oldItem: UpdateUI, newItem: UpdateUI): Boolean =
			oldItem.chapterID == newItem.chapterID

	override fun setupRecyclerView() {
		super.setupRecyclerView()
		updatesViewModel.getTimeBetweenDates(
				date,
				date + 86399999
		).observe(this@UpdateController, Observer { handleRecyclerUpdate(it) })
	}

	override fun createRecyclerAdapter(): UpdatedNovelsAdapter =
			UpdatedNovelsAdapter(this, activity!!)
}