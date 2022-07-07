package app.shosetsu.android.ui.settings.sub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.shosetsu.android.common.SettingKey.*
import app.shosetsu.android.common.consts.SELECTED_STROKE_WIDTH
import app.shosetsu.android.common.enums.MarkingType
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.makeSnackBar
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.css.CSSEditorActivity
import app.shosetsu.android.view.compose.ShosetsuCompose
import app.shosetsu.android.view.compose.setting.ButtonSettingContent
import app.shosetsu.android.view.compose.setting.DropdownSettingContent
import app.shosetsu.android.view.compose.setting.GenericBottomSettingLayout
import app.shosetsu.android.view.compose.setting.SwitchSettingContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.viewmodel.abstracted.settings.AReaderSettingsViewModel
import app.shosetsu.android.viewmodel.impl.settings.*
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
 * @since 04 / 10 / 2021
 * @author Doomsdayrs
 */
class ReaderSettings : ShosetsuController() {
	private val viewModel: AReaderSettingsViewModel by viewModel()

	override val viewTitleRes: Int = R.string.settings_reader

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedViewState: Bundle?
	): View = ComposeView(requireContext()).apply {
		setViewTitle()
		setContent {
			ShosetsuCompose {
				ReaderSettingsContent(
					viewModel,
					openHTMLEditor = {
						startActivity(
							Intent(activity, CSSEditorActivity::class.java).apply {
								putExtra(CSSEditorActivity.CSS_ID, -1)
							}
						)
					},
					showStyleAddSnackBar = {
						makeSnackBar(R.string.style_wait)?.show()
					}
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReaderSettingsContent(
	viewModel: AReaderSettingsViewModel,
	openHTMLEditor: () -> Unit,
	showStyleAddSnackBar: () -> Unit
) {

	LazyColumn(
		contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 64.dp)
	) {
		//TODO Text Preview at top

		item {
			viewModel.paragraphSpacingOption()
		}

		item {
			DropdownSettingContent(
				title = stringResource(R.string.settings_reader_text_alignment_title),
				description = stringResource(R.string.settings_reader_text_alignment_desc),
				choices = stringArrayResource(R.array.text_alignments),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				repo = viewModel.settingsRepo,
				ReaderTextAlignment
			)
		}

		item {
			viewModel.textSizeOption()
		}

		item {
			viewModel.paragraphIndentOption()
		}

		item {
			GenericBottomSettingLayout(
				stringResource(R.string.theme),
				""
			) {
				val themes by viewModel.getReaderThemes().collectAsState(listOf())

				LazyRow(
					contentPadding = PaddingValues(16.dp)
				) {
					items(themes, key = { it.id }) { themeItem ->
						Card(
							modifier = Modifier.padding(8.dp),
							border = if (themeItem.isSelected) BorderStroke(
								SELECTED_STROKE_WIDTH.dp,
								MaterialTheme.colors.secondary
							) else null,
							onClick = {
								launchIO {
									viewModel.settingsRepo.setInt(
										ReaderTheme,
										themeItem.id.toInt()
									)
								}
							}
						) {
							Box(
								modifier = Modifier.background(Color(themeItem.backgroundColor)),
								contentAlignment = Alignment.Center
							) {
								Text(
									"T",
									color = Color(themeItem.textColor),
									modifier = Modifier
										.size(64.dp)
										.padding(8.dp),
									textAlign = TextAlign.Center,
									fontSize = 32.sp
								)
							}
						}
					}

					item {
						Card(
							modifier = Modifier.padding(8.dp),
							onClick = {
								showStyleAddSnackBar()
							}
						) {
							Box(
								contentAlignment = Alignment.Center
							) {
								Image(
									painterResource(R.drawable.add_circle_outline),
									stringResource(R.string.style_add),
									modifier = Modifier
										.size(64.dp)
										.padding(8.dp)
								)
							}

						}
					}
				}
			}
		}


		item {
			viewModel.invertChapterSwipeOption()
		}

		//item { viewModel.tapToScrollOption() }

		item {
			viewModel.volumeScrollingOption()
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_title_mark_read_as_reading),
				stringResource(R.string.settings_reader_desc_mark_read_as_reading),
				viewModel.settingsRepo,
				ReaderMarkReadAsReading,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp)
			)
		}

		//item { viewModel.horizontalSwitchOption() }

		item {
			ButtonSettingContent(
				stringResource(R.string.settings_reader_title_html_css),
				stringResource(R.string.settings_reader_desc_html_css),
				stringResource(R.string.open_in),
				onClick = openHTMLEditor,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp)
			)
		}

		item {
			viewModel.stringAsHtmlOption()
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_title_continous_scroll),
				stringResource(R.string.settings_reader_desc_continous_scroll),
				viewModel.settingsRepo,
				ReaderContinuousScroll,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp)
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
				},
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp)
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_resume_behavior_title),
				stringResource(R.string.settings_reader_resume_behavior_desc),
				viewModel.settingsRepo,
				ChaptersResumeFirstUnread,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp)
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_reader_keep_screen_on),
				stringResource(R.string.settings_reader_keep_screen_on_desc),
				viewModel.settingsRepo,
				ReaderKeepScreenOn,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp)
			)
		}

		item {
			viewModel.showReaderDivider()
		}

		item { viewModel.doubleTapFocus() }
		item { viewModel.doubleTapSystem() }
	}

}