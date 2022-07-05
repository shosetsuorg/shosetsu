package app.shosetsu.android.ui.settings.sub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.SettingKey.*
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.launchUI
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.compose.ShosetsuCompose
import app.shosetsu.android.view.compose.setting.DropdownSettingContent
import app.shosetsu.android.view.compose.setting.NumberPickerSettingContent
import app.shosetsu.android.view.compose.setting.SwitchSettingContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.viewmodel.abstracted.settings.AViewSettingsViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.flow.map

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
 * @since 02 / 10 / 2021
 * @author Doomsdayrs
 */
class ViewSettings : ShosetsuController() {
	private val viewModel: AViewSettingsViewModel by viewModel()

	override val viewTitleRes: Int = R.string.settings_view

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedViewState: Bundle?
	): View = ComposeView(requireContext()).apply {
		setViewTitle()
		setContent {
			ShosetsuCompose {
				ViewSettingsContent(
					viewModel,
					finishActivity = {
						activity?.finish()
					}
				)
			}
		}
	}
}

@Composable
fun ViewSettingsContent(viewModel: AViewSettingsViewModel, finishActivity: () -> Unit) {
	var showUIAlert by remember { mutableStateOf(false) }

	@SuppressLint("FlowOperatorInvokedInComposition")
	val navStyle by viewModel.settingsRepo.getIntFlow(NavStyle).map { it == 1 }
		.collectAsState(NavStyle.default == 1)

	LazyColumn(
		contentPadding = PaddingValues(
			top = 16.dp,
			start = 16.dp,
			end = 16.dp,
			bottom = 64.dp
		)
	) {

		item {
			NumberPickerSettingContent(
				title = stringResource(R.string.columns_of_novel_listing_p),
				description = stringResource(R.string.columns_zero_automatic),
				range = 0..10,
				repo = viewModel.settingsRepo,
				key = ChapterColumnsInPortait,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp)
			)
		}

		item {
			NumberPickerSettingContent(
				title = stringResource(R.string.columns_of_novel_listing_h),
				description = stringResource(R.string.columns_zero_automatic),
				range = 0..10,
				repo = viewModel.settingsRepo,
				key = ChapterColumnsInLandscape,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp)
			)
		}

		item {
			DropdownSettingContent(
				title = stringResource(R.string.novel_card_type_selector_title),
				description = stringResource(R.string.novel_card_type_selector_desc),
				choices = stringArrayResource(R.array.novel_card_types),
				repo = viewModel.settingsRepo,
				key = SelectedNovelCardType,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp)
			)
		}

		item {
			SwitchSettingContent(
				title = stringResource(R.string.settings_view_legacy_nav_title),
				description = stringResource(R.string.settings_view_legacy_nav_desc),
				isChecked = navStyle,
				modifier = Modifier.fillMaxWidth()
			) {
				showUIAlert = true
			}
		}
	}

	if (showUIAlert)
		AlertDialog(
			onDismissRequest = {
				showUIAlert = false
			},
			confirmButton = {
				TextButton(
					onClick = {
						launchIO {
							viewModel.settingsRepo.setInt(NavStyle, if (navStyle) 0 else 1)
							launchUI {
								showUIAlert = false
								finishActivity()
							}
						}
					}
				) {
					Text(stringResource(R.string.restart))
				}
			},
			dismissButton = {
				TextButton(
					onClick = {
						showUIAlert = false
					}
				) {
					Text(stringResource(R.string.never_mind))
				}
			},
			title = {
				Text(stringResource(R.string.need_restart))
			}
		)
}










