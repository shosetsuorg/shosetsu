package app.shosetsu.android.providers.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteException
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.model.database.*
import app.shosetsu.android.providers.database.converters.*
import app.shosetsu.android.providers.database.dao.*
import app.shosetsu.android.providers.database.migrations.RemoveMigration
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
		DBChapterEntity::class,
		DBDownloadEntity::class,
		DBExtensionEntity::class,
		DBExtLibEntity::class,
		DBNovelReaderSettingEntity::class,
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
	ChapterTypeConverter::class,
	ReadingStatusConverter::class,
	StringArrayConverters::class,
	VersionConverter::class,
)
@GenerateRoomMigrations
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
					}
				).addMigrations(
					object : Migration(2, 3) {
						@Throws(SQLException::class)
						override fun migrate(database: SupportSQLiteDatabase) {
							// Handle repository migration
							run {
								val repositoryTableName = "repositories"

								// Creates new table
								database.execSQL("CREATE TABLE IF NOT EXISTS `${repositoryTableName}_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `url` TEXT NOT NULL UNIQUE, `name` TEXT NOT NULL)")

								val cursor = database.query("SELECT * FROM $repositoryTableName")
								while (cursor.moveToNext()) {
									database.insert(
										"${repositoryTableName}_new",
										OnConflictStrategy.ABORT,
										ContentValues().apply {
											val keyURL = "url"
											val keyName = "name"
											put(
												keyURL,
												cursor.getString(cursor.getColumnIndex(keyURL))
											)
											put(
												keyName,
												cursor.getString(cursor.getColumnIndex(keyName))
											)
										}
									)
								}

								// Drop
								database.execSQL("DROP TABLE $repositoryTableName")

								// Rename new table to fill in
								database.execSQL("ALTER TABLE `${repositoryTableName}_new` RENAME TO `${repositoryTableName}`")
								database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_${repositoryTableName}_url` ON `${repositoryTableName}` (`url`)")
							}
							// Handle chapter migration
							run {
								val chaptersTableName = "chapters"
								database.execSQL("CREATE TABLE IF NOT EXISTS `${chaptersTableName}_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `url` TEXT NOT NULL, `novelID` INTEGER NOT NULL, `formatterID` INTEGER NOT NULL, `title` TEXT NOT NULL, `releaseDate` TEXT NOT NULL, `order` REAL NOT NULL, `readingPosition` REAL NOT NULL, `readingStatus` INTEGER NOT NULL, `bookmarked` INTEGER NOT NULL, `isSaved` INTEGER NOT NULL, FOREIGN KEY(`novelID`) REFERENCES `novels`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`formatterID`) REFERENCES `extensions`(`id`) ON UPDATE CASCADE ON DELETE SET NULL )")

								val cursor = database.query("SELECT * FROM $chaptersTableName")
								while (cursor.moveToNext()) {
									database.insert(
										"${chaptersTableName}_new",
										OnConflictStrategy.ABORT,
										ContentValues().apply {
											this["'id'"] = cursor.getInt("id")
											this["'url'"] = cursor.getString("url")
											this["'novelID'"] = cursor.getInt("novelID")
											this["'formatterID'"] = cursor.getInt("formatterID")
											this["'title'"] = cursor.getString("title")
											this["'releaseDate'"] = cursor.getString("releaseDate")
											this["'order'"] = cursor.getDouble("order")
											this["'readingPosition'"] = 0.0
											this["'readingStatus'"] = cursor.getInt("readingStatus")
											this["'bookmarked'"] = cursor.getInt("bookmarked")
											this["'isSaved'"] = cursor.getInt("isSaved")
										}
									)
								}

								// Drop
								database.execSQL("DROP TABLE $chaptersTableName")
								database.execSQL("ALTER TABLE `${chaptersTableName}_new` RENAME TO `${chaptersTableName}`")

								// Indexs

								database.execSQL("CREATE INDEX IF NOT EXISTS `index_chapters_novelID` ON `${chaptersTableName}` (`novelID`)")
								database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_chapters_url` ON `${chaptersTableName}` (`url`)")
								database.execSQL("CREATE INDEX IF NOT EXISTS `index_chapters_formatterID` ON `${chaptersTableName}` (`formatterID`)")
							}

							// Migration to create novel_settings
							run {
								database.execSQL("CREATE TABLE IF NOT EXISTS `novel_settings` (`novelID` INTEGER NOT NULL, `sortType` TEXT NOT NULL, `showOnlyReadingStatusOf` INTEGER, `showOnlyBookmarked` INTEGER NOT NULL, `showOnlyDownloaded` INTEGER NOT NULL, `reverseOrder` INTEGER NOT NULL, PRIMARY KEY(`novelID`), FOREIGN KEY(`novelID`) REFERENCES `novels`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
								database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_novel_settings_novelID` ON `novel_settings` (`novelID`)")
							}

							// Create novel_reader_settings
							run {
								database.execSQL("CREATE TABLE IF NOT EXISTS `novel_reader_settings` (`novelID` INTEGER NOT NULL, `paragraphIndentSize` INTEGER NOT NULL, `paragraphSpacingSize` REAL NOT NULL, PRIMARY KEY(`novelID`), FOREIGN KEY(`novelID`) REFERENCES `novels`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
								database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_novel_reader_settings_novelID` ON `novel_reader_settings` (`novelID`)")
							}
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
	abstract val novelReaderSettingsDao: NovelReaderSettingsDao
	abstract val novelsDao: NovelsDao
	abstract val novelSettingsDao: NovelSettingsDao
	abstract val repositoryDao: RepositoryDao
	abstract val updatesDao: UpdatesDao
}