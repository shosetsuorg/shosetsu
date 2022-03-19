package app.shosetsu.android.ui.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import app.shosetsu.android.common.ext.logV
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.launch

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
fun PreviewStringPageContent() {
	MdcTheme {
		StringPageContent(
			"la\nla\nla\nla\nla\nla\nla\nla\nla\nla\nla\nla\nla\nla\nla\nla\nla\nla\nla\n",
			0.0,
			16.0f,
			{},
			{},
			Color.Black.toArgb(),
			Color.White.toArgb()
		)
	}
}

@Composable
fun StringPageContent(
	content: String,
	progress: Double,
	textSize: Float,
	onScroll: (perc: Double) -> Unit,
	onFocusToggle: () -> Unit,
	textColor: Int,
	backgroundColor: Int
) {
	val state = rememberScrollState()

	if (state.isScrollInProgress)
		DisposableEffect(Unit) {
			onDispose {
				if (state.value != 0)
					onScroll((state.value.toDouble() / state.maxValue))
				else onScroll(0.0)
			}
		}

	SelectionContainer(
		modifier = Modifier.clickable(
			interactionSource = remember { MutableInteractionSource() },
			indication = null
		) {
			onFocusToggle()
		}
	) {
		Text(
			content,
			fontSize = textSize.sp,
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(state)
				.background(Color(backgroundColor)),
			color = Color(textColor)
		)
	}

	LaunchedEffect(Unit) {
		launch {
			logV("Scroll to")
			state.scrollTo((state.maxValue * progress).toInt())
		}
	}
}