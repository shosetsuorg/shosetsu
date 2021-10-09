package app.shosetsu.android.ui.settings.sub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.compose.setting.DropdownSettingContent
import app.shosetsu.android.view.compose.setting.StringSettingContent
import app.shosetsu.android.view.compose.setting.SwitchSettingContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.viewmodel.abstracted.settings.AReaderSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.enums.MarkingType
import com.github.doomsdayrs.apps.shosetsu.R
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
 * Shosetsu
 *
 * @since 04 / 10 / 2021
 * @author Doomsdayrs
 */
class ComposeReaderSettings : ShosetsuController() {
	private val viewModel: AReaderSettingsViewModel by viewModel()

	override val viewTitleRes: Int = R.string.settings_reader

	override fun onViewCreated(view: View) {}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setViewTitle()
		setContent {
			MdcTheme {
				ReaderSettingsContent(
					viewModel,
				)
			}
		}
	}
}

@Composable
fun ReaderSettingsContent(viewModel: AReaderSettingsViewModel) {
	LazyColumn(modifier = Modifier.padding(16.dp)) {
		//TODO Text Preview at top

		// TODO FloatInput 4 paragraph spacing

		item {
			DropdownSettingContent(
				title = stringResource(R.string.settings_reader_text_alignment_title),
				description = stringResource(R.string.settings_reader_text_alignment_desc),
				choices = stringArrayResource(R.array.text_alignments),
				modifier = Modifier.fillMaxWidth(),
				repo = viewModel.settingsRepo,
				ReaderTextAlignment
			)
		}

		// TODO FloatInput 4 text size
		// TODO FloatInput 4 paragraph indent

		// TODO Reader Theme

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_inverted_swipe_title),
				stringResource(R.string.settings_reader_inverted_swipe_desc),
				viewModel.settingsRepo,
				ReaderIsInvertedSwipe,
				modifier = Modifier.fillMaxWidth()
			)
		}
		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_tap_to_scroll_title),
				stringResource(R.string.settings_reader_tap_to_scroll_desc),
				viewModel.settingsRepo,
				ReaderIsTapToScroll,
				modifier = Modifier.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_volume_scroll_title),
				stringResource(R.string.settings_reader_volume_scroll_desc),
				viewModel.settingsRepo,
				ReaderVolumeScroll,
				modifier = Modifier.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_title_mark_read_as_reading),
				stringResource(R.string.settings_reader_desc_mark_read_as_reading),
				viewModel.settingsRepo,
				ReaderMarkReadAsReading,
				modifier = Modifier.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_title_horizontal_option),
				stringResource(R.string.settings_reader_desc_horizontal_option),
				viewModel.settingsRepo,
				ReaderHorizontalPageSwap,
				modifier = Modifier.fillMaxWidth()
			)
		}

		item {
			StringSettingContent(
				stringResource(R.string.settings_reader_title_html_css),
				stringResource(R.string.settings_reader_desc_html_css),
				viewModel.settingsRepo,
				ReaderHtmlCss,
				modifier = Modifier.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_title_string_to_html),
				stringResource(R.string.settings_reader_desc_string_to_html),
				viewModel.settingsRepo,
				ReaderStringToHtml,
				modifier = Modifier.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_title_string_to_html),
				stringResource(R.string.settings_reader_desc_string_to_html),
				viewModel.settingsRepo,
				ReaderContinuousScroll,
				modifier = Modifier.fillMaxWidth()
			)
		}

		item {
			DropdownSettingContent(
				stringResource(R.string.marking_mode),
				stringResource(R.string.settings_reader_marking_mode_desc),
				choices = stringArrayResource(R.array.marking_names),
				repo = viewModel.settingsRepo,
				key = ReadingMarkingType,
				stringToInt = {
					when (MarkingType.valueOf(it)) {
						MarkingType.ONSCROLL -> 1
						MarkingType.ONVIEW -> 0
					}
				},
				intToString = {
					when (it) {
						0 -> MarkingType.ONVIEW.name
						1 -> MarkingType.ONSCROLL.name
						else -> {
							Log.e("MarkingMode", "UnknownType, defaulting")
							MarkingType.ONVIEW.name
						}
					}
				}
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_resume_behavior_title),
				stringResource(R.string.settings_reader_resume_behavior_desc),
				viewModel.settingsRepo,
				ChaptersResumeFirstUnread,
				modifier = Modifier.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_keep_screen_on),
				stringResource(R.string.settings_reader_keep_screen_on_desc),
				viewModel.settingsRepo,
				ReaderKeepScreenOn,
				modifier = Modifier.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_show_divider),
				stringResource(R.string.settings_reader_show_divider_desc),
				viewModel.settingsRepo,
				ReaderShowChapterDivider,
				modifier = Modifier.fillMaxWidth()
			)
		}

	}
}