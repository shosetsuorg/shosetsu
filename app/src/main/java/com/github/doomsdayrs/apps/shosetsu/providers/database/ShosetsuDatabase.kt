package com.github.doomsdayrs.apps.shosetsu.providers.database

import android.content.Context
import androidx.room.*
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.*
import com.github.doomsdayrs.apps.shosetsu.providers.database.converters.NovelStatusConverter
import com.github.doomsdayrs.apps.shosetsu.providers.database.converters.ReadingStatusConverter
import com.github.doomsdayrs.apps.shosetsu.providers.database.converters.StringArrayConverters
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
@TypeConverters(
		ReadingStatusConverter::class,
		StringArrayConverters::class,
		NovelStatusConverter::class
)
abstract class ShosetsuDatabase : RoomDatabase() {
	companion object {
		@Volatile
		private lateinit var databaseShosetsu: ShosetsuDatabase;

		@Synchronized
		fun getRoomDatabase(context: Context): ShosetsuDatabase {
			if (!Companion::databaseShosetsu.isInitialized)
				databaseShosetsu = Room.databaseBuilder(
						context.applicationContext,
						ShosetsuDatabase::class.java,
						"room_database"
				).build()
			GlobalScope.launch {
				databaseShosetsu.repositoryDao().initializeData()
			}
			return databaseShosetsu
		}
	}

	abstract fun extensionsDao(): ExtensionsDao
	abstract fun repositoryDao(): RepositoryDao
	abstract fun scriptLibDao(): ExtensionLibraryDao
	abstract fun updatesDao(): UpdatesDao
	abstract fun downloadsDao(): DownloadsDao
	abstract fun chaptersDao(): ChaptersDao
	abstract fun novelsDao(): NovelsDao
}