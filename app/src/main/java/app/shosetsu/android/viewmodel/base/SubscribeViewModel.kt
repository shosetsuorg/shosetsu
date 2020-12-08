package app.shosetsu.android.viewmodel.base

import androidx.lifecycle.LiveData
import app.shosetsu.common.com.dto.HResult

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
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 * Allows a view to subscribe to the view model
 */
interface SubscribeViewModel<T> {
	/**
	 * LiveData of this class
	 */
	val liveData: LiveData<T>
}

/**
 * Child of [SubscribeViewModel]
 * Passed [T] as an [HResult] of [T]
 */
interface SubscribeHandleViewModel<T : Any> : SubscribeViewModel<HResult<T>>