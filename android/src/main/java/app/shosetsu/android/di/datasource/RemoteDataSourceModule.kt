package app.shosetsu.android.di.datasource

import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.android.datasource.remote.impl.*
import app.shosetsu.common.datasource.remote.base.*
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
 * These modules load chapters from online
 */
val remoteDataSouceModule: Kodein.Module = Kodein.Module("remote_data_source_module") {
	bind<IRemoteCatalogueDataSource>() with singleton { RemoteCatalogueDataSource() }

	bind<IRemoteChaptersDataSource>() with singleton { RemoteChaptersDataSource() }

	bind<IRemoteNovelDataSource>() with singleton { RemoteNovelDataSource() }

	bind<IRemoteExtensionDataSource>() with singleton { RemoteExtensionDataSource(instance()) }

	bind<IRemoteExtRepoDataSource>() with singleton { RemoteExtRepoDataSource(instance()) }

	bind<IRemoteExtLibDataSource>() with singleton { RemoteExtLibDataSource(instance()) }

	bind<IRemoteAppUpdateDataSource>() with singleton { RemoteAppUpdateDataSource(instance()) }
}