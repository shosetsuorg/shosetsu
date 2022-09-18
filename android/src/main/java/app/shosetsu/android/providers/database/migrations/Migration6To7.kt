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
 * @since 08 / 08 / 2022
 */
object Migration6To7 : Migration(6, 7) {

	@Throws(SQLException::class)
	override fun migrate(database: SupportSQLiteDatabase) {
		database.execSQL("CREATE TABLE `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `order` INTEGER NOT NULL)")
		database.execSQL("CREATE TABLE `novel_categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `novelID` INTEGER NOT NULL, `categoryID` INTEGER NOT NULL, FOREIGN KEY(`novelID`) REFERENCES `novels`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`categoryID`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
		database.execSQL("CREATE INDEX `index_novel_categories_categoryID` ON `novel_categories` (`categoryID`)")
		database.execSQL("CREATE INDEX `index_novel_categories_novelID` ON `novel_categories` (`novelID`)")
	}

}