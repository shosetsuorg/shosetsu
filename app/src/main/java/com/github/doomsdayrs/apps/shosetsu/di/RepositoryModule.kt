package com.github.doomsdayrs.apps.shosetsu.di

import com.github.doomsdayrs.apps.shosetsu.domain.repository.model.DownloadsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.model.ExtensionsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.model.NovelsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.model.UpdatesRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IDownloadsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtensionsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IUpdatesRepository
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
	bind<IDownloadsRepository>() with singleton { DownloadsRepository(instance()) }
	bind<IExtensionsRepository>() with singleton { ExtensionsRepository(instance()) }
	bind<INovelsRepository>() with singleton { NovelsRepository(instance()) }
	bind<IUpdatesRepository>() with singleton { UpdatesRepository(instance()) }
}