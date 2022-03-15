package app.shosetsu.android.providers.database.migrations

import android.database.SQLException
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
object Migration5To6 : Migration(5, 6) {

	@Throws(SQLException::class)
	override fun migrate(database: SupportSQLiteDatabase) {

		// Chapters
		// We drop the foreign key relation with extensions
		database.beginTransaction()
		try {
			database.execSQL("ALTER TABLE `chapters` RENAME TO `chapters_old`")
			database.execSQL("CREATE TABLE IF NOT EXISTS `chapters` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `url` TEXT NOT NULL, `novelID` INTEGER NOT NULL, `formatterID` INTEGER NOT NULL, `title` TEXT NOT NULL, `releaseDate` TEXT NOT NULL, `order` REAL NOT NULL, `readingPosition` REAL NOT NULL, `readingStatus` INTEGER NOT NULL, `bookmarked` INTEGER NOT NULL, `isSaved` INTEGER NOT NULL, FOREIGN KEY(`novelID`) REFERENCES `novels`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
			database.execSQL("INSERT INTO `chapters` SELECT * FROM `chapters_old`")
			database.execSQL("DROP TABLE IF EXISTS `chapters_old`")
			database.execSQL("CREATE INDEX IF NOT EXISTS `index_chapters_novelID` ON `chapters` (`novelID`)")
			database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_chapters_url_formatterID` ON `chapters` (`url`, `formatterID`)")
			database.setTransactionSuccessful()
		} finally {
			database.endTransaction()
		}

		// Novels
		database.beginTransaction()
		try {
			database.execSQL("ALTER TABLE `novels` RENAME TO `novels_old`")
			database.execSQL("CREATE TABLE IF NOT EXISTS `novels` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `url` TEXT NOT NULL, `formatterID` INTEGER NOT NULL, `bookmarked` INTEGER NOT NULL, `loaded` INTEGER NOT NULL, `title` TEXT NOT NULL, `imageURL` TEXT NOT NULL, `description` TEXT NOT NULL, `language` TEXT NOT NULL, `genres` TEXT NOT NULL, `authors` TEXT NOT NULL, `artists` TEXT NOT NULL, `tags` TEXT NOT NULL, `status` INTEGER NOT NULL)")
			database.execSQL("INSERT INTO `novels` SELECT * FROM `novels_old`")
			database.execSQL("DROP TABLE IF EXISTS `novels_old`")
			database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_novels_url_formatterID` ON `novels` (`url`, `formatterID`)")
			database.setTransactionSuccessful()
		} finally {
			database.endTransaction()
		}

		// Extensions
		database.beginTransaction()
		try {
			database.execSQL("CREATE TABLE IF NOT EXISTS `installed_extension` (`id` INTEGER NOT NULL, `repoID` INTEGER NOT NULL, `name` TEXT NOT NULL, `fileName` TEXT NOT NULL, `imageURL` TEXT NOT NULL, `lang` TEXT NOT NULL, `version` TEXT NOT NULL, `md5` TEXT NOT NULL, `type` INTEGER NOT NULL, `enabled` INTEGER NOT NULL, `chapterType` INTEGER NOT NULL, PRIMARY KEY(`id`))")
			database.execSQL("CREATE TABLE IF NOT EXISTS `repository_extension` (`repoId` INTEGER NOT NULL, `id` INTEGER NOT NULL, `name` TEXT NOT NULL, `fileName` TEXT NOT NULL, `imageURL` TEXT NOT NULL, `lang` TEXT NOT NULL, `version` TEXT NOT NULL, `md5` TEXT NOT NULL, `type` INTEGER NOT NULL, PRIMARY KEY(`repoId`, `id`), FOREIGN KEY(`repoId`) REFERENCES `repositories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
			database.execSQL("INSERT INTO `repository_extension` SELECT `repoID`, `id`, `name`, `fileName`, `imageURL`, `lang`, `repositoryVersion`, `md5`, `type` FROM `extensions`")
			database.execSQL("INSERT INTO `installed_extension` SELECT `id`, `repoID`, `name`, `fileName`, `imageURL`, `lang`, `installedVersion`, `md5`, `type`, `enabled`, `chapterType` FROM `extensions` WHERE `installed`=1")
			database.execSQL("DROP TABLE IF EXISTS `extensions`")
			database.execSQL("CREATE INDEX IF NOT EXISTS `index_repository_extension_repoId` ON `repository_extension` (`repoId`)")
			database.setTransactionSuccessful()
		} finally {
			database.endTransaction()
		}

		// Extension Libs
		database.beginTransaction()
		try {
			database.execSQL("ALTER TABLE `libs` RENAME TO `libs_old`")
			database.execSQL("CREATE TABLE IF NOT EXISTS `libs` (`scriptName` TEXT NOT NULL, `version` TEXT NOT NULL, `repoID` INTEGER NOT NULL, PRIMARY KEY(`scriptName`))")
			database.execSQL("INSERT INTO `libs` SELECT * FROM `libs_old`")
			database.execSQL("DROP TABLE IF EXISTS `libs_old`")
			database.setTransactionSuccessful()
		} finally {
			database.endTransaction()
		}

	}

}