package app.shosetsu.android.common.dto

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
 * This is only to be used for android [LiveData]. This is because android LiveData does not support
 * proper exception handling.
 *
 * @since 14 / 02 / 2022
 * @author Doomsdayrs
 */
sealed class LiveDataResult<out T> {
	data class Success<out T>(val result: T) : LiveDataResult<T>()

	data class Failure(val cause: Throwable) : LiveDataResult<Nothing>()

	object Loading : LiveDataResult<Nothing>()
}
