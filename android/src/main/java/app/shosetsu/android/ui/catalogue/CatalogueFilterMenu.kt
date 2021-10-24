package app.shosetsu.android.ui.catalogue

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.loading
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.enums.NovelCardType
import app.shosetsu.lib.Filter
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.flow.flow

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


private val dummyViewModel = @SuppressLint("StaticFieldLeak")
object : ACatalogViewModel() {
	override val itemsLive: LiveData<HResult<List<ACatalogNovelUI>>>
		get() = TODO("Not yet implemented")
	override val filterItemsLive: LiveData<HResult<List<Filter<*>>>> by lazy {
		flow {
			emit(
				successResult(
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
					)
				)
			)
		}.asIOLiveData()
	}
	override val hasSearchLive: LiveData<Boolean>
		get() = TODO("Not yet implemented")
	override val extensionName: LiveData<HResult<String>>
		get() = TODO("Not yet implemented")
	override val novelCardTypeLive: LiveData<NovelCardType>
		get() = TODO("Not yet implemented")

	override fun setExtensionID(extensionID: Int) {
		TODO("Not yet implemented")
	}

	override fun applyQuery(newQuery: String) {
		TODO("Not yet implemented")
	}

	override fun loadMore() {
		TODO("Not yet implemented")
	}

	override fun resetView() {
		TODO("Not yet implemented")
	}

	override fun backgroundNovelAdd(novelID: Int): LiveData<HResult<*>> {
		TODO("Not yet implemented")
	}

	override fun applyFilter() {
		TODO("Not yet implemented")
	}

	override fun resetFilter() {
		TODO("Not yet implemented")
	}

	override fun setViewType(cardType: NovelCardType) {
		TODO("Not yet implemented")
	}

	override fun destroy() {
		TODO("Not yet implemented")
	}

	override fun getFilterStringState(id: Filter<String>): LiveData<String> =
		flow { emit(id.state) }.asIOLiveData()

	override fun setFilterStringState(id: Filter<String>, value: String) {
		TODO("Not yet implemented")
	}

	override fun getFilterBooleanState(id: Filter<Boolean>): LiveData<Boolean> =
		flow { emit(id.state) }.asIOLiveData()

	override fun setFilterBooleanState(id: Filter<Boolean>, value: Boolean) {
		TODO("Not yet implemented")
	}

	override fun getFilterIntState(id: Filter<Int>): LiveData<Int> =
		flow { emit(id.state) }.asIOLiveData()

	override fun setFilterIntState(id: Filter<Int>, value: Int) {
		TODO("Not yet implemented")
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		TODO("Not yet implemented")
	}

	override fun getBaseURL(): LiveData<HResult<String>> {
		TODO("Not yet implemented")
	}


	override val columnsInH: Int
		get() = TODO("Not yet implemented")

	override val columnsInP: Int
		get() = TODO("Not yet implemented")
}


@Preview
@Composable
fun CatalogFilterMenuPreview() = MdcTheme { CatalogFilterMenu(viewModel = dummyViewModel) }

@Composable
fun CatalogFilterMenu(viewModel: ACatalogViewModel) {
	Column(
		modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
	) {
		CatalogFilterMenuMainContent(viewModel = viewModel)

		CatalogFilterMenuControlContent(viewModel = viewModel)
	}
}

@Composable
fun CatalogFilterMenuMainContent(viewModel: ACatalogViewModel) {
	val items by viewModel.filterItemsLive.observeAsState(initial = loading)

	Column(modifier = Modifier.fillMaxWidth()) {
		items.handle {
			CatalogFilterMenuFilterListContent(viewModel, it)
		}
	}
}

