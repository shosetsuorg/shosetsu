package app.shosetsu.android.ui.reader

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
 * ====================================================================
 */
/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */

fun BubbleSeekBar.setBubbleOnProgressChanged(
	onProgressChangedFun: (
		@ParameterName("bubbleSeekBar") BubbleSeekBar?,
		@ParameterName("progress") Int,
		@ParameterName("progressFloat") Float,
		@ParameterName("fromUser") Boolean,
	) -> Unit,
) {
	this.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
		override fun onProgressChanged(
			bubbleSeekBar: BubbleSeekBar?,
			progress: Int,
			progressFloat: Float,
			fromUser: Boolean,
		) {
			onProgressChangedFun(bubbleSeekBar, progress, progressFloat, fromUser)
		}

		override fun getProgressOnActionUp(
			bubbleSeekBar: BubbleSeekBar?,
			progress: Int,
			progressFloat: Float,
		) {
		}

		override fun getProgressOnFinally(
			bubbleSeekBar: BubbleSeekBar?,
			progress: Int,
			progressFloat: Float,
			fromUser: Boolean,
		) {
		}
	}
}