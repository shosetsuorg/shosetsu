package app.shosetsu.android.ui.settings.sub

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.getSystemService
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.settings.SettingsSubController
import app.shosetsu.android.view.uimodels.model.ColorChoiceUI
import app.shosetsu.android.view.uimodels.settings.CustomBottomSettingData
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.customView
import app.shosetsu.android.viewmodel.abstracted.settings.AReaderSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.dto.transmogrify
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
 * 28 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 */
@SuppressLint("LogConditional")
class ReaderSettings : SettingsSubController() {
	override val viewTitleRes: Int = R.string.settings_reader
	override val viewModel: AReaderSettingsViewModel by viewModel()

	override val adjustments: List<SettingsItemData>.() -> Unit = {
		find<CustomBottomSettingData>(5)?.customView { root ->
			root.context.getSystemService<LayoutInflater>()!!.inflate(
				R.layout.reader_theme_selection,
				root,
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
						viewModel.settingsRepo.setInt(
							SettingKey.ReaderTheme,
							item.identifier.toInt()
						)
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

				viewModel.getReaderThemes().observe(this@ReaderSettings) { list ->
					itemAdapter.clear()
					launchIO {
						val v = viewModel.settingsRepo.getInt(SettingKey.ReaderTheme)
							.transmogrify { it }!!
						list.find {
							it.identifier == v.toLong()
						}?.isSelected = true

						launchUI { itemAdapter.add(list) }
					}
				}
			}
		}
		find<CustomBottomSettingData>(1)?.customView {
			it.context.getSystemService<LayoutInflater>()!!.inflate(
				R.layout.reader_theme_example,
				null,
				false
			).apply {
				findViewById<AppCompatTextView>(R.id.textView).apply textView@{
					val exampleText =
						"Because there are so many lines. I had lost sense of time. Plz help" +
								"me escape this horror called" +
								"\nThis is some sample text. With lots of testing. Lots of paragraph," +
								"Lots of lines. Plenty to read"

					val function = { textView: AppCompatTextView ->
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
						textView.text =
							exampleText.replace("\n".toRegex(), replaceSpacing.toString())
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
	}
}