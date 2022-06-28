package app.shosetsu.android.view.compose.setting

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
 */

/**
 * Shosetsu
 *
 * @since 14 / 11 / 2021
 * @author Doomsdayrs
 */
@Composable
fun ButtonSettingContent(
	title: String,
	description: String,
	buttonText: String,
	modifier: Modifier = Modifier,
	onClick: () -> Unit
) {
	GenericRightSettingLayout(title, description, modifier) {
		Button(
			onClick
		) {
			Text(buttonText, textAlign = TextAlign.Center)
		}
	}
}

@Preview
@Composable
fun PreviewButtonSettingContent() {
	ShosetsuCompose {
		ButtonSettingContent(
			"This is a button",
			"Press me now now now now now",
			"Button",
			modifier = Modifier
				.width(300.dp)
				.height(75.dp)
		) {
		}
	}
}