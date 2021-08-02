package app.shosetsu.android.ui.browse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.shosetsu.android.viewmodel.abstracted.ABrowseViewModel
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme

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
 * @since 30 / 07 / 2021
 * @author Doomsdayrs
 */

@Composable
fun BrowseControllerFilterMenu(viewModel: ABrowseViewModel) {
	BrowseControllerLanguagesFilter(viewModel)
}

@Composable
fun BrowseControllerLanguagesFilter(viewModel: ABrowseViewModel) {
	val languageList by viewModel.filteredLanguagesLive.observeAsState(initial = loading)
	val showOnlyInstalled by viewModel.onlyInstalledLive.observeAsState(initial = false)

	var hidden by remember { mutableStateOf(false) }

	Column(modifier = Modifier.fillMaxSize()) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = stringResource(R.string.languages),
				Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
			)

			IconToggleButton(
				onCheckedChange = {
					hidden = it
				},
				checked = hidden
			) {
				if (hidden)
					Icon(painterResource(R.drawable.expand_more), "")
				else
					Icon(painterResource(R.drawable.expand_less), "")
			}
		}
		Divider()
		if (!hidden)
			languageList.handle { (languages, state) ->
				BrowseControllerLanguagesContent(
					languages = languages,
					state = state,
					onLanguageChecked = { language, newState ->
						viewModel.setLanguageFiltered(language, newState)
					}
				)
			}


		BrowseControllerInstalledFilter(
			state = showOnlyInstalled,
			updateState = { viewModel.showOnlyInstalled(it) })

	}
}

@Preview
@Composable
fun BrowseControllerLanguagesPreview() {
	MdcTheme {
		BrowseControllerLanguagesContent(
			languages = listOf("en", "ch", "ru", "fr"),
			state = mapOf("en" to false, "ch" to false, "ru" to true, "fr" to false),
			onLanguageChecked = { a, b -> })
	}
}

@Composable
fun BrowseControllerLanguagesContent(
	languages: List<String>,
	state: Map<String, Boolean>,
	onLanguageChecked: (String, Boolean) -> Unit
) {
	LazyColumn(
		modifier = Modifier.fillMaxWidth(),
		contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp)
	) {
		items(languages) { language ->
			BrowseControllerLanguageItem(language, state[language] ?: false, onLanguageChecked)
		}

		item { Divider() }
	}
}

@Composable
fun BrowseControllerLanguageItem(
	language: String,
	state: Boolean,
	onLanguageChecked: (String, Boolean) -> Unit
) {
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = Modifier
			.fillMaxWidth()
	) {
		Text(
			text = language,
			modifier = Modifier.alignByBaseline()
		)
		Checkbox(
			checked = state,
			onCheckedChange = {
				onLanguageChecked(language, it)
			},
			modifier = Modifier
				.alignByBaseline()
				.padding(bottom = 8.dp)
		)
	}

}


@Composable
fun BrowseControllerInstalledFilter(state: Boolean, updateState: (Boolean) -> Unit) {
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 16.dp, end = 16.dp)
	) {
		Text(
			text = stringResource(R.string.controller_browse_filter_only_installed),
			modifier = Modifier.alignByBaseline()
		)
		Checkbox(
			checked = state,
			onCheckedChange = {
				updateState(it)
			},
			modifier = Modifier
				.alignByBaseline()
				.padding(bottom = 8.dp)
		)
	}
}