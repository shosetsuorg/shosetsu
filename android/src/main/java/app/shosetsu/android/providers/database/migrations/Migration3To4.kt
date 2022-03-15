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
object Migration3To4 : Migration(3, 4) {
	@Throws(SQLException::class)
	override fun migrate(database: SupportSQLiteDatabase) {
		// Migrate extensions
		run {
			val tableName = "extensions"

			// Create new table
			database.execSQL("CREATE TABLE IF NOT EXISTS `${tableName}_new` (`id` INTEGER NOT NULL, `repoID` INTEGER NOT NULL, `name` TEXT NOT NULL, `fileName` TEXT NOT NULL, `imageURL` TEXT, `lang` TEXT NOT NULL, `enabled` INTEGER NOT NULL, `installed` INTEGER NOT NULL, `installedVersion` TEXT, `repositoryVersion` TEXT NOT NULL, `chapterType` INTEGER NOT NULL, `md5` TEXT NOT NULL, `type` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`repoID`) REFERENCES `repositories`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")

			// Migrate
			database.execSQL(
				"""
INSERT INTO `${tableName}_new` 
SELECT 
	`id`, 
	`repoID`, 
	`name`, 
	`fileName`,
	`imageURL`, 
	`lang`, 
	`enabled`, 
	`installed`, 
	`installedVersion`,
	`repositoryVersion`,
	0,
	`md5`,
	0 
FROM `$tableName`;
									"""
			)

			// Drop
			database.execSQL("DROP TABLE $tableName")

			// Rename table_new to table
			database.execSQL("ALTER TABLE `${tableName}_new` RENAME TO `${tableName}`")

			// Create indexes
			database.execSQL("CREATE INDEX IF NOT EXISTS `index_extensions_repoID` ON `${tableName}` (`repoID`)")

		}

		// Migrate extension libraries
		run {
			val tableName = "libs"

			// Create new table
			database.execSQL("CREATE TABLE IF NOT EXISTS `${tableName}_new` (`scriptName` TEXT NOT NULL, `version` TEXT NOT NULL, `repoID` INTEGER NOT NULL, PRIMARY KEY(`scriptName`), FOREIGN KEY(`repoID`) REFERENCES `repositories`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")

			// Migrate
			database.execSQL("INSERT INTO `${tableName}_new` SELECT * FROM `$tableName`")

			// Drop
			database.execSQL("DROP TABLE $tableName")

			// Rename table_new to table
			database.execSQL("ALTER TABLE `${tableName}_new` RENAME TO `${tableName}`")

			// Create indexes
			database.execSQL("CREATE INDEX IF NOT EXISTS `index_libs_repoID` ON `${tableName}` (`repoID`)")
		}
	}
}