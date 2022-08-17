package app.shosetsu.android.common.utils

import app.shosetsu.android.common.enums.ProductFlavors
import app.shosetsu.android.BuildConfig
import kotlinx.serialization.json.Json


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
 * 20 / 06 / 2020
 */

fun flavor(): ProductFlavors = ProductFlavors.fromKey(BuildConfig.FLAVOR)

val backupJSON
	get() = Json {
		encodeDefaults = true
		ignoreUnknownKeys = true // Ignore to allow unknown values
	}