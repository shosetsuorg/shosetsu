package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.ext.onIO
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.ChapterHistoryEntity
import app.shosetsu.android.domain.repository.base.IChapterHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
 * Shosetsu
 *
 * @since 11 / 11 / 2021
 * @author Doomsdayrs
 */
class TempChapterHistoryRepository : IChapterHistoryRepository {
	private val chapterHistory: MutableStateFlow<List<ChapterHistoryEntity>> by lazy {
		MutableStateFlow(listOf())
	}

	override fun markChapterAsRead(chapter: ChapterEntity) {
		chapterHistory.value = chapterHistory.value + ChapterHistoryEntity.StatusUpdate(
			chapter.novelID,
			chapter.id!!,
			System.currentTimeMillis(),
			ReadingStatus.READ
		)
	}

	override fun markChapterAsReading(chapter: ChapterEntity) {
		chapterHistory.value = chapterHistory.value + ChapterHistoryEntity.StatusUpdate(
			chapter.novelID,
			chapter.id!!,
			System.currentTimeMillis(),
			ReadingStatus.READING
		)
	}

	override fun getLastRead(novelId: Int): ChapterHistoryEntity? {
		val chapters: List<ChapterHistoryEntity.StatusUpdate> =
			chapterHistory.value.filter { it.novelId == novelId }
				.filterIsInstance<ChapterHistoryEntity.StatusUpdate>()

		if (chapters.isEmpty()) return null

		return chapters.lastOrNull { it.status == ReadingStatus.READ }
	}

	override val history: Flow<List<ChapterHistoryEntity>> by lazy {
		chapterHistory.onIO()
	}
}