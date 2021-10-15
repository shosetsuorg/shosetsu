package app.shosetsu.android.view.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
fun PreviewEmptyDataContent() {
	EmptyDataContent(R.string.todo, EmptyDataView.Action(R.string.todo) {})
}

@Composable
fun EmptyDataContent(@StringRes textRes: Int, vararg actions: EmptyDataView.Action) {
	Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Text(EmptyDataView.getRandomErrorFace(), fontSize = 48.sp)
			Text(stringResource(textRes), fontSize = 16.sp, textAlign = TextAlign.Center)
			LazyRow {
				items(actions) {
					TextButton(onClick = { it.listener.onClick(null) }) {
						Text(stringResource(it.resId))
					}
				}
			}
		}
	}
}