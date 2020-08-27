package com.github.doomsdayrs.apps.shosetsu.ui.updates

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

import android.view.View
import androidx.lifecycle.observe
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.openChapter
import com.github.doomsdayrs.apps.shosetsu.common.ext.setOnClickListener
import com.github.doomsdayrs.apps.shosetsu.view.base.FastAdapterRecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.UpdateUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IUpdatesViewModel
import org.kodein.di.generic.instance

/**
 * shosetsu
 * 15 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatesController : FastAdapterRecyclerController<UpdateUI>() {
	val viewModel by instance<IUpdatesViewModel>()
	override val viewTitle: Int = R.string.updates
	override fun onViewCreated(view: View) {}

	override fun setupFastAdapter() {
		fastAdapter.setOnClickListener { _, _, (chapterID, novelID), _ ->
			activity?.openChapter(chapterID, novelID)
			true
		}
		startObservation()
	}

	private fun startObservation() {
		viewModel.liveData.observe(this) {
			handleRecyclerUpdate(it)
		}
	}
}