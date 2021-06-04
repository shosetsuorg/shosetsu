package app.shosetsu.android.view.uimodels.settings.dsl

import app.shosetsu.android.view.uimodels.settings.SeekBarSettingData
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.base.SettingsListBuilder
import app.shosetsu.libs.bubbleseekbar.BubbleSeekBar

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
 * 26 / 06 / 2020
 */

@SettingsItemDSL
inline fun seekBarSettingData(
	id: Int,
	action: SeekBarSettingData.() -> Unit,
): SettingsItemData = SeekBarSettingData(id).also(action)

@SettingsItemDSL
inline fun SettingsListBuilder.seekBarSettingData(
	id: Int,
	action: SeekBarSettingData.() -> Unit,
): Unit = this.let { list.add(SeekBarSettingData(id).also(action)) }


@SettingsItemDSL
inline fun SeekBarSettingData.range(
	crossinline value: SeekBarSettingData.() -> Pair<
			@ParameterName("lowerBound") Float,
			@ParameterName("upperBound") Float
			>,
): Unit = value().let {
	minValue = it.first
	maxValue = it.second
}

@SettingsItemDSL
inline fun SeekBarSettingData.onProgressChanged(
	crossinline value: SeekBarSettingData.(
		@ParameterName("bubbleSeekBar") BubbleSeekBar?,
		@ParameterName("progress") Int,
		@ParameterName("progressFloat") Float,
		@ParameterName("fromUser") Boolean,
	) -> Unit,
) {
	onProgressChangedUnit = { p1, p2, p3, p4 -> value(p1, p2, p3, p4) }
}

@SettingsItemDSL
inline fun SeekBarSettingData.onProgressActionUp(
	crossinline value: SeekBarSettingData.(
		@ParameterName("bubbleSeekBar") BubbleSeekBar?,
		@ParameterName("progress") Int,
		@ParameterName("progressFloat") Float,
	) -> Unit,
) {
	onProgressActionUpUnit = { p1, p2, p3 -> value(p1, p2, p3) }
}


@SettingsItemDSL
inline fun SeekBarSettingData.onProgressFinally(
	crossinline value: SeekBarSettingData.(
		@ParameterName("bubbleSeekBar") BubbleSeekBar?,
		@ParameterName("progress") Int,
		@ParameterName("progressFloat") Float,
		@ParameterName("fromUser") Boolean,
	) -> Unit,
) {
	oProgressOnFinallyUnit = { p1, p2, p3, p4 -> value(p1, p2, p3, p4) }
}