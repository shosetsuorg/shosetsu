package app.shosetsu.android.ui.catalogue

import android.view.View
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerCatalogFilterBottomMenuBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil

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
 * 17 / 02 / 2021
 */
class CatalogFilterMenuBuilder(
	private val controller: CatalogController
) {
	@Suppress("ProtectedInFinal")
	protected val layoutInflater = controller.activity!!.layoutInflater

	fun build(): View = ControllerCatalogFilterBottomMenuBinding.inflate(layoutInflater)
		.also { binding ->
			binding.extensionFilters.apply {
				val itemAdapter = ItemAdapter<SettingsItemData>()
				adapter = FastAdapter.with(itemAdapter)
				controller.viewModel.filterItemsLive.handleObserve(controller,
					onLoading = {
						logD("Loading")
					}, onEmpty = {
						logD("Empty")
					},
					onError = {
						logE("Error")
					}) { list ->
					logV("Reloading")
					FastAdapterDiffUtil[itemAdapter] = FastAdapterDiffUtil.calculateDiff(
						itemAdapter,
						list
					)
				}
			}
			binding.applyButton.setOnClickListener {
				logI("Apply button clicked")
				controller.viewModel.applyFilter()
			}
			binding.resetButton.setOnClickListener {
				logI("Reset button clicked")
				controller.viewModel.resetFilter()
			}

		}.root
}