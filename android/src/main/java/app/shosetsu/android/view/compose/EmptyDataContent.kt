package app.shosetsu.android.view.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.shosetsu.android.view.widget.EmptyDataView
import com.github.doomsdayrs.apps.shosetsu.R

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

@Preview
@Composable
fun PreviewErrorContent() {
	ErrorContent(R.string.todo, EmptyDataView.Action(R.string.todo) {})
}

@Composable
fun ErrorContent(@StringRes messageRes: Int, vararg actions: EmptyDataView.Action) =
	ErrorContent(stringResource(messageRes), *actions)

@Composable
fun ErrorContent(message: String, vararg actions: EmptyDataView.Action) {
	Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Text(
				EmptyDataView.getRandomErrorFace(),
				fontSize = 48.sp,
				modifier = Modifier.padding(bottom = 16.dp)
			)
			Text(
				message,
				fontSize = 16.sp,
				textAlign = TextAlign.Center,
				modifier = Modifier.padding(bottom = 8.dp)
			)
			LazyRow {
				items(actions) {
					TextButton(
						onClick = { it.listener.onClick(null) },
						contentPadding = PaddingValues(16.dp)
					) {
						Text(stringResource(it.resId))
					}
				}
			}
		}
	}
}