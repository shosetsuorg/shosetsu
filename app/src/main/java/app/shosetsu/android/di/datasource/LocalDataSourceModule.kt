package app.shosetsu.android.di.datasource

import app.shosetsu.android.datasource.database.base.*
import app.shosetsu.android.datasource.database.model.*
import app.shosetsu.android.datasource.file.base.IFileSettingsDataSource
import app.shosetsu.android.datasource.file.model.FileSharedPreferencesSettingsDataSource
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

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
val localDataSouceModule: Kodein.Module = Kodein.Module("local_data_source_module") {


	bind<ILocalChaptersDataSource>() with singleton { LocalChaptersDataSource(instance()) }
	bind<ILocalDownloadsDataSource>() with singleton { LocalDownloadsDataSource(instance()) }

	bind<ILocalExtensionsDataSource>() with singleton { LocalExtensionsDataSource(instance()) }

	bind<ILocalExtLibDataSource>() with singleton { LocalExtLibDataSource(instance()) }

	bind<ILocalNovelsDataSource>() with singleton { LocalNovelsDataSource(instance()) }

	bind<ILocalExtRepoDataSource>() with singleton { LocalExtRepoDataSource(instance()) }

	bind<ILocalUpdatesDataSource>() with singleton { LocalUpdatesDataSource(instance()) }

	bind<IFileSettingsDataSource>() with singleton { FileSharedPreferencesSettingsDataSource(instance()) }
}