package app.shosetsu.android.ui.catalogue

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.shosetsu.lib.Filter
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
 *
 * @since 02 / 08 / 2021
 */

@Preview
@Composable
fun CatalogFilterMenuPreview() = MdcTheme {
	CatalogFilterMenu(
		listOf(
			Filter.Header("This is a header"),
			Filter.Separator(),
			Filter.Text(1, "Text input"),
			Filter.Switch(2, "Switch"),
			Filter.Checkbox(3, "Checkbox"),
			Filter.TriState(4, "Tri state"),
			Filter.Dropdown(5, "Drop down", arrayOf("A", "B", "C")),
			Filter.RadioGroup(6, "Radio group", arrayOf("A", "B", "C")),
			Filter.List(
				"List", arrayOf(
					Filter.Switch(7, "Switch"),
					Filter.Checkbox(8, "Checkbox"),
					Filter.TriState(9, "Tri state"),
				)
			),
			Filter.Group(
				"Group", arrayOf(
					Filter.Switch(10, "Switch"),
					Filter.Switch(11, "Switch"),
					Filter.Switch(12, "Switch"),
				)
			)
		),
		getBoolean = { MutableStateFlow(false) },
		setBoolean = { _, _ -> },
		getInt = { MutableStateFlow(1) },
		setInt = { _, _ -> },
		getString = { MutableStateFlow("") },
		setString = { _, _ -> },
		applyFilter = {},
		resetFilter = {}
	)
}

@Composable
fun CatalogFilterMenu(
	items: List<Filter<*>>,
	getBoolean: (Filter<Boolean>) -> Flow<Boolean>,
	setBoolean: (Filter<Boolean>, Boolean) -> Unit,
	getInt: (Filter<Int>) -> Flow<Int>,
	setInt: (Filter<Int>, Int) -> Unit,
	getString: (Filter<String>) -> Flow<String>,
	setString: (Filter<String>, String) -> Unit,
	applyFilter: () -> Unit,
	resetFilter: () -> Unit
) {
	Column(
		modifier = Modifier,
		verticalArrangement = Arrangement.Bottom
	) {
		CatalogFilterMenuControlContent(resetFilter, applyFilter)

		CatalogFilterMenuFilterListContent(
			items,
			getBoolean,
			setBoolean,
			getInt,
			setInt,
			getString,
			setString
		)

	}
}

@Composable
fun CatalogFilterMenuFilterListContent(
	list: List<Filter<*>>,
	getBoolean: (Filter<Boolean>) -> Flow<Boolean>,
	setBoolean: (Filter<Boolean>, Boolean) -> Unit,
	getInt: (Filter<Int>) -> Flow<Int>,
	setInt: (Filter<Int>, Int) -> Unit,
	getString: (Filter<String>) -> Flow<String>,
	setString: (Filter<String>, String) -> Unit
) {

	LazyColumn(
		modifier = Modifier
			.fillMaxWidth(),
		contentPadding = PaddingValues(16.dp),
		verticalArrangement = Arrangement.Bottom
	) {
		items(list) { filter ->
			when (filter) {
				is Filter.Header -> Column {
					Divider()
				}
				is Filter.Separator -> Divider()
				is Filter.Text -> CatalogFilterMenuTextContent(filter, getString, setString)
				is Filter.Switch -> CatalogFilterMenuSwitchContent(filter, getBoolean, setBoolean)
				is Filter.Checkbox ->
					CatalogFilterMenuCheckboxContent(filter, getBoolean, setBoolean)
				is Filter.TriState -> CatalogFilterMenuTriStateContent(filter, getInt, setInt)
				is Filter.Dropdown -> CatalogFilterMenuDropDownContent(filter, getInt, setInt)
				is Filter.RadioGroup -> CatalogFilterMenuRadioGroupContent(filter, getInt, setInt)
				is Filter.List -> {
					CatalogFilterMenuFilterListContent(
						filter.filters.toList(),
						filter.name,
						getBoolean, setBoolean, getInt, setInt, getString, setString
					)
				}
				is Filter.Group<*> -> {
					CatalogFilterMenuFilterListContent(
						filter.filters.toList(),
						filter.name,
						getBoolean, setBoolean, getInt, setInt, getString, setString
					)
				}
			}
		}
	}
}

@Preview
@Composable
fun PreviewCatalogFilterMenuFilterListContent() = MdcTheme {
	CatalogFilterMenuFilterListContent(
		list = listOf(
			Filter.Switch(7, "Switch"),
			Filter.Checkbox(8, "Checkbox"),
			Filter.TriState(9, "Tri state"),
		),
		name = "A list",
		getBoolean = { MutableStateFlow(false) },
		setBoolean = { _, _ -> },
		getInt = { MutableStateFlow(1) },
		setInt = { _, _ -> },
		getString = { MutableStateFlow("") },
		setString = { _, _ -> }
	)
}

