package app.shosetsu.android.common.ext

import app.shosetsu.android.domain.model.database.DBInstalledExtensionEntity
import app.shosetsu.android.domain.model.local.GenericExtensionEntity
import app.shosetsu.android.domain.model.local.InstalledExtensionEntity

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
 * 05 / 12 / 2020
 */

fun InstalledExtensionEntity.toDB(): DBInstalledExtensionEntity = DBInstalledExtensionEntity(
	id = id,
	repoID = repoID,
	name = name,
	fileName = fileName,
	imageURL = imageURL,
	lang = lang,
	chapterType = chapterType,
	version = version,
	md5 = md5,
	type = type,
	enabled = enabled
)

fun InstalledExtensionEntity.generify(): GenericExtensionEntity = GenericExtensionEntity(
	id = id,
	repoID = repoID,
	name = name,
	fileName = fileName,
	imageURL = imageURL,
	lang = lang,
	version = version,
	md5 = md5,
	type = type,
)