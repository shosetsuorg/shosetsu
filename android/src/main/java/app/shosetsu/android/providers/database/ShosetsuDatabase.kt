package app.shosetsu.android.providers.database

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.shosetsu.android.domain.model.database.*
import app.shosetsu.android.providers.database.converters.*
import app.shosetsu.android.providers.database.dao.*
import app.shosetsu.android.providers.database.migrations.RemoveMigration
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
		DBChapterEntity::class,
		DBDownloadEntity::class,
		DBExtensionEntity::class,
		DBExtLibEntity::class,
		DBNovelEntity::class,
		DBNovelSettingsEntity::class,
		DBRepositoryEntity::class,
		DBUpdate::class,
	],
	version = 3
)
@TypeConverters(
	ChapterSortTypeConverter::class,
	DownloadStatusConverter::class,
	ListConverter::class,
	NovelStatusConverter::class,
	ReaderTypeConverter::class,
	ReadingStatusConverter::class,
	StringArrayConverters::class,
	VersionConverter::class,
)
abstract class ShosetsuDatabase : RoomDatabase() {
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
					object : RemoveMigration(1, 2) {
						override fun migrate(database: SupportSQLiteDatabase) {
							deleteColumnFromTable(database, "chapters", "savePath")
						}
					},
					object : Migration(2, 3) {
						override fun migrate(database: SupportSQLiteDatabase) {
							database.execSQL("CREATE TABLE IF NOT EXISTS novel_settings (`novelID` INTEGER NOT NULL, `sortType` TEXT NOT NULL, `showOnlyReadingStatusOf` INTEGER, `showOnlyBookmarked` INTEGER NOT NULL, `showOnlyDownloaded` INTEGER NOT NULL, `reverseOrder` INTEGER NOT NULL, PRIMARY KEY(`novelID`), FOREIGN KEY(`novelID`) REFERENCES `novels`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
						}
					}
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

	abstract val chaptersDao: ChaptersDao
	abstract val downloadsDao: DownloadsDao
	abstract val extensionLibraryDao: ExtensionLibraryDao
	abstract val extensionsDao: ExtensionsDao
	abstract val novelsDao: NovelsDao
	abstract val novelSettingsDao: NovelSettingsDao
	abstract val repositoryDao: RepositoryDao
	abstract val updatesDao: UpdatesDao
}