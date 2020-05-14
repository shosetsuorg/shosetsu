package com.github.doomsdayrs.apps.shosetsu.di

import com.github.doomsdayrs.apps.shosetsu.viewmodel.*
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.*
import com.github.doomsdayrs.apps.shosetsu.viewmodel.settings.*
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
 */


/**
 * shosetsu
 * 01 / 05 / 2020
 */
val viewModelsModule = Kodein.Module("view_models_module") {

	// Other
	bind<ILibraryViewModel>() with provider { LibraryViewModel(instance(), instance()) }
	bind<IDownloadsViewModel>() with provider { DownloadsViewModel(instance()) }
	bind<ISearchViewModel>() with provider { SearchViewModel(instance(), instance()) }
	bind<IUpdatesViewModel>() with provider { UpdatesViewModel(instance()) }

	// Catalog(s)
	bind<ICatalogsViewModel>() with provider { CatalogsViewModel(instance()) }
	bind<ICatalogViewModel>() with provider { CatalogViewModel() }

	// Extensions
	bind<IExtensionsViewModel>() with provider {
		ExtensionsViewModel(instance(), instance(), instance(), instance())
	}
	bind<IExtensionsConfigureViewModel>() with provider {
		ExtensionsConfigureViewModel(instance(), instance())
	}

	// Settings
	bind<ISettingsAdvancedViewModel>() with provider { SettingsAdvancedViewModel(instance()) }
	bind<ISettingsBackupViewModel>() with provider { SettingsBackupViewModel(instance(), instance()) }
	bind<ISettingsDownloadViewModel>() with provider { SettingsDownloadViewModel(instance()) }
	bind<ISettingsInfoViewModel>() with provider { SettingsInfoViewModel() }
	bind<ISettingsReaderViewModel>() with provider { SettingsReaderViewModel(instance()) }
	bind<ISettingsViewViewModel>() with provider { SettingsViewViewModel(instance()) }
}