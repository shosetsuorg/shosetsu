package app.shosetsu.android.view.uimodels.settings

import android.util.SparseArray
import androidx.core.view.isVisible
import app.shosetsu.android.view.uimodels.settings.base.BottomSettingsItemData
import app.shosetsu.libs.bubbleseekbar.BubbleSeekBar
import com.github.doomsdayrs.apps.shosetsu.databinding.SettingsItemBinding

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

	private var showThumbText: Boolean = false

	var showSectionMark: Boolean = false

	var seekBySection: Boolean = false

	var seekByStepSection: Boolean = false

	var autoAdjustSectionMark: Boolean = false

	var touchToSeek: Boolean = false

	var hideBubble: Boolean = false

	private var sectionTextP: Int = BubbleSeekBar.TextPosition.BELOW_SECTION_MARK

	var array: SparseArray<String> = SparseArray()

	var onProgressChangedUnit: (
		@ParameterName("bubbleSeekBar") BubbleSeekBar?,
		@ParameterName("progress") Int,
		@ParameterName("progressFloat") Float,
		@ParameterName("fromUser") Boolean,
	) -> Unit =
		{ _, _, _, _ -> }

	var onProgressActionUpUnit: (
		@ParameterName("bubbleSeekBar") BubbleSeekBar?,
		@ParameterName("progress") Int,
		@ParameterName("progressFloat") Float,
	) -> Unit =
		{ _, _, _ -> }

	var oProgressOnFinallyUnit: (
		@ParameterName("bubbleSeekBar") BubbleSeekBar?,
		@ParameterName("progress") Int,
		@ParameterName("progressFloat") Float,
		@ParameterName("fromUser") Boolean,
	) -> Unit =
		{ _, _, _, _ -> }

	override fun bindBinding(holder: SettingsItemBinding, payloads: List<Any>) {
		super.bindBinding(holder, payloads)
		with(holder) {
			bubbleSeekBar.isVisible = true

			bubbleSeekBar.configBuilder.apply {
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
			bubbleSeekBar.setCustomSectionTextArray(object : BubbleSeekBar.CustomSectionTextArray {
				override fun onCustomize(
					sectionCount: Int,
					ignored: SparseArray<String?>
				): SparseArray<String?> = array as SparseArray<String?>
			})
			bubbleSeekBar.onProgressChangedListener =
				object : BubbleSeekBar.OnProgressChangedListener {
					override fun onProgressChanged(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float,
						fromUser: Boolean,
					) {
						onProgressChangedUnit(bubbleSeekBar, progress, progressFloat, fromUser)
					}

					override fun getProgressOnActionUp(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float,
					) {
						onProgressActionUpUnit(bubbleSeekBar, progress, progressFloat)
					}

					override fun getProgressOnFinally(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float,
						fromUser: Boolean,
					) {
						oProgressOnFinallyUnit(bubbleSeekBar, progress, progressFloat, fromUser)
					}
				}
		}

	}

	override fun unbindBinding(holder: SettingsItemBinding) {
		super.unbindBinding(holder)
		holder.bubbleSeekBar.apply {
			onProgressChangedListener = null
		}
	}
}