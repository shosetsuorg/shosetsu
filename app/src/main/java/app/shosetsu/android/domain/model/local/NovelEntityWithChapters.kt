package app.shosetsu.android.domain.model.local

import androidx.room.Relation

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
data class NovelEntityWithChapters(
		val novelEntity: NovelEntity,
		@Relation(parentColumn = "id", entityColumn = "id", entity = ChapterEntity::class)
		val array: Array<ChapterEntity>,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as NovelEntityWithChapters

		if (novelEntity != other.novelEntity) return false
		if (!array.contentEquals(other.array)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = novelEntity.hashCode()
		result = 31 * result + array.contentHashCode()
		return result
	}
}