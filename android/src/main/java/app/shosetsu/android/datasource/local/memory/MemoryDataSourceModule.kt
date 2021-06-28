package app.shosetsu.android.datasource.local.memory

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import app.shosetsu.android.datasource.local.memory.impl.GuavaMemChaptersDataSource
import app.shosetsu.android.datasource.local.memory.impl.GuavaMemExtLibDataSource
import app.shosetsu.android.datasource.local.memory.impl.GuavaMemExtensionDataSource
import app.shosetsu.common.datasource.memory.base.IMemChaptersDataSource
import app.shosetsu.common.datasource.memory.base.IMemExtLibDataSource
import app.shosetsu.common.datasource.memory.base.IMemExtensionsDataSource
import app.shosetsu.common.datasource.memory.impl.GenericMemChaptersDataSource
import app.shosetsu.common.datasource.memory.impl.GenericMemExtLibDataSource
import app.shosetsu.common.datasource.memory.impl.GenericMemExtensionDataSource
import org.kodein.di.DI
import org.kodein.di.bind
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
 * 04 / 05 / 2020
 * These modules handle cached data that is in memory
 */

val memoryDataSourceModule: DI.Module = DI.Module("cache_data_source") {
	bind<IMemChaptersDataSource>() with singleton {
		if (SDK_INT <= M)
			GenericMemChaptersDataSource() else
			GuavaMemChaptersDataSource()
	}

	bind<IMemExtensionsDataSource>() with singleton {
		if (SDK_INT <= M)
			GenericMemExtensionDataSource() else
			GuavaMemExtensionDataSource()
	}

	bind<IMemExtLibDataSource>() with singleton {
		if (SDK_INT <= M)
			GenericMemExtLibDataSource() else
			GuavaMemExtLibDataSource()
	}

}