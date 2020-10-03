package app.shosetsu.android.domain.model.local

import app.shosetsu.android.domain.model.base.Convertible
import app.shosetsu.android.view.uimodels.model.UpdateUI

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
 * 28 / 08 / 2020
 *
 * Loads all data for the [UpdateEntity]
 *
 * @param chapterID ID of the chapter
 * @param novelID ID of the novel
 * @param time The time the update was made
 * @param chapterName Name of the chapter, loaded via [chapterID]
 * @param novelName Name of the novel, loaded via [novelID]
 * @param novelImageURL imageURL of the novel, loaded via [novelID]
 */
data class UpdateCompleteEntity(
		val chapterID: Int,
		val novelID: Int,
		val time: Long,
		val chapterName: String,
		val novelName: String,
		val novelImageURL: String,
) : Convertible<UpdateUI> {

	override fun convertTo(): UpdateUI = UpdateUI(
			chapterID,
			novelID,
			time,
			chapterName,
			novelName,
			novelImageURL
	)
}