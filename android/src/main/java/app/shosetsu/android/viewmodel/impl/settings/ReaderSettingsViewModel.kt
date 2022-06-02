package app.shosetsu.android.viewmodel.impl.settings

import android.app.Application
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.SettingKey.*
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.load.LoadReaderThemes
import app.shosetsu.android.view.compose.setting.FloatSliderSettingContent
import app.shosetsu.android.view.compose.setting.SliderSettingContent
import app.shosetsu.android.view.compose.setting.SwitchSettingContent
import app.shosetsu.android.view.uimodels.model.ColorChoiceUI
import app.shosetsu.android.viewmodel.abstracted.settings.AReaderSettingsViewModel
import app.shosetsu.android.viewmodel.base.ExposedSettingsRepoViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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
 * shosetsu
 * 31 / 08 / 2020
 */
class ReaderSettingsViewModel(
	iSettingsRepository: ISettingsRepository,
	private val app: Application,
	val loadReaderThemes: LoadReaderThemes
) : AReaderSettingsViewModel(iSettingsRepository) {

	override fun getReaderThemes(): Flow<List<ColorChoiceUI>> =
		loadReaderThemes().combine(settingsRepo.getIntFlow(ReaderTheme)) { a, b ->
			a.map { if (it.id == b.toLong()) it.copy(isSelected = true) else it }
		}.onIO()

}

@Composable
fun ExposedSettingsRepoViewModel.stringAsHtmlOption() {
	SwitchSettingContent(
		stringResource(R.string.settings_reader_title_string_to_html),
		stringResource(R.string.settings_reader_desc_string_to_html),
		settingsRepo,
		ReaderStringToHtml, modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}

@Composable
fun ExposedSettingsRepoViewModel.horizontalSwitchOption() {
	SwitchSettingContent(
		stringResource(R.string.settings_reader_title_horizontal_option),
		stringResource(R.string.settings_reader_desc_horizontal_option),
		settingsRepo,
		ReaderHorizontalPageSwap, modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}

@Composable
fun ExposedSettingsRepoViewModel.invertChapterSwipeOption() {
	SwitchSettingContent(
		stringResource(R.string.settings_reader_inverted_swipe_title),
		stringResource(R.string.settings_reader_inverted_swipe_desc),
		settingsRepo,
		ReaderIsInvertedSwipe, modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}

@Composable
fun ExposedSettingsRepoViewModel.showReaderDivider() {
	SwitchSettingContent(
		stringResource(R.string.settings_reader_show_divider),
		stringResource(R.string.settings_reader_show_divider_desc),
		settingsRepo,
		ReaderShowChapterDivider, modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}

@Composable
fun ExposedSettingsRepoViewModel.doubleTapFocus() {
	SwitchSettingContent(
		stringResource(R.string.settings_reader_double_tap),
		stringResource(R.string.settings_reader_double_tap_desc),
		settingsRepo,
		ReaderDoubleTapFocus, modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}

@Composable
fun ExposedSettingsRepoViewModel.doubleTapSystem() {
	SwitchSettingContent(
		stringResource(R.string.settings_reader_double_tap_system),
		stringResource(R.string.settings_reader_double_tap_system_desc),
		settingsRepo,
		ReaderDoubleTapSystem, modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}


@Composable
fun ExposedSettingsRepoViewModel.continuousScrollOption() {
	SwitchSettingContent(
		stringResource(R.string.settings_reader_title_continous_scroll),
		stringResource(R.string.settings_reader_desc_continous_scroll),
		settingsRepo,
		ReaderContinuousScroll, modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}

@Composable
fun ExposedSettingsRepoViewModel.tapToScrollOption() {
	SwitchSettingContent(
		stringResource(R.string.settings_reader_tap_to_scroll_title),
		"",
		settingsRepo,
		ReaderIsTapToScroll, modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}

@Composable
fun ExposedSettingsRepoViewModel.readerKeepScreenOnOption() {
	SwitchSettingContent(
		stringResource(R.string.settings_reader_keep_screen_on),
		stringResource(R.string.settings_reader_keep_screen_on_desc),
		settingsRepo,
		ReaderKeepScreenOn, modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}

@Composable
fun ExposedSettingsRepoViewModel.volumeScrollingOption() {
	SwitchSettingContent(
		stringResource(R.string.settings_reader_volume_scroll_title),
		"",
		settingsRepo,
		ReaderVolumeScroll, modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}

@Composable
fun ExposedSettingsRepoViewModel.textSizeOption() {
	FloatSliderSettingContent(
		stringResource(R.string.text_size),
		"",
		7..50,
		parseValue = { "$it" },
		settingsRepo,
		ReaderTextSize,
		haveSteps = false,
		flip = true,
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}

@Composable
fun ExposedSettingsRepoViewModel.paragraphIndentOption() {
	SliderSettingContent(
		stringResource(R.string.paragraph_indent),
		"",
		0..10,
		{ "$it" },
		settingsRepo,
		ReaderIndentSize, modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}

@Composable
fun ExposedSettingsRepoViewModel.paragraphSpacingOption() {
	FloatSliderSettingContent(
		stringResource(R.string.paragraph_spacing),
		"",
		0..10,
		{ "$it" },
		settingsRepo,
		ReaderParagraphSpacing,
		flip = true,
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp)
	)
}
