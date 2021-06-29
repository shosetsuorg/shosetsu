package app.shosetsu.android.viewmodel.impl

import android.app.Application
import androidx.lifecycle.LiveData
import app.shosetsu.android.common.enums.TextAsset
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.readAsset
import app.shosetsu.android.viewmodel.abstracted.ATextAssetReaderViewModel
import app.shosetsu.common.dto.*
import kotlinx.coroutines.flow.MutableStateFlow

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
 * Shosetsu
 *
 * @since 29 / 06 / 2021
 * @author Doomsdayrs
 */
class TextAssetReaderViewModel(val application: Application) : ATextAssetReaderViewModel() {
	override val liveData: LiveData<HResult<String>>
		get() = targetFlow.mapResult { successResult(application.readAsset(it.assetName+".txt")) }
			.asIOLiveData()

	private val targetFlow = MutableStateFlow<HResult<TextAsset>>(empty)

	override val targetLiveData: LiveData<HResult<TextAsset>>
		get() = targetFlow.asIOLiveData()

	override fun setTarget(targetOrdinal: Int) {
		logI("Opening up asset via ordinal $targetOrdinal")
		// If target is empty, emit
		if (targetFlow.value is HResult.Success) {
			// If the targets are the same, ignore and return
			if ((targetFlow.value as? HResult.Success)?.data?.ordinal == targetOrdinal)
				return
		}
		targetFlow.tryEmit(loading)
		targetFlow.tryEmit(successResult(TextAsset.values()[targetOrdinal]))
	}
}