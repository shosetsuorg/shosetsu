package app.shosetsu.android.providers.database.migrations

import android.database.Cursor
import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.shosetsu.android.common.ext.logID

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
 * SQLite does not support many of the alter table functions,
 * so this class provides workarounds to delete/ update a column
 * @author dittrich
 */
abstract class RemoveMigration(from: Int, to: Int) : Migration(from, to) {

	/**
	 * deletes a column from a given table.
	 * Its expected from the user, that u handle the transaction management by yourself
	 * @param database the apps database
	 * @param tableName table you want to alter
	 */
	fun deleteColumnFromTable(database: SupportSQLiteDatabase, tableName: String, vararg colName: String?) {

		val backupTableName = "data_backup"
		database.execSQL("DROP TABLE IF EXISTS $backupTableName;")
		val columnInfo = database.query("PRAGMA table_info('$tableName')", null)
		val foreignKeys = database.query("PRAGMA foreign_key_list('$tableName')")
		val uniqueIndex = database.query("PRAGMA index_list('$tableName')")
		if (columnInfo.count != 0) {
			var sourceTableInfos = ""
			var targetColumns = ""
			while (columnInfo.moveToNext()) {
				if (!colName.contains(columnInfo.getString(1))) {
					sourceTableInfos = if (sourceTableInfos.isEmpty()) {
						extractColumnInfo(columnInfo)
					} else {
						"$sourceTableInfos ,${extractColumnInfo(columnInfo)}"
					}

					targetColumns = if (targetColumns.isEmpty()) {
						"`${columnInfo.getString(1)}`"
					} else {
						"$targetColumns, `${columnInfo.getString(1)}`"
					}
				}
				if (columnInfo.isLast) {
					var createBackupTableSQL = "CREATE TABLE IF NOT EXISTS  $backupTableName($sourceTableInfos"

					while (foreignKeys.moveToNext()) {
						createBackupTableSQL += ",FOREIGN KEY (${foreignKeys.getString(3)}) REFERENCES ${foreignKeys.getString(2)}(${foreignKeys.getString(4)}) ON UPDATE ${foreignKeys.getString(5)} ON DELETE ${foreignKeys.getString(6)}"
					}
					createBackupTableSQL += ");"
					Log.i(logID(), "Creating Backup Table with sql: $createBackupTableSQL")
					database.execSQL(createBackupTableSQL)

					// Creates index
					val createIndexSql = arrayListOf<String>()
					while (uniqueIndex.moveToNext()) {
						val indexName = uniqueIndex.getString(1)
						val indexInfo = database.query("PRAGMA index_info('$indexName')")
						val unique = if (uniqueIndex.getInt(2) == 1) "UNIQUE" else ""
						indexInfo.moveToNext()
						val parentId = indexInfo.getString(2)
						database.execSQL("DROP INDEX IF EXISTS $indexName")
						createIndexSql.plusAssign("CREATE $unique INDEX $indexName on data_backup('$parentId');")
					}

					if (createIndexSql.isNotEmpty()) {
						createIndexSql.forEach {
							Log.i(logID(), "Creating index: $it")
							database.execSQL(it)
						}
					}

					val insertIntoBackupSQL = "INSERT INTO $backupTableName SELECT $targetColumns FROM $tableName;"

					Log.i(logID(), "Insert into Backup Table with sql: $insertIntoBackupSQL")
					database.execSQL(insertIntoBackupSQL)
					Log.i(logID(), "Dropping table $tableName")
					database.execSQL("DROP TABLE $tableName")
					Log.i(logID(), "Renaming Backup table to $tableName")
					database.execSQL("ALTER TABLE $backupTableName RENAME TO $tableName;")
				}
			}
		}
	}

	/**
	 * extract the column info from the cursor
	 */
	private fun extractColumnInfo(columnInfo: Cursor): String {
		val columnName = columnInfo.getString(1)
		val columnType = columnInfo.getString(2)
		val notNull = if (columnInfo.getInt(3) == 1) "NOT NULL" else ""
		val defaultValue = columnInfo.getString(4)
		val isPrimaryKey = if (columnInfo.getInt(5) == 0) "" else "PRIMARY KEY"
		return "`$columnName` $columnType $notNull DEFAULT $defaultValue $isPrimaryKey"
	}
}