package app.shosetsu.android.datasource.local.database

import app.shosetsu.android.datasource.local.database.impl.*
import app.shosetsu.common.datasource.database.base.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * 01 / 01 / 2021
 */
val dbDataSourceModule = DI.Module("database_data_source") {
	bind<IDBChaptersDataSource>() with singleton { DBChaptersDataSource(instance()) }
	bind<IDBDownloadsDataSource>() with singleton { DBDownloadsDataSource(instance()) }

	bind<IDBExtensionsDataSource>() with singleton { DBExtensionsDataSource(instance()) }

	bind<IDBExtLibDataSource>() with singleton { DBExtLibDataSource(instance()) }

	bind<IDBNovelsDataSource>() with singleton { DBNovelsDataSource(instance()) }

	bind<IDBExtRepoDataSource>() with singleton { DBExtRepoDataSource(instance()) }

	bind<IDBUpdatesDataSource>() with singleton { DBUpdatesDataSource(instance()) }

	bind<IDBNovelSettingsDataSource>() with singleton { DBNovelSettingsDataSource(instance()) }
	bind<IDBNovelReaderSettingsDataSource>() with singleton {
		DBNovelReaderSettingsDataSource(
			instance()
		)
	}

}