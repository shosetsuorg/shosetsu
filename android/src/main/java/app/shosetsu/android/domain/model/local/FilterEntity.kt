package app.shosetsu.android.domain.model.local

import app.shosetsu.android.common.enums.TriStateState
import app.shosetsu.common.enums.TriStateState
import app.shosetsu.lib.Filter
import app.shosetsu.lib.Filter.TriState.Companion.STATE_EXCLUDE
import app.shosetsu.lib.Filter.TriState.Companion.STATE_INCLUDE
import kotlin.random.Random

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

/**
 * Shosetsu
 *
 * @since 16 / 11 / 2021
 * @author Doomsdayrs
 */
sealed class FilterEntity {
	abstract val id: Int
	abstract val name: String

	data class Header(
		override val name: String,
		override val id: Int = Random.nextInt() + 100000,
	) : FilterEntity()

	data class Separator(
		override val name: String,
		override val id: Int = Random.nextInt() + 100000,
	) : FilterEntity()

	data class Text(
		override val id: Int,
		override val name: String,
		val state: String = ""
	) : FilterEntity()

	data class Switch(
		override val id: Int,
		override val name: String,
		val state: Boolean = false
	) : FilterEntity()

	data class Checkbox(
		override val id: Int,
		override val name: String,
		val state: Boolean = false
	) : FilterEntity()

	data class TriState(
		override val id: Int,
		override val name: String,
		val state: TriStateState = TriStateState.IGNORED
	) : FilterEntity()

	data class Dropdown(
		override val id: Int,
		override val name: String,
		val choices: List<String>,
		val selected: Int = 0,
	) : FilterEntity()

	data class RadioGroup(
		override val id: Int,
		override val name: String,
		val choices: List<String>,
		val selected: Int = 0,
	) : FilterEntity()

	data class FList(
		override val name: String,
		val filters: List<FilterEntity>,
		override val id: Int = Random.nextInt() + 10000,
	) : FilterEntity()

	data class Group(
		override val name: String,
		val filters: List<FilterEntity>,
		override val id: Int = Random.nextInt() + 10000,
	) : FilterEntity()

	companion object {
		/**
		 * Convert kotlin-lib [Filter] into a Shosetsu Filter
		 */
		fun fromFilter(filter: Filter<*>): FilterEntity =
			when (filter) {
				is Filter.Header -> Header(filter.name)
				is Filter.Separator -> Separator(filter.name)
				is Filter.Text -> Text(filter.id, filter.name, filter.state)
				is Filter.Switch -> Switch(filter.id, filter.name, filter.state)
				is Filter.Checkbox -> Checkbox(filter.id, filter.name, filter.state)
				is Filter.TriState -> TriState(
					filter.id,
					filter.name,
					when (filter.state) {
						STATE_INCLUDE -> TriStateState.CHECKED
						STATE_EXCLUDE -> TriStateState.UNCHECKED
						else -> TriStateState.IGNORED;
					}
				)
				is Filter.Dropdown -> Dropdown(
					filter.id,
					filter.name,
					filter.choices.toList(),
					filter.state
				)
				is Filter.RadioGroup -> RadioGroup(
					filter.id,
					filter.name,
					filter.choices.toList(),
					filter.state
				)
				is Filter.List -> FList(filter.name, filter.filters.map { fromFilter(it) })
				is Filter.Group<*> -> Group(filter.name, filter.filters.map { fromFilter(it) })
			}
	}
}