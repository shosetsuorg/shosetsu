package com.github.doomsdayrs.apps.shosetsu.di

import com.github.doomsdayrs.apps.shosetsu.providers.database.ShosetsuDatabase
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.*
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

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
 * 23 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */

val databaseModule = Kodein.Module("database_module") {
	bind<ChaptersDao>() with provider { instance<ShosetsuDatabase>().chaptersDao() }
	bind<NovelsDao>() with provider { instance<ShosetsuDatabase>().novelsDao() }
	bind<DownloadsDao>() with provider { instance<ShosetsuDatabase>().downloadsDao() }
	bind<UpdatesDao>() with provider { instance<ShosetsuDatabase>().updatesDao() }
	bind<ExtensionLibraryDao>() with provider { instance<ShosetsuDatabase>().scriptLibDao() }
	bind<RepositoryDao>() with provider { instance<ShosetsuDatabase>().repositoryDao() }
	bind<ExtensionsDao>() with provider { instance<ShosetsuDatabase>().extensionsDao() }

}