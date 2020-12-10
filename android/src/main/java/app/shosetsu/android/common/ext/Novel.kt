package app.shosetsu.android.common.ext

import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.NovelEntity
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
				formatterID = novelEntity.formatterID,
				title = this.title,
				releaseDate = this.release,
				order = this.order
		)

fun Novel.Listing.convertTo(formatter: IExtension): NovelEntity = NovelEntity(
		url = this.link,
		imageURL = this.imageURL,
		title = this.title,
		formatterID = formatter.formatterID
)