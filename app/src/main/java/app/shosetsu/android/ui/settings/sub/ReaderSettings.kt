package app.shosetsu.android.ui.settings.sub

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.ShosetsuSettings
import app.shosetsu.android.common.ShosetsuSettings.ColorChoice
import app.shosetsu.android.common.ext.context
import app.shosetsu.android.common.ext.setOnClickListener
import app.shosetsu.android.common.ext.setSelectionListener
import app.shosetsu.android.ui.settings.SettingsSubController
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.selectExtension
import org.kodein.di.generic.instance
import java.util.*

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
	private val shosetsuSettings: ShosetsuSettings by instance()
	override val viewTitleRes: Int = R.string.settings_reader


	override val settings: ArrayList<SettingsItemData> by settingsList {
		customSettingData(4) {
			title { "" }
			customView {
				activity!!.getSystemService<LayoutInflater>()!!.inflate(
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
							for (x in 0 until shosetsuSettings.readerParagraphSpacing)
								replaceSpacing.append("\n")
							for (x in 0 until shosetsuSettings.readerIndentSize)
								replaceSpacing.append("\t")
							textView.textSize = shosetsuSettings.readerTextSize

							val r = shosetsuSettings.readerTheme.toLong()
							val b = shosetsuSettings.getReaderBackgroundColor(r)
							val t = shosetsuSettings.getReaderTextColor(r)
							textView.setTextColor(t)
							textView.setBackgroundColor(b)
							textView.text = exampleText.replace("\n".toRegex(), replaceSpacing.toString())
						}

						postDelayed(500) {
							shosetsuSettings.apply {
								readerTextSizeLive.observe(this@ReaderSettings) {
									textSize = shosetsuSettings.readerTextSize
								}
								readerIndentSizeLive.observe(this@ReaderSettings) {
									function(this@textView)
								}
								readerParagraphSpacingLive.observe(this@ReaderSettings) {
									function(this@textView)
								}
								readerUserThemeSelectionLive.observe(this@ReaderSettings) {
									function(this@textView)
								}
							}
						}
					}
				}
			}
		}

		spinnerSettingData(0) {
			title { R.string.paragraph_spacing }
			arrayAdapter = ArrayAdapter(
					context!!,
					android.R.layout.simple_spinner_dropdown_item,
					resources!!.getStringArray(R.array.sizes_with_none)
			)
			spinnerField { shosetsuSettings::readerParagraphSpacing }
		}
		spinnerSettingData(1) {
			title { R.string.text_size }
			spinnerSelection = when (shosetsuSettings.readerTextSize.toInt()) {
				14 -> 0
				17 -> 1
				20 -> 2
				else -> 0
			}
			arrayAdapter = ArrayAdapter(
					context!!,
					android.R.layout.simple_spinner_dropdown_item,
					resources!!.getStringArray(R.array.sizes_no_none)
			)
			onSpinnerItemSelected { adapterView, _, i, _ ->
				Log.d("TextSizeSelection", i.toString())
				if (i in 0..2) {
					var size = 14
					when (i) {
						0 -> {
						}
						1 -> size = 17
						2 -> size = 20
					}
					shosetsuSettings.readerTextSize = (size.toFloat())
					adapterView?.setSelection(i)
				}
			}
		}
		spinnerSettingData(2) {
			title { R.string.paragraph_indent }
			spinnerField { shosetsuSettings::readerIndentSize }
			arrayAdapter = ArrayAdapter(
					context!!,
					android.R.layout.simple_spinner_dropdown_item,
					resources!!.getStringArray(R.array.sizes_with_none)
			)
		}

		customBottomSettingData(1) {
			title { R.string.reader_theme }
			customView {
				activity!!.getSystemService<LayoutInflater>()!!.inflate(
						R.layout.reader_theme_selection,
						null,
						false
				).apply {
					val recycler = findViewById<RecyclerView>(R.id.color_picker_options)
					val itemAdapter = ItemAdapter<ColorChoice>()
					val fastAdapter = FastAdapter.with(itemAdapter)
					fastAdapter.selectExtension {
						isSelectable = true
						setSelectionListener { item, _ ->
							fastAdapter.notifyItemChanged(fastAdapter.getPosition(item))
						}
					}
					recycler.adapter = fastAdapter
					fastAdapter.setOnClickListener { _, _, item, _ ->
						shosetsuSettings.readerTheme = item.identifier.toInt()
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


					shosetsuSettings.readerUserThemesLive.observe(this@ReaderSettings) { list ->
						itemAdapter.clear()
						list.find {
							it.identifier == shosetsuSettings.readerTheme.toLong()
						}?.isSelected = true
						itemAdapter.add(list)
					}
				}
			}
		}
		switchSettingData(1) {
			title { R.string.inverted_swipe }
			checker { shosetsuSettings::isInvertedSwipe }
		}
		switchSettingData(1) {
			title { R.string.tap_to_scroll }
			checker { shosetsuSettings::isTapToScroll }
		}
		switchSettingData(4) {
			title { "Resume first unread" }
			description {
				"Instead of resuming the first chapter that is not read(can be reading), " +
						"the app will open the first unread chapter"
			}
			checker { shosetsuSettings::resumeOpenFirstUnread }
		}
	}
}