@Composable
fun CatalogFilterMenuFilterListContent(
	viewModel: ACatalogViewModel,
	list: List<Filter<*>>,
) {

	LazyColumn(
		modifier = Modifier.fillMaxWidth(),
	) {


		items(list) { filter ->
			when (filter) {
				is Filter.Header -> Column {
					Divider()
				}
				is Filter.Separator -> Divider()
				is Filter.Text -> CatalogFilterMenuTextContent(viewModel, filter)
				is Filter.Switch -> CatalogFilterMenuSwitchContent(viewModel, filter)
				is Filter.Checkbox -> CatalogFilterMenuCheckboxContent(viewModel, filter)
				is Filter.TriState -> CatalogFilterMenuTriStateContent(viewModel, filter)
				is Filter.Dropdown -> CatalogFilterMenuDropDownContent(viewModel, filter)
				is Filter.RadioGroup -> CatalogFilterMenuRadioGroupContent(viewModel, filter)
				is Filter.List -> {
					CatalogFilterMenuFilterListContent(
						viewModel,
						filter.filters.toList(),
						filter.name
					)
				}
				is Filter.Group<*> -> {
					CatalogFilterMenuFilterListContent(
						viewModel,
						filter.filters.toList(),
						filter.name
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
		dummyViewModel, listOf(
			Filter.Switch(7, "Switch"),
			Filter.Checkbox(8, "Checkbox"),
			Filter.TriState(9, "Tri state"),
		), "A list"
	)
}

@Composable
fun CatalogFilterMenuFilterListContent(
	viewModel: ACatalogViewModel,
	list: List<Filter<*>>,
	name: String,
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
						is Filter.Text -> CatalogFilterMenuTextContent(viewModel, filter)
						is Filter.Switch -> CatalogFilterMenuSwitchContent(viewModel, filter)
						is Filter.Checkbox -> CatalogFilterMenuCheckboxContent(viewModel, filter)
						is Filter.TriState -> CatalogFilterMenuTriStateContent(viewModel, filter)
						is Filter.Dropdown -> CatalogFilterMenuDropDownContent(viewModel, filter)
						is Filter.RadioGroup -> CatalogFilterMenuRadioGroupContent(
							viewModel,
							filter
						)
						is Filter.List -> {
							Log.e(
								"FilterListContent",
								"CatalogFilterMenuFilterListContent: Please avoid usage of lists in sub lists"
							)
							CatalogFilterMenuFilterListContent(
								viewModel,
								filter.filters.toList(),
								filter.name
							)
						}
						is Filter.Group<*> -> {
							Log.e(
								"FilterListContent",
								"CatalogFilterMenuFilterListContent: Please avoid usage of lists in sub lists"
							)
							CatalogFilterMenuFilterListContent(
								viewModel,
								filter.filters.toList(),
								filter.name
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
			viewModel = dummyViewModel,
			filter = Filter.Text(0, "This is a text input")
		)
	}

@Composable
fun CatalogFilterMenuTextContent(viewModel: ACatalogViewModel, filter: Filter.Text) {
	val text by viewModel.getFilterStringState(filter)
		.observeAsState(initial = "")

	TextField(
		modifier = Modifier.fillMaxWidth(),
		value = text,
		onValueChange = { viewModel.setFilterStringState(filter, it) },
		label = {
			Text(text = filter.name)
		}
	)
}

@Preview
@Composable
fun PreviewCatalogFilterMenuSwitchContent() = MdcTheme {
	CatalogFilterMenuSwitchContent(
		dummyViewModel, Filter.Switch(0, "Switch")
	)
}

@Composable
fun CatalogFilterMenuSwitchContent(viewModel: ACatalogViewModel, filter: Filter.Switch) {
	val state by viewModel.getFilterBooleanState(filter)
		.observeAsState(initial = false)

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(text = filter.name)
		Switch(
			checked = state,
			onCheckedChange = { viewModel.setFilterBooleanState(filter, it) }
		)
	}
}

@Preview
@Composable
fun PreviewCatalogFilterMenuCheckboxContent() = MdcTheme {
	CatalogFilterMenuCheckboxContent(dummyViewModel, Filter.Checkbox(0, "Checkbox"))
}

@Composable
fun CatalogFilterMenuCheckboxContent(viewModel: ACatalogViewModel, filter: Filter.Checkbox) {
	val state by viewModel.getFilterBooleanState(filter)
		.observeAsState(initial = false)

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(text = filter.name)
		Checkbox(
			checked = state,
			onCheckedChange = { viewModel.setFilterBooleanState(filter, it) }
		)
	}
}

@Preview
@Composable
fun PreviewCatalogFilterMenuTriStateContent() = MdcTheme {
	CatalogFilterMenuTriStateContent(dummyViewModel, Filter.TriState(0, "Tristate"))
}

@Composable
fun CatalogFilterMenuTriStateContent(viewModel: ACatalogViewModel, filter: Filter.TriState) {
	val triState by viewModel.getFilterIntState(filter)
		.map {
			when (it) {
				Filter.TriState.STATE_IGNORED -> ToggleableState.Indeterminate
				Filter.TriState.STATE_EXCLUDE -> ToggleableState.Off
				Filter.TriState.STATE_INCLUDE -> ToggleableState.On
				else -> ToggleableState.Off
			}
		}
		.observeAsState(initial = ToggleableState.Off)

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(text = filter.name)
		TriStateCheckbox(
			state = triState,
			onClick = {
				viewModel.setFilterIntState(
					filter,
					when (triState) {
						ToggleableState.On -> Filter.TriState.STATE_EXCLUDE
						ToggleableState.Off -> Filter.TriState.STATE_IGNORED
						ToggleableState.Indeterminate -> Filter.TriState.STATE_INCLUDE
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
		dummyViewModel,
		Filter.Dropdown(0, "Dropdown", arrayOf("A", "B", "C"))
	)
}

@Composable
fun CatalogFilterMenuDropDownContent(viewModel: ACatalogViewModel, filter: Filter.Dropdown) {
	val selection by viewModel.getFilterIntState(filter)
		.observeAsState(initial = 0)
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
			ClickableText(
				text = AnnotatedString(filter.choices[selection]),
			) {
				expanded = true
			}
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
					ClickableText(
						text = AnnotatedString(s),
						modifier = Modifier.padding(8.dp),
						onClick = {
							viewModel.setFilterIntState(filter, i)
							expanded = false
						})
				}
			}
		}
	}
}

@Preview
@Composable
fun PreviewCatalogFilterMenuRadioGroupContent() = MdcTheme {
	CatalogFilterMenuRadioGroupContent(
		dummyViewModel,
		Filter.RadioGroup(0, "Dropdown", arrayOf("A", "B", "C"))
	)
}

@Composable
fun CatalogFilterMenuRadioGroupContent(viewModel: ACatalogViewModel, filter: Filter.RadioGroup) {
	val selection by viewModel.getFilterIntState(filter)
		.observeAsState(initial = 0)
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
							onClick = { viewModel.setFilterIntState(filter, index) }
						)
					}
				}
			}
		}
	}
}

@Composable
fun CatalogFilterMenuControlContent(viewModel: ACatalogViewModel) {
	Card(
		modifier = Modifier
			.padding(8.dp)
			.fillMaxWidth()
	) {
		Row(
			horizontalArrangement = Arrangement.Center
		) {
			Button(onClick = { viewModel.resetFilter() }, modifier = Modifier.padding(8.dp)) {
				Text(text = stringResource(id = R.string.reset))
			}

			Button(onClick = { viewModel.applyFilter() }, modifier = Modifier.padding(8.dp)) {
				Text(text = stringResource(id = R.string.apply))
			}
		}
	}
}