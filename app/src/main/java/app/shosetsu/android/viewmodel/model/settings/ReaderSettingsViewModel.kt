package app.shosetsu.android.viewmodel.model.settings

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.consts.settings.SettingKey
import app.shosetsu.android.common.consts.settings.SettingKey.*
import app.shosetsu.android.common.dto.handle
import app.shosetsu.android.common.dto.handledReturnAny
import app.shosetsu.android.common.dto.mapTo
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.setOnClickListener
import app.shosetsu.android.common.ext.setSelectionListener
import app.shosetsu.android.domain.model.local.ColorChoiceData
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.load.LoadReaderThemes
import app.shosetsu.android.view.uimodels.model.ColorChoiceUI
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AReaderSettingsViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.selectExtension

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
		private val loadReaderThemes: LoadReaderThemes
) : AReaderSettingsViewModel(iSettingsRepository) {
	override suspend fun settings(): List<SettingsItemData> = listOf(
			customSettingData(1) {
				title { "" }
				customView {
					context.getSystemService<LayoutInflater>()!!.inflate(
							R.layout.reader_theme_example,
							null,
							false
					).apply {
						findViewById<TextView>(R.id.textView).apply textView@{
							val exampleText =
									"Because there are so many lines. I had lost sense of time. Plz help" +
											"me escape this horror called" +
											"\nThis is some sample text. With lots of testing. Lots of paragraph," +
											"Lots of lines. Plenty to read"

							val function = { textView: TextView ->
								val replaceSpacing = StringBuilder("\n")
								//for (x in 0 until shosetsuSettings.readerParagraphSpacing)
								//	replaceSpacing.append("\n")
								//for (x in 0 until shosetsuSettings.readerIndentSize)
								//	replaceSpacing.append("\t")
								//textView.textSize = shosetsuSettings.readerTextSize

								//val r = shosetsuSettings.selectedReaderTheme.toLong()
								//val b = shosetsuSettings.getReaderBackgroundColor(r)
								//val t = shosetsuSettings.getReaderTextColor(r)
								//textView.setTextColor(t)
								//textView.setBackgroundColor(b)
								textView.text = exampleText.replace("\n".toRegex(), replaceSpacing.toString())
							}
							postDelayed(500) {
								//shosetsuSettings.apply {
								//	readerTextSizeLive.observe(this@ReaderSettings) {
								//		textSize = shosetsuSettings.readerTextSize
								//	}
								//	readerIndentSizeLive.observe(this@ReaderSettings) {
								//		function(this@textView)
								//	}
								//	readerParagraphSpacingLive.observe(this@ReaderSettings) {
								//		function(this@textView)
								//	}
								//	readerUserThemeSelectionLive.observe(this@ReaderSettings) {
								//		function(this@textView)
								//	}
								//}
							}
						}
					}
				}
			},
			spinnerSettingData(2) {
				title { R.string.paragraph_spacing }
				arrayAdapter = ArrayAdapter(
						context,
						android.R.layout.simple_spinner_dropdown_item,
						context.resources!!.getStringArray(R.array.sizes_with_none)
				)
				iSettingsRepository.getInt(ReaderParagraphSpacing).handle {
					spinnerValue { it }
				}
				onSpinnerItemSelected { _, _, position, _ ->
					launchIO {
						iSettingsRepository.setInt(ReaderParagraphSpacing, position)
					}
				}
			},
			spinnerSettingData(3) {
				title { R.string.text_size }
				arrayAdapter = ArrayAdapter(
						context,
						android.R.layout.simple_spinner_dropdown_item,
						context.resources!!.getStringArray(R.array.sizes_no_none)
				)
				iSettingsRepository.getFloat(ReaderTextSize).handle {
					spinnerValue {
						when (it) {
							14f -> 0
							17f -> 1
							20f -> 2
							else -> 0
						}
					}
				}
				onSpinnerItemSelected { adapterView, _, i, _ ->
					launchIO {
						if (i in 0..2) {
							var size = 14
							when (i) {
								0 -> {
								}
								1 -> size = 17
								2 -> size = 20
							}
							iSettingsRepository.setFloat(ReaderTextSize, size.toFloat())
							adapterView?.setSelection(i)
						}
					}
				}
			},
			spinnerSettingData(4) {
				title { R.string.paragraph_indent }
				arrayAdapter = ArrayAdapter(
						context,
						android.R.layout.simple_spinner_dropdown_item,
						context.resources!!.getStringArray(R.array.sizes_with_none)
				)
				iSettingsRepository.getInt(ReaderIndentSize).handle {
					spinnerValue { it }
				}
				onSpinnerItemSelected { _, _, position, _ ->
					launchIO {
						iSettingsRepository.setInt(ReaderIndentSize, position)
					}
				}
			},
			customBottomSettingData(5) {
				title { R.string.reader_theme }
				customView {
					context.getSystemService<LayoutInflater>()!!.inflate(
							R.layout.reader_theme_selection,
							null,
							false
					).apply {
						val recycler = findViewById<RecyclerView>(R.id.color_picker_options)
						val itemAdapter = ItemAdapter<ColorChoiceUI>()
						val fastAdapter = FastAdapter.with(itemAdapter)
						fastAdapter.selectExtension {
							isSelectable = true
							setSelectionListener { item, _ ->
								fastAdapter.notifyItemChanged(fastAdapter.getPosition(item))
							}
						}
						recycler.adapter = fastAdapter
						fastAdapter.setOnClickListener { _, _, item, _ ->
							launchIO {
								iSettingsRepository.setInt(ReaderTheme, item.identifier.toInt())
							}
							item.isSelected = true

							run {
								val count = fastAdapter.itemCount
								for (i in 0 until count)
									fastAdapter.getItem(i)?.takeIf {
										it.identifier != item.identifier
									}?.isSelected = false
							}

							fastAdapter.notifyDataSetChanged()
							true
						}

						loadReaderThemes().observe(lifecycleOwner) { list ->
							itemAdapter.clear()
							launchIO {
								val v = iSettingsRepository.getInt(ReaderTheme).handledReturnAny { it }!!
								list.find {
									it.identifier == v.toLong()
								}?.isSelected = true

								itemAdapter.add(list)
							}
						}
					}
				}
			},
			switchSettingData(6) {
				title { R.string.inverted_swipe }
				iSettingsRepository.getBoolean(ReaderIsInvertedSwipe).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(ReaderIsInvertedSwipe, isChecked)
					}
				}
			},
			switchSettingData(7) {
				title { R.string.tap_to_scroll }
				iSettingsRepository.getBoolean(ReaderIsTapToScroll).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(ReaderIsTapToScroll, isChecked)
					}
				}
			},
			switchSettingData(8) {
				title { "Resume first unread" }
				description {
					"Instead of resuming the first chapter that is not read(can be reading), " +
							"the app will open the first unread chapter"
				}
				iSettingsRepository.getBoolean(ChaptersResumeFirstUnread).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(ChaptersResumeFirstUnread, isChecked)
					}
				}
			},
	)
}