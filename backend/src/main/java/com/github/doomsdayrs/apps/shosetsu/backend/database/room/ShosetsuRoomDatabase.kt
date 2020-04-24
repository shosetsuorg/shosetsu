package com.github.doomsdayrs.apps.shosetsu.backend.database.room

import android.content.Context
import androidx.room.*
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.converters.FormatterConverter
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.converters.NovelStatusConverter
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.converters.ReadingStatusConverter
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.converters.StringArrayConverters
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.dao.*
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.*

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
 * 17 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Fts4
@Database(
		entities = [
			ExtensionEntity::class,
			RepositoryEntity::class,
			ExtensionLibraryEntity::class,
			DownloadEntity::class,
			UpdateEntity::class,
			ChapterEntity::class,
			NovelEntity::class
		],
		version = 1
)
@TypeConverters(FormatterConverter::class, ReadingStatusConverter::class, StringArrayConverters::class, NovelStatusConverter::class)
abstract class ShosetsuRoomDatabase : RoomDatabase() {
	companion object {
		private lateinit var databaseShosetsu: ShosetsuRoomDatabase;

		fun getRoomDatabase(context: Context): ShosetsuRoomDatabase {
			if (!::databaseShosetsu.isInitialized)
				synchronized(ShosetsuRoomDatabase::class) {
					databaseShosetsu = Room.databaseBuilder(
							context.applicationContext,
							ShosetsuRoomDatabase::class.java,
							"room_database"
					).build()
				}
			databaseShosetsu.repositoryDao().initializeData()
			return databaseShosetsu
		}
	}

	abstract fun extensionsDao(): ExtensionsDao
	abstract fun repositoryDao(): RepositoryDao
	abstract fun scriptLibDao(): ScriptLibDao
	abstract fun updatesDao(): UpdatesDao
	abstract fun downloadsDao(): DownloadsDao
	abstract fun chaptersDao(): ChaptersDao
	abstract fun novelsDao(): NovelsDao
}