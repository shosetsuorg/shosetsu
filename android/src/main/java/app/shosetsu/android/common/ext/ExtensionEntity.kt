package app.shosetsu.android.common.ext

import app.shosetsu.android.domain.model.database.DBExtensionEntity
import app.shosetsu.common.domain.model.local.ExtensionEntity

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

fun ExtensionEntity.toDB(): DBExtensionEntity = DBExtensionEntity(
	id = id,
	repoID = repoID,
	name = name,
	fileName = fileName,
	imageURL = imageURL,
	lang = lang,
	chapterType = chapterType,
	version = installedVersion,
	md5 = md5,
	type = type,
	enabled = enabled
)