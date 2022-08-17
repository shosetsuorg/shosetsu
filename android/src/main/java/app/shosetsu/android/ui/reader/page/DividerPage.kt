package app.shosetsu.android.ui.reader.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.shosetsu.android.R
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
 * @since 18 / 03 / 2022
 * @author Doomsdayrs
 */
@Preview
@Composable
fun PreviewDividerPageContent() {
	ShosetsuCompose {
		DividierPageContent(
			"The first",
			"The second"
		)
	}
}

@Composable
fun DividierPageContent(
	previous: String,
	next: String?
) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(Color.Black),
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)
		) {

			if (next != null) {
				Text(stringResource(R.string.reader_last_chapter), color = Color.White)
				Text(previous, color = Color.White)
				Text(stringResource(R.string.reader_next_chapter), color = Color.White)
				Text(next, color = Color.White)
			} else {
				Text(stringResource(R.string.no_more_chapters), color = Color.White)
			}
		}
	}
}