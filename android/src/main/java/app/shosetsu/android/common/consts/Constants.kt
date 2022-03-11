package app.shosetsu.android.common.consts

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


const val SELECTED_STROKE_WIDTH: Int = 8

/** How fast the user must fling inorder to activate the scroll to last */
const val FLING_THRESHOLD = 19999

/**
 * File system directory for extension scripts
 */
const val FILE_SCRIPT_DIR: String = "/scripts/"

/**
 * File system directory for library scripts
 */
const val FILE_LIBRARY_DIR: String = "/libraries/"

/**
 * File system directory for source files
 */
const val FILE_SOURCE_DIR: String = "/src/"

/**
 * Directory on the repository that contains the extensions,
 * proceeding this will be the extension language
 */
const val REPO_SOURCE_DIR: String = "/src/"

const val APP_UPDATE_CACHE_FILE = "SHOSETSU_APP_UPDATE.json"


const val READER_BAR_ALPHA: Float = 0.95F

const val APK_MIME = "application/vnd.android.package-archive"

/**
 * The version of backups this build of shosetsu supports
 */
const val VERSION_BACKUP: String = "1.0.0"