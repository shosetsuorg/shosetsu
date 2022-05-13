package app.shosetsu.android.dto

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Convertible object, useful when creating objects that transform itself into other types
 */
interface Convertible<T> {
	/** Converts this into [T] using implementation*/
	fun convertTo(): T
}

/**
 * Converts a list of [Convertible] from their [I] form to their [O] form
 */
inline fun <reified O, reified I : Convertible<O>> List<I>.convertList(): List<O> =
	this.map { it.convertTo() }