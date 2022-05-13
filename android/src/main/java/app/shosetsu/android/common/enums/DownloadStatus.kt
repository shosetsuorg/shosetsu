package app.shosetsu.android.common.enums

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
 * shosetsu
 * 09 / 10 / 2020
 */
enum class DownloadStatus(val key: Int) {
	PENDING(0),

	/** Notates that this download is currently waiting,
	 * either for the thread pool to open up or some other reason */
	WAITING(3),
	DOWNLOADING(1),
	PAUSED(2),
	ERROR(-1),
	COMPLETE(4);

	companion object {
		fun fromInt(key: Int) = values().find { it.key == key } ?: ERROR
	}
}