package com.github.doomsdayrs.apps.shosetsu.domain.model.local

import androidx.room.ColumnInfo
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageBookUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.URLTitleImageUI
import java.io.Serializable

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

data class CountIDTuple(
		@ColumnInfo(name = "id") val id: Int,
		@ColumnInfo(name = "COUNT(*)") val count: Int
) : Serializable


data class CountNovelIDTuple(
		@ColumnInfo(name = "novelID") val id: Int,
		@ColumnInfo(name = "COUNT(*)") val count: Int
) : Serializable

data class BooleanChapterIDTuple(
		var boolean: Boolean,
		@ColumnInfo(name = "chapterID") val id: Int
) : Serializable

data class URLImageTitle(
		var url: String,
		var imageURL: String,
		var title: String
) : Serializable, Convertible<URLTitleImageUI> {
	override fun convertTo(): URLTitleImageUI = URLTitleImageUI(url, title, imageURL)
}

/**
 * @param id of the target
 * @param title of the data
 * @param imageURL of the data
 */
data class IDTitleImage(
		val id: Int,
		val title: String,
		val imageURL: String
) : Serializable, Convertible<IDTitleImageUI> {
	override fun convertTo(): IDTitleImageUI = IDTitleImageUI(id, title, imageURL)
}

/**
 * @param id of the target
 * @param title of the data
 * @param imageURL of the data
 */
data class IDTitleImageBook(
		val id: Int,
		val title: String,
		val imageURL: String,
		var bookmarked: Boolean
) : Serializable, Convertible<IDTitleImageBookUI> {
	override fun convertTo(): IDTitleImageBookUI = IDTitleImageBookUI(id, title, imageURL, bookmarked)
}

data class IDNameImage(
		val id: Int,
		val name: String,
		val imageURL: String
) : Serializable, Convertible<IDTitleImageUI> {
	override fun convertTo(): IDTitleImageUI = IDTitleImageUI(id, name, imageURL)
}