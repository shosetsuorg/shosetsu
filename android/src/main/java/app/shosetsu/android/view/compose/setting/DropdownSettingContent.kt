package app.shosetsu.android.view.compose.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import com.github.doomsdayrs.apps.shosetsu.R

@Composable
fun DropdownSettingContent(
	title: String,
	description: String,
	choices: Array<String>,
	modifier: Modifier = Modifier,
	repo: ISettingsRepository,
	key: SettingKey<Int>
) {
	val choice by repo.getIntFlow(key).collectAsState(key.default)

	DropdownSettingContent(title, description, choice, choices, modifier) {
		launchIO { repo.setInt(key, it) }
	}

}

@Composable
fun DropdownSettingContent(
	title: String,
	description: String,
	choices: Array<String>,
	modifier: Modifier = Modifier,
	repo: ISettingsRepository,
	key: SettingKey<String>,
	stringToInt: (value: String) -> Int,
	intToString: (value: Int) -> String
) {
	val choice by repo.getStringFlow(key)
		.collectAsState(key.default)

	DropdownSettingContent(title, description, stringToInt(choice), choices, modifier) {
		launchIO { repo.setString(key, intToString(it)) }
	}

}

@Composable
fun DropdownSettingContent(
	title: String,
	description: String,
	selection: Int,
	choices: Array<String>,
	modifier: Modifier = Modifier,
	onSelection: (newValue: Int) -> Unit
) {
	var expanded by remember { mutableStateOf(false) }

	GenericRightSettingLayout(title, description, modifier, onClick = { expanded = !expanded }) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = AnnotatedString(choices[selection]),
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
				choices.forEachIndexed { index, s ->
					DropdownMenuItem(
						onClick = {
							onSelection(index)
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