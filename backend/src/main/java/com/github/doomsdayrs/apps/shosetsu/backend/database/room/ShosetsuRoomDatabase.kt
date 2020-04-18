package com.github.doomsdayrs.apps.shosetsu.backend.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.dao.FormatterDao
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.FormatterEntity

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
@Database(entities = [FormatterEntity::class], version = 1)
abstract class ShosetsuRoomDatabase : RoomDatabase() {
	companion object {
		private lateinit var databaseShosetsu: ShosetsuRoomDatabase;

		fun getRoomDatabase(context: Context): ShosetsuRoomDatabase {
			if (!::databaseShosetsu.isInitialized)
				synchronized(ShosetsuRoomDatabase::class) {
					databaseShosetsu = Room.databaseBuilder(context, ShosetsuRoomDatabase::class.java, "room_database").build()
				}
			return databaseShosetsu
		}
	}

	abstract val formatterDao: FormatterDao

}