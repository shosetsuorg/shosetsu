package app.shosetsu.android.viewmodel.impl.settings

import android.app.Application
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.load.LoadReaderThemes
import app.shosetsu.android.view.uimodels.model.ColorChoiceUI
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AReaderSettingsViewModel
import app.shosetsu.android.viewmodel.base.ExposedSettingsRepoViewModel
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.domain.repositories.base.getStringOrDefault
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.MarkingType
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
	private val reportExceptionUseCase: ReportExceptionUseCase,
	val loadReaderThemes: LoadReaderThemes
) : AReaderSettingsViewModel(iSettingsRepository) {

	@ExperimentalCoroutinesApi
	override fun getReaderThemes(): LiveData<List<ColorChoiceUI>> =
		loadReaderThemes().asIOLiveData()

	override suspend fun settings(): List<SettingsItemData> = listOf(
		customSettingData(1) {
			title { "" }
		},
		paragraphSpacingOption(2),
		spinnerSettingData(9) {
			title { "Text Alignment" }
			try {
				arrayAdapter = ArrayAdapter(
					app.applicationContext,
					android.R.layout.simple_spinner_dropdown_item,
					app.applicationContext.resources!!.getStringArray(R.array.text_alignments)
				)
			} catch (e: NotFoundException) {
				reportExceptionUseCase(e.toHError())
			}
			spinnerSettingValue(ReaderTextAlignment)
		},
		textSizeOption(3),
		paragraphIndentOption(4, app.applicationContext),
		customBottomSettingData(5) {
			title { R.string.reader_theme }
		},
		invertChapterSwipeOption(6),
		tapToScrollOption(7),
		volumeScrollingOption(13),
		switchSettingData(8) {
			title { R.string.settings_reader_title_mark_read_as_reading }
			description { R.string.settings_reader_desc_mark_read_as_reading }
			checkSettingValue(ReaderMarkReadAsReading)
		},
		horizontalSwitchOption(9),
		textInputSettingData(10) {
			title { R.string.settings_reader_title_html_css }
			description { R.string.settings_reader_desc_html_css }
			textSettingValue(ReaderHtmlCss)
		},
		stringAsHtmlOption(11),
		continuousScrollOption(12),
		spinnerSettingData(0) {
			title { R.string.marking_mode }
			try {
				arrayAdapter = ArrayAdapter(
					app.applicationContext,
					android.R.layout.simple_spinner_dropdown_item,
					app.applicationContext.resources!!.getStringArray(R.array.marking_names)
				)
			} catch (e: NotFoundException) {
				reportExceptionUseCase.invoke(e.toHError())
			}
			spinnerValue {
				when (MarkingType.valueOf(settingsRepo.getStringOrDefault(ReadingMarkingType))) {
					MarkingType.ONSCROLL -> 1
					MarkingType.ONVIEW -> 0
				}
			}
			onSpinnerItemSelected { _, _, position, _ ->
				launchIO {
					when (position) {
						0 -> settingsRepo.setString(ReadingMarkingType, MarkingType.ONVIEW.name)
						1 -> settingsRepo.setString(ReadingMarkingType, MarkingType.ONSCROLL.name)
						else -> Log.e("MarkingMode", "UnknownType")
					}
				}
			}
		},

		switchSettingData(8) {
			title { "Resume first unread" }
			description {
				"Instead of resuming the first chapter reading/unread, " +
						"the app will open the first unread chapter"
			}
			checkSettingValue(ChaptersResumeFirstUnread)
		},
		readerKeepScreenOnOption(14)
	)

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}
}

suspend fun ExposedSettingsRepoViewModel.stringAsHtmlOption(id: Int) =
	switchSettingData(id) {
		titleRes = R.string.settings_reader_title_string_to_html
		description { R.string.settings_reader_desc_string_to_html }
		checkSettingValue(ReaderStringToHtml)
	}

suspend fun ExposedSettingsRepoViewModel.horizontalSwitchOption(id: Int) =
	switchSettingData(id) {
		titleRes = R.string.settings_reader_title_horizontal_option
		description { R.string.settings_reader_desc_horizontal_option }
		checkSettingValue(ReaderHorizontalPageSwap)
	}

suspend fun ExposedSettingsRepoViewModel.invertChapterSwipeOption(id: Int) =
	switchSettingData(id) {
		titleRes = R.string.settings_reader_inverted_swipe_title

		description { R.string.settings_reader_inverted_swipe_desc }
		checkSettingValue(ReaderIsInvertedSwipe)
	}

suspend fun ExposedSettingsRepoViewModel.continuousScrollOption(id: Int) =
	switchSettingData(id) {
		titleRes = R.string.settings_reader_title_continous_scroll
		description { R.string.settings_reader_desc_continous_scroll }
		checkSettingValue(ReaderContinuousScroll)
	}

suspend fun ExposedSettingsRepoViewModel.tapToScrollOption(id: Int) =
	switchSettingData(id) {
		titleRes = R.string.tap_to_scroll
		checkSettingValue(ReaderIsTapToScroll)
	}

suspend fun ExposedSettingsRepoViewModel.readerKeepScreenOnOption(id: Int) =
	switchSettingData(id) {
		titleRes = R.string.settings_reader_keep_screen_on
		description { R.string.settings_reader_keep_screen_on_desc }
		checkSettingValue(ReaderKeepScreenOn)
	}

suspend fun ExposedSettingsRepoViewModel.volumeScrollingOption(id: Int) =
	switchSettingData(id) {
		titleRes = R.string.volume_scroll
		checkSettingValue(ReaderVolumeScroll)
	}

suspend fun ExposedSettingsRepoViewModel.textSizeOption(id: Int) =
	floatButtonSettingData(id) {
		titleRes = R.string.text_size
		minWhole = 7
		maxWhole = 50
		settingValue(ReaderTextSize)
	}

suspend fun ExposedSettingsRepoViewModel.paragraphIndentOption(id: Int, context: Context) =
	spinnerSettingData(id) {
		titleRes = R.string.paragraph_indent
		arrayAdapter = ArrayAdapter(
			context,
			android.R.layout.simple_spinner_dropdown_item,
			context.resources!!.getStringArray(R.array.sizes_with_none)
		)
		spinnerSettingValue(ReaderIndentSize)
	}

suspend fun ExposedSettingsRepoViewModel.paragraphSpacingOption(id: Int) =
	floatButtonSettingData(id) {
		titleRes = R.string.paragraph_spacing
		minWhole = 0

		settingValue(ReaderParagraphSpacing)
	}