@Composable
fun CatalogFilterMenuFilterListContent(
	list: List<Filter<*>>,
	name: String,
	getBoolean: (Filter<Boolean>) -> Flow<Boolean>,
	setBoolean: (Filter<Boolean>, Boolean) -> Unit,
	getInt: (Filter<Int>) -> Flow<Int>,
	setInt: (Filter<Int>, Int) -> Unit,
	getString: (Filter<String>) -> Flow<String>,
	setString: (Filter<String>, String) -> Unit
) {
	var collapsed by remember { mutableStateOf(true) }
	Column(
		modifier = Modifier.fillMaxWidth()
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(text = name)
			IconToggleButton(
				onCheckedChange = {
					collapsed = it
				},
				checked = collapsed
			) {
				if (collapsed)
					Icon(painterResource(R.drawable.expand_more), "")
				else
					Icon(painterResource(R.drawable.expand_less), "")
			}
		}

		if (!collapsed) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 8.dp, end = 8.dp)
			) {
				list.forEach { filter ->
					when (filter) {
						is Filter.Header -> Column {
							Divider()
						}
						is Filter.Separator -> Divider()
						is Filter.Text -> CatalogFilterMenuTextContent(filter, getString, setString)
						is Filter.Switch -> CatalogFilterMenuSwitchContent(
							filter,
							getBoolean,
							setBoolean
						)
						is Filter.Checkbox -> CatalogFilterMenuCheckboxContent(
							filter,
							getBoolean,
							setBoolean
						)
						is Filter.TriState -> CatalogFilterMenuTriStateContent(
							filter,
							getInt,
							setInt
						)
						is Filter.Dropdown -> CatalogFilterMenuDropDownContent(
							filter,
							getInt,
							setInt
						)
						is Filter.RadioGroup -> CatalogFilterMenuRadioGroupContent(
							filter,
							getInt,
							setInt
						)
						is Filter.List -> {
							Log.e(
								"FilterListContent",
								"CatalogFilterMenuFilterListContent: Please avoid usage of lists in sub lists"
							)
							CatalogFilterMenuFilterListContent(
								filter.filters.toList(),
								filter.name,
								getBoolean,
								setBoolean,
								getInt,
								setInt,
								getString,
								setString
							)
						}
						is Filter.Group<*> -> {
							Log.e(
								"FilterListContent",
								"CatalogFilterMenuFilterListContent: Please avoid usage of lists in sub lists"
							)
							CatalogFilterMenuFilterListContent(
								filter.filters.toList(),
								filter.name,
								getBoolean,
								setBoolean,
								getInt,
								setInt,
								getString,
								setString
							)
						}
					}
				}

			}
		}
	}
}

@Preview
@Composable
fun PreviewCatalogFilterMenuTextContent() =
	MdcTheme {
		CatalogFilterMenuTextContent(
			filter = Filter.Text(0, "This is a text input"),
			{ MutableStateFlow("") },
			{ _, _ -> }
		)
	}

@Composable
fun CatalogFilterMenuTextContent(
	filter: Filter.Text,
	getString: (Filter<String>) -> Flow<String>,
	setString: (Filter<String>, String) -> Unit
) {
	val text by getString(filter)
		.collectAsState(initial = "")

	TextField(
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp),
		value = text,
		onValueChange = { setString(filter, it) },
		label = {
			Text(text = filter.name)
		}
	)
}

@Preview
@Composable
fun PreviewCatalogFilterMenuSwitchContent() = MdcTheme {
	CatalogFilterMenuSwitchContent(
		Filter.Switch(0, "Switch"),
		{ MutableStateFlow(false) },
		{ _, _ -> }
	)
}

@Composable
fun CatalogFilterMenuSwitchContent(
	filter: Filter.Switch,
	getBoolean: (Filter<Boolean>) -> Flow<Boolean>,
	setBoolean: (Filter<Boolean>, Boolean) -> Unit
) {
	val state by getBoolean(filter)
		.collectAsState(initial = false)

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(text = filter.name)
		Switch(
			checked = state,
			onCheckedChange = { setBoolean(filter, it) }
		)
	}
}

@Preview
@Composable
fun PreviewCatalogFilterMenuCheckboxContent() = MdcTheme {
	CatalogFilterMenuCheckboxContent(Filter.Checkbox(0, "Checkbox"),
		{ MutableStateFlow(false) },
		{ _, _ -> })
}

@Composable
fun CatalogFilterMenuCheckboxContent(
	filter: Filter.Checkbox,
	getBoolean: (Filter<Boolean>) -> Flow<Boolean>,
	setBoolean: (Filter<Boolean>, Boolean) -> Unit
) {
	val state by getBoolean(filter)
		.collectAsState(initial = false)

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(text = filter.name)
		Checkbox(
			checked = state,
			onCheckedChange = { setBoolean(filter, it) }
		)
	}
}

