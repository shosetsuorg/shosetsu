package app.shosetsu.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.ext.shosetsuPush
import app.shosetsu.android.ui.settings.sub.*
import app.shosetsu.android.view.compose.ShosetsuCompose
import app.shosetsu.android.view.controller.ShosetsuController
import com.bluelinelabs.conductor.Controller
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

/**
 * Shosetsu
 *
 * @since 06 / 10 / 2021
 * @author Doomsdayrs
 */
class SettingsController : ShosetsuController() {

	override val viewTitleRes: Int = R.string.settings

	override fun onViewCreated(view: View) {}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setViewTitle()
		setContent {
			ShosetsuCompose {
				SettingsContent { router.shosetsuPush(it) }
			}
		}
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingMenuItem(@StringRes title: Int, @DrawableRes drawableRes: Int, onClick: () -> Unit) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		elevation = 0.dp,
		onClick = onClick,
		backgroundColor = colorResource(android.R.color.transparent),
		shape = RectangleShape
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				painterResource(drawableRes),
				null,
				modifier = Modifier
					.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 24.dp)
					.size(24.dp),
				tint = MaterialTheme.colors.primary
			)
			Text(stringResource(title))
		}
	}
}

@Composable
fun SettingsContent(pushController: (Controller) -> Unit) {
	Column {
		SettingMenuItem(R.string.view, R.drawable.view_module) {
			pushController(ViewSettings())
		}

		SettingMenuItem(R.string.reader, R.drawable.book) {
			pushController(ReaderSettings())
		}

		SettingMenuItem(R.string.download, R.drawable.download) {
			pushController(DownloadSettings())
		}

		SettingMenuItem(R.string.update, R.drawable.update) {
			pushController(UpdateSettings())
		}

		SettingMenuItem(R.string.advanced, R.drawable.settings) {
			pushController(AdvancedSettings())
		}
	}
}