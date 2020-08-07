package com.github.doomsdayrs.apps.shosetsu.common.ext

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import app.shosetsu.lib.Filter
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.view.builder.SDViewBuilder
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.dsl.*

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
 * 01 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 */

fun Array<Filter<*>>.toSettingItems(
		formatter: Formatter,
		context: Context,
		setting: ShosetsuSettings
): List<SettingsItemData> {
	val settings = ArrayList<SettingsItemData>()
	forEach { filter ->
		when (filter) {
			is Filter.Checkbox -> {
				settings.add(
						checkBoxSettingData(filter.id) {
							title { filter.name }
							isChecked = setting.getFormSetting(formatter, filter.id) ?: filter.state
							onChecked { _, isChecked ->
								setting.setFormSetting(formatter, filter.id, isChecked)
							}
						}
				)
			}
			is Filter.Dropdown -> {
				settings.add(
						spinnerSettingData(filter.id) {
							title { filter.name }
							arrayAdapter = ArrayAdapter<String>(
									context,
									android.R.layout.simple_list_item_1,
									filter.choices
							)
							spinnerValue {
								setting.getFormSetting(formatter, filter.id) ?: filter.state
							}
							onSpinnerItemSelected { _, _, position, _ ->
								setting.setFormSetting(formatter, filter.id, position)
							}
						}
				)
			}
			is Filter.Group<*, *> -> {
			}
			is Filter.Header -> {
			}
			is Filter.List -> {
				settings.addAll(
						filter.filters.toSettingItems(formatter, context, setting)
				)
			}
			is Filter.RadioGroup -> {
				settings.add(
						spinnerSettingData(filter.id) {
							title { filter.name }
							arrayAdapter = ArrayAdapter<String>(
									context,
									android.R.layout.simple_list_item_1,
									filter.choices
							)
							spinnerValue {
								setting.getFormSetting(formatter, filter.id) ?: filter.state
							}
							onSpinnerItemSelected { _, _, position, _ ->
								setting.setFormSetting(formatter, filter.id, position)
							}
						}
				)
			}
			is Filter.Separator -> {
			}
			is Filter.Switch -> {
				settings.add(
						switchSettingData(filter.id) {
							title { filter.name }
							isChecked = setting.getFormSetting(formatter, filter.id) ?: filter.state
							onChecked { _, isChecked ->
								setting.setFormSetting(formatter, filter.id, isChecked)
							}
						}
				)
			}
			is Filter.Text -> {
				settings.add(
						textInputSettingData(filter.id) {
							title { filter.name }
							initialText = setting.getFormSetting(formatter, filter.id)
									?: filter.state
							doAfterTextChanged {
								setting.setFormSetting(formatter, filter.id, it.toString())
							}
						}
				)
			}
			is Filter.TriState -> {
			}
		}
	}
	return settings
}

fun Filter<*>.build(builder: SDViewBuilder) {
	when (this) {
		is Filter.Text -> builder.editText(name).also {
			it.onFocusChangeListener = View.OnFocusChangeListener { _, _ -> state = it.getValue() }
		}
		is Filter.Switch -> builder.switch(name, state).also {
			it.setOnCheckedChangeListener { _, v -> state = v }
		}
		is Filter.Dropdown -> builder.spinner(name, choices, state).also {
			it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
				override fun onNothingSelected(_p: AdapterView<*>?) {}
				override fun onItemSelected(_p: AdapterView<*>?, _v: View?, pos: Int, id: Long) {
					state = pos
				}
			}
		}
		is Filter.RadioGroup -> builder.radioGroup(name, choices, state).also {
			it.setOnCheckedChangeListener { _, i -> state = i }
		}
	}
}