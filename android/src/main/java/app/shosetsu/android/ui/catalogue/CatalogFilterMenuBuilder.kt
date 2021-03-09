package app.shosetsu.android.ui.catalogue

import android.view.View
import android.widget.LinearLayout
import app.shosetsu.android.common.ext.createUI
import app.shosetsu.android.common.ext.handleObserve
import app.shosetsu.lib.Filter
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerCatalogFilterBottomMenuBinding

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

	private fun LinearLayout.build(list: List<Filter<*>>) {
		list.createUI(context).forEach {
			addView(it)
		}
	}

	fun build(): View = ControllerCatalogFilterBottomMenuBinding.inflate(layoutInflater)
		.also { binding ->
			binding.extensionFilters.apply {
				controller.viewModel.filterItemsLive.handleObserve(controller) { list ->
					removeAllViews()
					build(list)
				}
			}
			binding.applyButton.setOnClickListener {

			}

		}.root
}