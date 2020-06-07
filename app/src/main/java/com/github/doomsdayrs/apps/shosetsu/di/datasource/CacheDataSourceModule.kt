package com.github.doomsdayrs.apps.shosetsu.di.datasource

import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheChaptersDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheExtLibDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheExtensionsDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.model.CacheChaptersDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.model.CacheExtLibDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.model.CacheExtensionDataSource
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
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
 * 04 / 05 / 2020
 * These modules handle cached data that is in memory
 */
val cacheDataSouceModule: Kodein.Module = Kodein.Module("cache_data_source_module") {
	bind<ICacheChaptersDataSource>() with singleton { CacheChaptersDataSource() }

	bind<ICacheExtensionsDataSource>() with singleton { CacheExtensionDataSource() }

	bind<ICacheExtLibDataSource>() with singleton { CacheExtLibDataSource() }
}