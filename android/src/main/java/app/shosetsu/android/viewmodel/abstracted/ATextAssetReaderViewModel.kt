package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.enums.TextAsset
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.android.viewmodel.base.SubscribeViewModel
import kotlinx.coroutines.flow.Flow

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
abstract class ATextAssetReaderViewModel : ShosetsuViewModel(), SubscribeViewModel<String?> {

	/**
	 * [LiveData] of text to display
	 */
	abstract override val liveData: Flow<String?>

	/**
	 * [LiveData] of the current [TextAsset]
	 */
	abstract val targetLiveData: Flow<TextAsset?>

	/**
	 * Set the target asset to read
	 */
	abstract fun setTarget(targetOrdinal: Int)

}