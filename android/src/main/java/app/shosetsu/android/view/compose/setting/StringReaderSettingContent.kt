package app.shosetsu.android.view.compose.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.repositories.base.ISettingsRepository

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
@Composable
fun StringSettingContent(
	title: String,
	description: String,
	value: String,
	modifier: Modifier = Modifier,
	onValueChanged: (newString: String) -> Unit
) {
	Column(
		modifier = modifier,
	) {
		TextField(
			value = value,
			onValueChange = onValueChanged,
			label = { Text(title) }
		)
		Text(description)
	}
}

@Composable
fun StringSettingContent(
	title: String,
	description: String,
	repo: ISettingsRepository,
	key: SettingKey<String>,
	modifier: Modifier = Modifier,
) {
	val value by repo.getStringFlow(key).collectAsState(key.default)

	Column {
		TextField(
			value = value,
			onValueChange = {
				launchIO { repo.setString(key, it) }
			},
			modifier = modifier,
			label = { Text(title) }
		)
		Text(description)
	}
}

@Preview
@Composable
fun PreviewStringSettingContent() {
	StringSettingContent("Text Input", "This is a text input", "") {

	}
}