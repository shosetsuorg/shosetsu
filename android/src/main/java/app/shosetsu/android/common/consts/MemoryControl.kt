package app.shosetsu.android.common.consts

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

// Control memory parameters

/** How many chapters allowed in memory at once */
const val MEMORY_MAX_CHAPTERS = 10L

/** How long can a chapter can remain in memory (minutes) */
const val MEMORY_EXPIRE_CHAPTER_TIME = 10L


/** How many extensions allowed in memory at once */
const val MEMORY_MAX_EXTENSIONS = 100L

/** How long can an extension remain in memory (hours) */
const val MEMORY_EXPIRE_EXTENSION_TIME = 1L


/** How many ext-libs allowed in memory at once */
const val MEMORY_MAX_EXT_LIBS = 100L

/** How long can a ext-lib remain in memory (minutes) */
const val MEMORY_EXPIRE_EXT_LIB_TIME = 20L