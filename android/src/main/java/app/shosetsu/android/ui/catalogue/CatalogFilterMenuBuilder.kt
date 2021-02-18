package app.shosetsu.android.ui.catalogue

import android.view.View
import android.widget.LinearLayout
import app.shosetsu.android.common.ext.handleObserve
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.view.widget.setting.CheckboxFilterInput
import app.shosetsu.android.view.widget.setting.SwitchFilterInput
import app.shosetsu.android.view.widget.setting.TextFilterInput
import app.shosetsu.android.view.widget.setting.TriStateFilterInput
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
		list.forEach {
			when (it) {
				is Filter.Header -> {
				}
				is Filter.Separator -> {
				}
				is Filter.Text -> {
					logV("Filter.Text -> TextFilterInput")
					addView(TextFilterInput(it, context))
				}
				is Filter.Switch -> {
					logV("Filter.Switch -> SwitchFilterInput")
					addView(SwitchFilterInput(it, context))
				}
				is Filter.Checkbox -> {
					logV("Filter.Checkbox -> CheckboxFilterInput")
					addView(CheckboxFilterInput(it, context))
				}
				is Filter.TriState -> {
					logV("Filter.TriState -> TriStateFilterInput")
					addView(TriStateFilterInput(it, context))
				}
				is Filter.Dropdown -> {
				}
				is Filter.RadioGroup -> {
				}
				is Filter.List -> {
				}
				is Filter.Group<*> -> {
				}
			}
		}
	}

	fun build(): View = ControllerCatalogFilterBottomMenuBinding.inflate(layoutInflater)
		.also {
			it.linearLayout.apply {
				controller.viewModel.filterItemsLive.handleObserve(controller) { list ->
					build(list)
				}
			}
		}.root
}