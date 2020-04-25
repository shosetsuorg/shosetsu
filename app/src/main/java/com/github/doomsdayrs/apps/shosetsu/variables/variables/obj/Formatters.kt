package com.github.doomsdayrs.apps.shosetsu.variables.obj

import android.content.Context
import app.shosetsu.lib.Filter
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.extensionsDao
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.FormatterCard

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
 * ====================================================================
 */
/**
 * Shosetsu
 * 30 / May / 2019
 *
 * @author github.com/doomsdayrs
 */
object Formatters {
	val unknown = object : Formatter {
		override val formatterID: Int = -1

		override val baseURL: String
			get() = throw Exception("Unknown Formatter")
		override val hasCloudFlare: Boolean
			get() = throw Exception("Unknown Formatter")
		override val hasSearch: Boolean
			get() = throw Exception("Unknown Formatter")
		override val imageURL: String
			get() = throw Exception("Unknown Formatter")
		override val listings: Array<Formatter.Listing>
			get() = throw Exception("Unknown Formatter")
		override val name: String
			get() = throw Exception("Unknown Formatter")
		override val searchFilters: Array<Filter<*>>
			get() = throw Exception("Unknown Formatter")
		override val settings: Array<Filter<*>>
			get() = throw Exception("Unknown Formatter")

		override fun getPassage(chapterURL: String): String = throw Exception("Unknown Formatter")

		override fun parseNovel(novelURL: String, loadChapters: Boolean, reporter: (status: String) -> Unit) =
				throw Exception("Unknown Formatter")

		override fun search(data: Array<*>, reporter: (status: String) -> Unit) =
				throw Exception("Unknown Formatter")

		override fun updateSetting(id: Int, value: Any?): Unit = throw Exception("Unknown Formatter")
	}

	val formatters = ArrayList<Formatter>()

	/**
	 * Loads the formatters
	 */
	fun load(context: Context) {
		val fileNames = extensionsDao
				.loadPoweredFormatterFileNames()
		fileNames.forEach {
			formatters.add(
					LuaFormatter(FormatterUtils.makeFormatterFile(context, it))
			)
		}
	}

	fun removeByID(formatterID: Int) {
		formatters.forEachIndexed { index, formatter ->
			if (formatter.formatterID == formatterID) {
				formatters.removeAt(index)
				return
			}
		}
	}

	fun addFormatter(formatter: Formatter) {
		removeByID(formatter.formatterID)
		formatters.add(formatter)
		formatters.sortBy { it.name }
	}

	fun getByID(ID: Int): Formatter = formatters.firstOrNull { it.formatterID == ID } ?: unknown

	fun getAsCards(): ArrayList<FormatterCard> {
		val catalogueCards = ArrayList<FormatterCard>()
		for (formatter in formatters) catalogueCards.add(FormatterCard(formatter))
		catalogueCards.sortedWith(compareBy { it.title })
		return catalogueCards
	}
}