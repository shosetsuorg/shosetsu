package app.shosetsu.android.datasource.remote

import app.shosetsu.android.common.enums.ProductFlavors
import app.shosetsu.android.common.utils.flavor
import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.android.datasource.remote.impl.*
import app.shosetsu.android.datasource.remote.impl.update.FDroidAppUpdateDataSource
import app.shosetsu.android.datasource.remote.impl.update.GithubAppUpdateDataSource
import app.shosetsu.android.datasource.remote.impl.update.PlayAppUpdateDataSource
import app.shosetsu.android.datasource.remote.impl.update.UpToDownAppUpdateDataSource
import app.shosetsu.common.datasource.remote.base.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

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
val remoteDataSouceModule: DI.Module = DI.Module("remote_data_source") {
	bind<IRemoteCatalogueDataSource>() with singleton { RemoteCatalogueDataSource() }

	bind<IRemoteChaptersDataSource>() with singleton { RemoteChaptersDataSource() }

	bind<IRemoteNovelDataSource>() with singleton { RemoteNovelDataSource() }

	bind<IRemoteExtensionDataSource>() with singleton { RemoteExtensionDataSource(instance()) }

	bind<IRemoteExtRepoDataSource>() with singleton { RemoteExtRepoDataSource(instance()) }

	bind<IRemoteExtLibDataSource>() with singleton { RemoteExtLibDataSource(instance()) }

	bind<IRemoteAppUpdateDataSource>() with singleton {
		when (flavor()) {
			ProductFlavors.PLAY_STORE -> PlayAppUpdateDataSource()
			ProductFlavors.F_DROID -> FDroidAppUpdateDataSource()
			ProductFlavors.UP_TO_DOWN -> UpToDownAppUpdateDataSource()
			ProductFlavors.STANDARD -> GithubAppUpdateDataSource(instance())
		}
	}
}