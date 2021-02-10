package app.shosetsu.android.di

import app.shosetsu.android.viewmodel.abstracted.*
import app.shosetsu.android.viewmodel.abstracted.settings.*
import app.shosetsu.android.viewmodel.impl.*
import app.shosetsu.android.viewmodel.impl.extension.ExtensionConfigureViewModel
import app.shosetsu.android.viewmodel.impl.extension.ExtensionsViewModel
import app.shosetsu.android.viewmodel.impl.novel.NovelViewModel
import app.shosetsu.android.viewmodel.impl.settings.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
@ExperimentalCoroutinesApi
val viewModelsModule: Kodein.Module = Kodein.Module("view_models_module") {
	// Main
	bind<IMainViewModel>() with provider {
		MainViewModel(i(), i(), i(), i(), i(), i(), i(), i(), i(), i())
	}

	// Library
	bind<ILibraryViewModel>() with provider {
		LibraryViewModel(
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i()
		)
	}

	// Other
	bind<IDownloadsViewModel>() with provider {
		DownloadsViewModel(i(), i(), i(), i(), i(), i(), i())
	}
	bind<ISearchViewModel>() with provider { SearchViewModel(i(), i(), i(), i()) }
	bind<IUpdatesViewModel>() with provider { UpdatesViewModel(i(), i(), i(), i()) }

	bind<AAboutViewModel>() with provider { AboutViewModel(i(), i(), i()) }

	// Catalog(s)
	bind<ICatalogViewModel>() with provider {
		CatalogViewModel(
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i()
		)
	}

	// Extensions
	bind<IExtensionsViewModel>() with provider {
		ExtensionsViewModel(
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i()
		)
	}
	bind<IExtensionConfigureViewModel>() with provider {
		ExtensionConfigureViewModel(
			i(),
			i(),
			i(),
			i(),
			i()
		)
	}

	// Novel View
	bind<INovelViewModel>() with provider {
		NovelViewModel(
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i(),
			i()
		)
	}

	// Chapter
	bind<IChapterReaderViewModel>() with provider {
		ChapterReaderViewModel(
			i(),
			i(),
			i(),
			i(),
			i(),
			i()
		)
	}
	bind<ARepositoryViewModel>() with provider { RepositoryViewModel(i(), i(), i(), i()) }


	// Settings
	bind<AAdvancedSettingsViewModel>() with provider {
		AdvancedSettingsViewModel(
			i(),
			i(),
			i(),
			i()
		)
	}
	bind<ABackupSettingsViewModel>() with provider { BackupSettingsViewModel(i(), i(), i(), i()) }
	bind<ADownloadSettingsViewModel>() with provider { DownloadSettingsViewModel(i(), i()) }
	bind<AReaderSettingsViewModel>() with provider { ReaderSettingsViewModel(i(), i(), i(), i()) }
	bind<AUpdateSettingsViewModel>() with provider { UpdateSettingsViewModel(i(), i()) }
	bind<AViewSettingsViewModel>() with provider { ViewSettingsViewModel(i(), i(), i()) }
	bind<ASplashScreenViewModel>() with provider { SplashScreenViewModel(i()) }

}