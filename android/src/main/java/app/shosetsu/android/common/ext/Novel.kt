package app.shosetsu.android.common.ext

import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel

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
 * ====================================================================
 */

/**
 * shosetsu
 * 23 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */

fun Novel.Chapter.entity(novelEntity: NovelEntity): ChapterEntity =
	ChapterEntity(
		url = this.link,
		novelID = novelEntity.id!!,
		extensionID = novelEntity.extensionID,
		title = this.title,
		releaseDate = this.release,
		order = this.order
	)

fun Novel.Chapter.entity(
	novelID: Int,
	extensionID: Int,
): ChapterEntity =
	ChapterEntity(
		url = this.link,
		novelID = novelID,
		extensionID = extensionID,
		title = this.title,
		releaseDate = this.release,
		order = this.order
	)

fun Novel.Listing.convertTo(extension: IExtension): NovelEntity = NovelEntity(
	url = this.link,
	imageURL = this.imageURL,
	title = this.title,
	extensionID = extension.formatterID,
)

fun Novel.Info.asEntity(
	link: String,
	extensionID: Int,
): NovelEntity =
	NovelEntity(
		url = link,
		imageURL = this.imageURL,
		description = this.description,
		extensionID = extensionID,
		loaded = true,
		title = this.title,
		artists = this.artists.toList(),
		authors = this.authors.toList(),
		language = this.language,
		genres = this.genres.toList(),
		tags = this.tags.toList(),
		status = this.status,
	)