package app.shosetsu.android.di

import app.shosetsu.android.viewmodel.abstracted.*
import app.shosetsu.android.viewmodel.abstracted.settings.AInfoSettingsViewModel
import app.shosetsu.android.viewmodel.model.*
import app.shosetsu.android.viewmodel.model.CatalogViewModel
import app.shosetsu.android.viewmodel.model.extension.ExtensionConfigureViewModel
import app.shosetsu.android.viewmodel.model.extension.ExtensionsViewModel
import app.shosetsu.android.viewmodel.model.novel.NovelChaptersViewModel
import app.shosetsu.android.viewmodel.model.novel.NovelInfoViewModel
import app.shosetsu.android.viewmodel.model.novel.NovelViewModel
import app.shosetsu.android.viewmodel.model.settings.InfoSettingsViewModel
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
		MainViewModel(i(), i(), i(), i(), i())
	}

	// Library
	bind<ILibraryViewModel>() with provider { LibraryViewModel(i(), i(), i(), i()) }

	// Other
	bind<IDownloadsViewModel>() with provider {
		DownloadsViewModel(i(), i(), i(), i(), i(), i())
	}
	bind<ISearchViewModel>() with provider { SearchViewModel(i(), i(), i()) }
	bind<IUpdatesViewModel>() with provider { UpdatesViewModel(i()) }

	// Catalog(s)
	bind<ICatalogViewModel>() with provider { CatalogViewModel(i(), i(), i(), i(), i()) }

	// Extensions
	bind<IExtensionsViewModel>() with provider { ExtensionsViewModel(i(), i(), i(), i(), i(), i()) }
	bind<IExtensionConfigureViewModel>() with provider { ExtensionConfigureViewModel(i(), i(), i(), i()) }

	// Novel View
	bind<INovelViewModel>() with provider { NovelViewModel(i(), i()) }
	bind<INovelInfoViewModel>() with provider { NovelInfoViewModel(i(), i(), i(), i(), i(), i()) }
	bind<INovelChaptersViewModel>() with provider {
		NovelChaptersViewModel(i(), i(), i(), i(), i(), i(), i())
	}

	// Chapter
	bind<IChapterReaderViewModel>() with provider { ChapterReaderViewModel(i(), i(), i(), i()) }


	bind<AInfoSettingsViewModel>() with provider { InfoSettingsViewModel(i()) }

	bind<ARepositoryViewModel>() with provider { RepositoryViewModel(i()) }
}