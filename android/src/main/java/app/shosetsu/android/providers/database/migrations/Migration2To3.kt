package app.shosetsu.android.providers.database.migrations

import android.content.ContentValues
import android.database.SQLException
import androidx.room.OnConflictStrategy
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.shosetsu.android.common.ext.getDouble
import app.shosetsu.android.common.ext.getInt
import app.shosetsu.android.common.ext.getString
import app.shosetsu.android.common.ext.set

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
 * @since 15 / 03 / 2022
 * @author Doomsdayrs
 */
object Migration2To3 : Migration(2, 3) {
	@Throws(SQLException::class)
	override fun migrate(database: SupportSQLiteDatabase) {
		// Handle repository migration
		run {
			val tableName = "repositories"

			// Creates new table
			database.execSQL("CREATE TABLE IF NOT EXISTS `${tableName}_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `url` TEXT NOT NULL UNIQUE, `name` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL)")

			// Migrate
			database.execSQL("INSERT INTO `${tableName}_new` SELECT _rowid_, `url`, `url` as `name`, 1 as `isEnabled` FROM `$tableName`")

			// Drop
			database.execSQL("DROP TABLE $tableName")

			// Rename table_new to table
			database.execSQL("ALTER TABLE `${tableName}_new` RENAME TO `${tableName}`")

			// Creat indexes
			database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_${tableName}_url` ON `${tableName}` (`url`)")
		}

		// Handle chapter migration
		run {
			val tableName = "chapters"

			// Create new table
			database.execSQL("CREATE TABLE IF NOT EXISTS `${tableName}_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `url` TEXT NOT NULL, `novelID` INTEGER NOT NULL, `formatterID` INTEGER NOT NULL, `title` TEXT NOT NULL, `releaseDate` TEXT NOT NULL, `order` REAL NOT NULL, `readingPosition` REAL NOT NULL, `readingStatus` INTEGER NOT NULL, `bookmarked` INTEGER NOT NULL, `isSaved` INTEGER NOT NULL, FOREIGN KEY(`novelID`) REFERENCES `novels`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`formatterID`) REFERENCES `extensions`(`id`) ON UPDATE CASCADE ON DELETE SET NULL )")

			// Handle migration
			val cursor = database.query("SELECT * FROM $tableName")
			while (cursor.moveToNext()) {
				database.insert(
					"${tableName}_new",
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
			database.execSQL("DROP TABLE $tableName")

			// Rename table_new to table
			database.execSQL("ALTER TABLE `${tableName}_new` RENAME TO `${tableName}`")

			// Create indexes
			database.execSQL("CREATE INDEX IF NOT EXISTS `index_chapters_novelID` ON `${tableName}` (`novelID`)")
			database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_chapters_url` ON `${tableName}` (`url`)")
			database.execSQL("CREATE INDEX IF NOT EXISTS `index_chapters_formatterID` ON `${tableName}` (`formatterID`)")
		}

		// Handle extension migration
		run {
			val tableName = "extensions"

			// Create new table
			database.execSQL("CREATE TABLE IF NOT EXISTS `${tableName}_new` (`id` INTEGER NOT NULL, `repoID` INTEGER NOT NULL, `name` TEXT NOT NULL, `fileName` TEXT NOT NULL, `imageURL` TEXT, `lang` TEXT NOT NULL, `enabled` INTEGER NOT NULL, `installed` INTEGER NOT NULL, `installedVersion` TEXT, `repositoryVersion` TEXT NOT NULL, `chapterType` INTEGER NOT NULL, `md5` TEXT NOT NULL, `type` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`repoID`) REFERENCES `repositories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )\n")

			// Migrate
			database.execSQL(
				"""
INSERT INTO `${tableName}_new` SELECT `id`, `repoID`, `name`, `name`, `imageURL`, `lang`,`enabled`,`installed`,`installedVersion`,`repositoryVersion`,0,`md5`,0 FROM `$tableName`									
								"""
			)

			// Drop
			database.execSQL("DROP TABLE $tableName")

			// Rename table_new to table
			database.execSQL("ALTER TABLE `${tableName}_new` RENAME TO `${tableName}`")

			// Create indexes
			database.execSQL("CREATE INDEX IF NOT EXISTS `index_extensions_repoID` ON `${tableName}` (`repoID`)")
		}

		// Handle novel migration
		object : RemoveMigration(2, 3) {
			override fun migrate(database: SupportSQLiteDatabase) {
				deleteColumnFromTable(database, "novels", "readerType")
			}
		}.migrate(database)

		// Create novel_settings
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