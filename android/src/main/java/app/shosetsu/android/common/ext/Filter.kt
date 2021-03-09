package app.shosetsu.android.common.ext

import android.content.Context
import android.view.View
import app.shosetsu.android.view.widget.setting.*
import app.shosetsu.lib.Filter

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
 * 09 / 03 / 2021
 */


fun List<Filter<*>>.createUI(context: Context): List<View> =
	map {
		when (it) {
			is Filter.Header -> {
				logV("Filter.Header -> HeaderInput")
				HeaderInput(it, context)
			}
			is Filter.Separator -> {
				null
			}
			is Filter.Text -> {
				logV("Filter.Text -> TextFilterInput")
				TextFilterInput(it, context)
			}
			is Filter.Switch -> {
				logV("Filter.Switch -> SwitchFilterInput")
				SwitchFilterInput(it, context)
			}
			is Filter.Checkbox -> {
				logV("Filter.Checkbox -> CheckboxFilterInput")
				CheckboxFilterInput(it, context)
			}
			is Filter.TriState -> {
				logV("Filter.TriState -> TriStateFilterInput")
				TriStateFilterInput(it, context)
			}
			is Filter.Dropdown -> {
				null
			}
			is Filter.RadioGroup -> {
				logV("Filter.RadioGroup -> RadioGroupInput")
				RadioGroupInput(it, context)
			}
			is Filter.List -> {
				ListInput(it, context)
				null
			}
			is Filter.Group<*> -> {
				null
			}
		}
	}.filterNotNull()