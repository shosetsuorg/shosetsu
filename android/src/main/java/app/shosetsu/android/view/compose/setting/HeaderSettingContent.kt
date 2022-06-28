package app.shosetsu.android.view.compose.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.shosetsu.android.view.compose.ShosetsuCompose

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
 * @since 03 / 03 / 2022
 * @author Doomsdayrs
 */


@Preview
@Composable
fun PreviewHeaderSettingContent() {
	ShosetsuCompose {
		HeaderSettingContent("Test", modifier = Modifier.padding(16.dp))
	}
}


@Composable
fun HeaderSettingContent(
	name: String,
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		Text(
			name,
			style = MaterialTheme.typography.h6,
			modifier = Modifier.padding(bottom = 8.dp),
			color = MaterialTheme.colors.primary
		)
		Divider(modifier = Modifier.fillMaxWidth())
	}
}