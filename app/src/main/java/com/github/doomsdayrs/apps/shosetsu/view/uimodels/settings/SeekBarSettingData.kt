package com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings

import android.util.SparseArray
import android.view.View
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.base.BottomSettingsItemData
import com.xw.repo.BubbleSeekBar
import com.xw.repo.BubbleSeekBar.OnProgressChangedListener

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
 * 25 / 06 / 2020
 */
class SeekBarSettingData(id: Int) : BottomSettingsItemData(id) {
	var minValue: Float = 0F
	var maxValue: Float = 10F
	var progressValue: Float = 5F

	var sectionC: Int = 2

	var showSectionText: Boolean = false

	var showThumbText: Boolean = false

	var showSectionMark: Boolean = false

	var seekBySection: Boolean = false

	var seekByStepSection: Boolean = false

	var autoAdjustSectionMark: Boolean = false

	var touchToSeek: Boolean = false

	var hideBubble: Boolean = false

	var sectionTextP = BubbleSeekBar.TextPosition.BELOW_SECTION_MARK

	var array: SparseArray<String> = SparseArray()

	var ProgressChanged: (
			@ParameterName("bubbleSeekBar") BubbleSeekBar?,
			@ParameterName("progress") Int,
			@ParameterName("progressFloat") Float,
			@ParameterName("fromUser") Boolean
	) -> Unit =
			{ _, _, _, _ -> }

	var OnProgressActionUp: (
			@ParameterName("bubbleSeekBar") BubbleSeekBar?,
			@ParameterName("progress") Int,
			@ParameterName("progressFloat") Float
	) -> Unit =
			{ _, _, _ -> }

	var ProgressOnFinally: (
			@ParameterName("bubbleSeekBar") BubbleSeekBar?,
			@ParameterName("progress") Int,
			@ParameterName("progressFloat") Float,
			@ParameterName("fromUser") Boolean
	) -> Unit =
			{ _, _, _, _ -> }

	override fun bindView(settingsItem: ViewHolder, payloads: List<Any>) {
		super.bindView(settingsItem, payloads)
		with(settingsItem) {
			seekbar.visibility = View.VISIBLE

			seekbar.configBuilder.apply {
				min(minValue)
				max(maxValue)
				progress(progressValue)
				sectionCount(sectionC)
				if (showSectionMark)
					showSectionText()
				if (showThumbText)
					showThumbText()
				if (showSectionText)
					showSectionText()
				if (seekBySection)
					seekBySection()
				if (autoAdjustSectionMark)
					autoAdjustSectionMark()
				if (seekByStepSection)
					seekStepSection()
				if (hideBubble)
					hideBubble()
				sectionTextPosition(sectionTextP)
				if (touchToSeek)
					touchToSeek()
			}.build()
			seekbar.setCustomSectionTextArray { _, _ -> array }
			seekbar.onProgressChangedListener = object : OnProgressChangedListener {
				override fun onProgressChanged(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float,
						fromUser: Boolean
				) {
					ProgressChanged(bubbleSeekBar, progress, progressFloat, fromUser)
				}

				override fun getProgressOnActionUp(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float
				) {
					OnProgressActionUp(bubbleSeekBar, progress, progressFloat)
				}

				override fun getProgressOnFinally(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float,
						fromUser: Boolean
				) {
					ProgressOnFinally(bubbleSeekBar, progress, progressFloat, fromUser)
				}
			}
		}
	}

	override fun unbindView(settingsItem: ViewHolder) {
		super.unbindView(settingsItem)
		settingsItem.seekbar.apply {
			onProgressChangedListener = null
		}
	}
}