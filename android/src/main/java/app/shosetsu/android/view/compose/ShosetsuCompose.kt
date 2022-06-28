package app.shosetsu.android.view.compose

import android.content.Context
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
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
 * To provide unified connection
 *
 * @since 28 / 06 / 2022
 * @author Doomsdayrs
 */
@Composable
fun ShosetsuCompose(
	context: Context = LocalContext.current,
	content: @Composable () -> Unit
) {
	MdcTheme(
		context = context
	) {
		Surface(
			modifier = Modifier.nestedScroll(rememberViewInteropNestedScrollConnection()),
			content = content
		)
	}
}