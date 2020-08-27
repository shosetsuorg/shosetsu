package com.github.doomsdayrs.apps.shosetsu.di

import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.*
import com.github.doomsdayrs.apps.shosetsu.viewmodel.model.*
import com.github.doomsdayrs.apps.shosetsu.viewmodel.model.catalog.CatalogOptionsViewModel
import com.github.doomsdayrs.apps.shosetsu.viewmodel.model.catalog.CatalogViewModel
import com.github.doomsdayrs.apps.shosetsu.viewmodel.model.extension.ExtensionsConfigureViewModel
import com.github.doomsdayrs.apps.shosetsu.viewmodel.model.extension.ExtensionsViewModel
import com.github.doomsdayrs.apps.shosetsu.viewmodel.model.novel.NovelChaptersViewModel
import com.github.doomsdayrs.apps.shosetsu.viewmodel.model.novel.NovelInfoViewModel
import com.github.doomsdayrs.apps.shosetsu.viewmodel.model.novel.NovelViewModel
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import org.kodein.di.generic.instance as i

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
 */
val viewModelsModule: Kodein.Module = Kodein.Module("view_models_module") {
	// Main
	bind<IMainViewModel>() with provider {
		MainViewModel(i(), i(), i())
	}

	// Library
	bind<ILibraryViewModel>() with provider { LibraryViewModel(i(), i()) }

	// Other
	bind<IDownloadsViewModel>() with provider {
		DownloadsViewModel(i(), i(), i(), i(), i())
	}
	bind<ISearchViewModel>() with provider { SearchViewModel(i(), i()) }
	bind<IUpdatesViewModel>() with provider { UpdatesViewModel(i()) }

	// Catalog(s)
	bind<ICatalogOptionsViewModel>() with provider { CatalogOptionsViewModel(i()) }
	bind<ICatalogViewModel>() with provider { CatalogViewModel(i(), i(), i()) }

	// Extensions
	bind<IExtensionsViewModel>() with provider { ExtensionsViewModel(i(), i(), i(), i(), i()) }
	bind<IExtensionsConfigureViewModel>() with provider { ExtensionsConfigureViewModel(i(), i()) }

	// Novel View
	bind<INovelViewModel>() with provider { NovelViewModel(i()) }
	bind<INovelInfoViewModel>() with provider { NovelInfoViewModel(i(), i(), i()) }
	bind<INovelChaptersViewModel>() with provider { NovelChaptersViewModel(i(), i(), i(), i(), i()) }

	// Chapter
	bind<IChapterReaderViewModel>() with provider { ChapterReaderViewModel(i(), i(), i(), i(), i()) }
}