package app.shosetsu.android.datasource.local

import app.shosetsu.android.datasource.local.database.dbDataSourceModule
import app.shosetsu.android.datasource.local.file.fileDataSourceModule
import app.shosetsu.android.datasource.local.memory.memoryDataSourceModule
import org.kodein.di.DI

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
 * shosetsu
 * 01 / 05 / 2020
 * Should all be singletons
 */
val localDataSourceModule: DI.Module = DI.Module("local_data_source") {
	import(dbDataSourceModule)
	import(fileDataSourceModule)
	import(memoryDataSourceModule)
}