@Preview
@Composable
fun PreviewCatalogFilterMenuTriStateContent() = MdcTheme {
	CatalogFilterMenuTriStateContent(Filter.TriState(0, "Tristate"),
		{ MutableStateFlow(1) },
		{ _, _ -> })
}

@Composable
fun CatalogFilterMenuTriStateContent(
	filter: Filter.TriState,
	getInt: (Filter<Int>) -> Flow<Int>,
	setInt: (Filter<Int>, Int) -> Unit
) {
	val triState by getInt(filter)
		.collectAsState(initial = Filter.TriState.STATE_IGNORED)

	val convertedState = when (triState) {
		Filter.TriState.STATE_IGNORED -> ToggleableState.Off
		Filter.TriState.STATE_EXCLUDE -> ToggleableState.Indeterminate
		Filter.TriState.STATE_INCLUDE -> ToggleableState.On
		else -> ToggleableState.Off
	}

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(text = filter.name)
		TriStateCheckbox(
			state = convertedState,
			onClick = {
				setInt(
					filter,
					when (triState) {
						Filter.TriState.STATE_IGNORED -> Filter.TriState.STATE_INCLUDE
						Filter.TriState.STATE_INCLUDE -> Filter.TriState.STATE_EXCLUDE
						Filter.TriState.STATE_EXCLUDE -> Filter.TriState.STATE_IGNORED
						else -> Filter.TriState.STATE_IGNORED
					}
				)
			}
		)
	}
}

@Preview
@Composable
fun PreviewCatalogFilterMenuDropDownContent() = MdcTheme {
	CatalogFilterMenuDropDownContent(
		Filter.Dropdown(0, "Dropdown", arrayOf("A", "B", "C")),
		{ MutableStateFlow(1) },
		{ _, _ -> }
	)
}

@Composable
fun CatalogFilterMenuDropDownContent(
	filter: Filter.Dropdown,
	getInt: (Filter<Int>) -> Flow<Int>,
	setInt: (Filter<Int>, Int) -> Unit
) {
	val selection by getInt(filter)
		.collectAsState(initial = 0)
	var expanded by remember { mutableStateOf(false) }

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(text = filter.name)


		Row(
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = AnnotatedString(filter.choices[selection]),
				modifier = Modifier.clickable(onClick = {
					expanded = true
				})
			)
			IconToggleButton(
				onCheckedChange = {
					expanded = it
				},
				checked = expanded,
				modifier = Modifier.wrapContentWidth()
			) {

				if (expanded)
					Icon(painterResource(R.drawable.expand_less), "")
				else
					Icon(painterResource(R.drawable.expand_more), "")
			}
			DropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false },
			) {
				filter.choices.forEachIndexed { i, s ->
					DropdownMenuItem(
						onClick = {
							setInt(filter, i)
							expanded = false
						}) {
						Text(
							text = AnnotatedString(s)
						)
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun PreviewCatalogFilterMenuRadioGroupContent() = MdcTheme {
	CatalogFilterMenuRadioGroupContent(
		Filter.RadioGroup(0, "Dropdown", arrayOf("A", "B", "C")),
		{ MutableStateFlow(1) },
		{ _, _ -> }
	)
}

@Composable
fun CatalogFilterMenuRadioGroupContent(
	filter: Filter.RadioGroup,
	getInt: (Filter<Int>) -> Flow<Int>,
	setInt: (Filter<Int>, Int) -> Unit
) {
	val selection by getInt(filter)
		.collectAsState(initial = 0)
	var expanded by remember { mutableStateOf(true) }

	Column(
		modifier = Modifier.fillMaxWidth(),
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(text = filter.name)

			IconToggleButton(
				onCheckedChange = {
					expanded = it
				},
				checked = expanded
			) {
				if (expanded)
					Icon(painterResource(R.drawable.expand_less), "")
				else
					Icon(painterResource(R.drawable.expand_more), "")
			}
		}

		if (expanded) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 8.dp, end = 8.dp),
			) {
				filter.choices.forEachIndexed { index, s ->
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text(text = s)
						RadioButton(
							selected = index == selection,
							onClick = { setInt(filter, index) }
						)
					}
				}
			}
		}
	}
}

@Composable
fun CatalogFilterMenuControlContent(
	resetFilter: () -> Unit,
	applyFilter: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth(),
		shape = RectangleShape
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceEvenly
		) {
			TextButton(onClick = { resetFilter() }, contentPadding = PaddingValues(8.dp)) {
				Text(text = stringResource(id = R.string.reset))
			}

			TextButton(onClick = { applyFilter() }, contentPadding = PaddingValues(8.dp)) {
				Text(text = stringResource(id = R.string.apply))
			}
		}
	}
}