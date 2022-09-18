package app.shosetsu.android.providers.database

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.*
import app.shosetsu.android.domain.model.database.*
import app.shosetsu.android.providers.database.converters.*
import app.shosetsu.android.providers.database.dao.*
import app.shosetsu.android.providers.database.migrations.*
import dev.matrix.roomigrant.GenerateRoomMigrations
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
		DBCategoryEntity::class,
		DBChapterEntity::class,
		DBDownloadEntity::class,
		DBInstalledExtensionEntity::class,
		DBRepositoryExtensionEntity::class,
		DBExtLibEntity::class,
		DBNovelCategoryEntity::class,
		DBNovelReaderSettingEntity::class,
		DBNovelEntity::class,
		DBNovelSettingsEntity::class,
		DBRepositoryEntity::class,
		DBUpdate::class,
	],
	version = 7
)
@TypeConverters(
	ChapterSortTypeConverter::class,
	DownloadStatusConverter::class,
	ListConverter::class,
	NovelStatusConverter::class,
	ChapterTypeConverter::class,
	ReadingStatusConverter::class,
	StringArrayConverters::class,
	VersionConverter::class,
	ExtensionTypeConverter::class
)
@GenerateRoomMigrations
abstract class ShosetsuDatabase : RoomDatabase() {

	abstract val categoriesDao: CategoriesDao
	abstract val chaptersDao: ChaptersDao
	abstract val downloadsDao: DownloadsDao
	abstract val extensionLibraryDao: ExtensionLibraryDao
	abstract val installedExtensionsDao: InstalledExtensionsDao
	abstract val repositoryExtensionDao: RepositoryExtensionsDao
	abstract val novelCategoriesDao: NovelCategoriesDao
	abstract val novelReaderSettingsDao: NovelReaderSettingsDao
	abstract val novelsDao: NovelsDao
	abstract val novelSettingsDao: NovelSettingsDao
	abstract val repositoryDao: RepositoryDao
	abstract val updatesDao: UpdatesDao

	companion object {
		@Volatile
		private lateinit var databaseShosetsu: ShosetsuDatabase

		@Synchronized
		fun getRoomDatabase(context: Context): ShosetsuDatabase {
			if (!Companion::databaseShosetsu.isInitialized)
				databaseShosetsu = Room.databaseBuilder(
					context.applicationContext,
					ShosetsuDatabase::class.java,
					"room_database"
				).addMigrations(
					Migration1To2,
					Migration2To3,
					Migration3To4,
					Migration4To5,
					Migration5To6,
					Migration6To7
				).build()

			GlobalScope.launch {
				try {
					databaseShosetsu.repositoryDao.initializeData()
				} catch (e: SQLiteException) {
					e.printStackTrace()
				}
			}
			return databaseShosetsu
		}
	}
}