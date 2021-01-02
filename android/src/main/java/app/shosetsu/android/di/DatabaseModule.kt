package app.shosetsu.android.di

import app.shosetsu.android.providers.database.ShosetsuDatabase
import app.shosetsu.android.providers.database.ShosetsuDatabase.Companion.getRoomDatabase
import app.shosetsu.android.providers.database.dao.*
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
 * 23 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */

val databaseModule: Kodein.Module = Kodein.Module("database_module") {
	bind<ShosetsuDatabase>() with singleton { getRoomDatabase(instance()) }

	bind<ChaptersDao>() with singleton { instance<ShosetsuDatabase>().chaptersDao }
	bind<DownloadsDao>() with singleton { instance<ShosetsuDatabase>().downloadsDao }
	bind<ExtensionLibraryDao>() with singleton { instance<ShosetsuDatabase>().extensionLibraryDao }
	bind<ExtensionsDao>() with singleton { instance<ShosetsuDatabase>().extensionsDao }
	bind<NovelsDao>() with singleton { instance<ShosetsuDatabase>().novelsDao }
	bind<NovelSettingsDao>() with singleton { instance<ShosetsuDatabase>().novelSettingsDao }
	bind<RepositoryDao>() with singleton { instance<ShosetsuDatabase>().repositoryDao }
	bind<UpdatesDao>() with singleton { instance<ShosetsuDatabase>().updatesDao }
}