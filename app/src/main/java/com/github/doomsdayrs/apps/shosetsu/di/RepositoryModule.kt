package com.github.doomsdayrs.apps.shosetsu.di

import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.*
import com.github.doomsdayrs.apps.shosetsu.domain.repository.model.*
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
 * ====================================================================
 */

/**
 * shosetsu
 * 25 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */

val repositoryModule = Kodein.Module("repository_module") {
	bind<IChaptersRepository>() with singleton {
		ChaptersRepository(instance(), instance(), instance(), instance())
	}
	bind<IDownloadsRepository>() with singleton { DownloadsRepository(instance()) }
	bind<IExtensionsRepository>() with singleton {
		ExtensionsRepository(instance(), instance(), instance(), instance(), instance())
	}
	bind<IExtLibRepository>() with singleton {
		ExtLibRepository(instance(), instance(), instance(),instance())
	}
	bind<IExtRepoRepository>() with singleton { ExtRepoRepository(instance(), instance()) }
	bind<INovelsRepository>() with singleton { NovelsRepository(instance(), instance()) }
	bind<IUpdatesRepository>() with singleton { UpdatesRepository(instance()) }
}