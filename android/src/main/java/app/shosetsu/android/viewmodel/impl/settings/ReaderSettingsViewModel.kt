package app.shosetsu.android.viewmodel.impl.settings

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
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.domain.repositories.base.getFloatOrDefault
import app.shosetsu.common.domain.repositories.base.getStringOrDefault
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.MarkingTypes
import app.shosetsu.common.enums.MarkingTypes.ONSCROLL
import app.shosetsu.common.enums.MarkingTypes.ONVIEW
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
	private val context: Context,
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
		spinnerSettingData(2) {
			title { R.string.paragraph_spacing }
			try {
				arrayAdapter = ArrayAdapter(
					context,
					android.R.layout.simple_spinner_dropdown_item,
					context.resources!!.getStringArray(R.array.sizes_with_none)
				)
			} catch (e: NotFoundException) {
				reportExceptionUseCase(e.toHError())
			}
			spinnerSettingValue(ReaderParagraphSpacing)
		},
		spinnerSettingData(3) {
			title { R.string.text_size }
			try {
				arrayAdapter = ArrayAdapter(
					context,
					android.R.layout.simple_spinner_dropdown_item,
					context.resources!!.getStringArray(R.array.sizes_no_none)
				)
			} catch (e: NotFoundException) {
				reportExceptionUseCase(e.toHError())
			}
			spinnerValue {
				when (settingsRepo.getFloatOrDefault(ReaderTextSize)) {
					14f -> 0
					17f -> 1
					20f -> 2
					else -> 0
				}
			}
			onSpinnerItemSelected { adapterView, _, i, _ ->
				if (i in 0..2) {
					var size = 14
					when (i) {
						0 -> {
						}
						1 -> size = 17
						2 -> size = 20
					}
					launchIO {
						settingsRepo.setFloat(ReaderTextSize, size.toFloat())
					}

					adapterView?.setSelection(i)
				}
			}
		},
		spinnerSettingData(4) {
			title { R.string.paragraph_indent }
			try {
				arrayAdapter = ArrayAdapter(
					context,
					android.R.layout.simple_spinner_dropdown_item,
					context.resources!!.getStringArray(R.array.sizes_with_none)
				)
			} catch (e: NotFoundException) {
				reportExceptionUseCase(e.toHError())
			}
			spinnerSettingValue(ReaderIndentSize)
		},
		customBottomSettingData(5) {
			title { R.string.reader_theme }
		},
		switchSettingData(6) {
			title { R.string.inverted_swipe }
			description { "Invert the chapter swipe" }
			checkSettingValue(ReaderIsInvertedSwipe)
		},
		switchSettingData(7) {
			title { R.string.tap_to_scroll }
			checkSettingValue(ReaderIsTapToScroll)
		},
		switchSettingData(6) {
			title { R.string.mark_read_as_reading }
			description { R.string.mark_read_as_reading_desc }
			checkSettingValue(ReaderMarkReadAsReading)
		},
		spinnerSettingData(0) {
			title { R.string.marking_mode }
			try {
				arrayAdapter = ArrayAdapter(
					context,
					android.R.layout.simple_spinner_dropdown_item,
					context.resources!!.getStringArray(R.array.marking_names)
				)
			} catch (e: NotFoundException) {
				reportExceptionUseCase.invoke(e.toHError())
			}
			spinnerValue {
				when (MarkingTypes.valueOf(settingsRepo.getStringOrDefault(ReadingMarkingType))) {
					ONSCROLL -> 1
					ONVIEW -> 0
				}
			}
			onSpinnerItemSelected { _, _, position, _ ->
				launchIO {
					when (position) {
						0 -> settingsRepo.setString(ReadingMarkingType, ONVIEW.name)
						1 -> settingsRepo.setString(ReadingMarkingType, ONSCROLL.name)
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
	)

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}
}