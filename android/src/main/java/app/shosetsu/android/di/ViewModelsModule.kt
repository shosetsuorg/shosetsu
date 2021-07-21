package app.shosetsu.android.di

import app.shosetsu.android.viewmodel.abstracted.*
import app.shosetsu.android.viewmodel.abstracted.settings.*
import app.shosetsu.android.viewmodel.impl.*
import app.shosetsu.android.viewmodel.impl.extension.ExtensionConfigureViewModel
import app.shosetsu.android.viewmodel.impl.extension.ExtensionsViewModel
import app.shosetsu.android.viewmodel.impl.settings.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

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
val viewModelsModule: DI.Module = DI.Module("view_models_module") {
	// Main
	bind<AMainViewModel>() with provider {
		MainViewModel(
			startDownloadWorkerUseCase = instance(),
			loadAppUpdateFlowLiveUseCase = instance(),
			isOnlineUseCase = instance(),
			shareUseCase = instance(),
			loadNavigationStyleUseCase = instance(),
			reportExceptionUseCase = instance(),
			loadLiveAppThemeUseCase = instance(),
			startInstallWorker = instance(),
			canAppSelfUpdateUseCase = instance(),
			loadAppUpdateUseCase = instance(),
			loadRequireDoubleBackUseCase = instance()
		)
	}

	// Library
	bind<ALibraryViewModel>() with provider {
		LibraryViewModel(
			libraryAsCardsUseCase = instance(),
			updateBookmarkedNovelUseCase = instance(),
			isOnlineUseCase = instance(),
			startUpdateWorkerUseCase = instance(),
			reportExceptionUseCase = instance(),
			loadNovelUITypeUseCase = instance(),
			loadNovelUIColumnsHUseCase = instance(),
			loadNovelUIColumnsPUseCase = instance(),
			setNovelUITypeUseCase = instance()
		)
	}

	// Other
	bind<ADownloadsViewModel>() with provider {
		DownloadsViewModel(
			getDownloadsUseCase = instance(),
			startDownloadWorkerUseCase = instance(),
			updateDownloadUseCase = instance(),
			deleteDownloadUseCase = instance(),
			settings = instance(),
			isOnlineUseCase = instance(),
			reportExceptionUseCase = instance()
		)
	}
	bind<ASearchViewModel>() with provider {
		SearchViewModel(
			searchBookMarkedNovelsUseCase = instance(),
			loadSearchRowUIUseCase = instance(),
			loadCatalogueQueryDataUseCase = instance(),
			reportExceptionUseCase = instance()
		)
	}
	bind<AUpdatesViewModel>() with provider {
		UpdatesViewModel(
			getUpdatesUseCase = instance(),
			reportExceptionUseCase = instance(),
			startUpdateWorkerUseCase = instance(),
			isOnlineUseCase = instance()
		)
	}

	bind<AAboutViewModel>() with provider {
		AboutViewModel(
			openInWebviewUseCase = instance(),
			application = instance(),
			manager = instance()
		)
	}

	// Catalog(s)
	bind<ACatalogViewModel>() with provider {
		CatalogViewModel(
			application = instance(),
			getExtensionUseCase = instance(),
			backgroundAddUseCase = instance(),
			getCatalogueListingData = instance(),
			loadCatalogueQueryDataUseCase = instance(),
			reportExceptionUseCase = instance(),
			loadNovelUITypeUseCase = instance(),
			loadNovelUIColumnsHUseCase = instance(),
			loadNovelUIColumnsPUseCase = instance(),
			instance()
		)
	}

	// Extensions
	bind<ABrowseViewModel>() with provider {
		ExtensionsViewModel(
			getExtensionsUIUseCase = instance(),
			installExtensionUIUseCase = instance(),
			uninstallExtensionUIUseCase = instance(),
			isOnlineUseCase = instance(),
			reportExceptionUseCase = instance(),
			startRepositoryUpdateManagerUseCase = instance()
		)
	}
	bind<AExtensionConfigureViewModel>() with provider {
		ExtensionConfigureViewModel(
			application = instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			instance()
		)
	}

	// Novel View
	bind<ANovelViewModel>() with provider {
		NovelViewModel(
			getChapterUIsUseCase = instance(),
			loadNovelUIUseCase = instance(),
			reportExceptionUseCase = instance(),
			updateNovelUseCase = instance(),
			openInBrowserUseCase = instance(),
			openInWebviewUseCase = instance(),
			shareUseCase = instance(),
			loadRemoteNovel = instance(),
			isOnlineUseCase = instance(),
			updateChapterUseCase = instance(),
			downloadChapterPassageUseCase = instance(),
			deleteChapterPassageUseCase = instance(),
			isChaptersResumeFirstUnread = instance(),
			getNovelSettingFlowUseCase = instance(),
			updateNovelSettingUseCase = instance(),
			loadDeletePreviousChapterUseCase = instance(),
			startDownloadWorkerUseCase = instance(),
			startDownloadWorkerAfterUpdateUseCase = instance()
		)
	}

	// Chapter
	bind<AChapterReaderViewModel>() with provider {
		ChapterReaderViewModel(
			application = instance(),
			settingsRepo = instance(),
			loadReaderChaptersUseCase = instance(),
			loadChapterPassageUseCase = instance(),
			updateReaderChapterUseCase = instance(),
			reportExceptionUseCase = instance(),
			getReaderSettingsUseCase = instance(),
			updateReaderSettingUseCase = instance()
		)
	}
	bind<ARepositoryViewModel>() with provider {
		RepositoryViewModel(
			loadRepositoriesUseCase = instance(),
			reportExceptionUseCase = instance(),
			addRepositoryUseCase = instance(),
			deleteRepositoryUseCase = instance(),
			updateRepositoryUseCase = instance(),
			startRepositoryUpdateManagerUseCase = instance(),
			forceInsertRepositoryUseCase = instance(),
			isOnlineUseCase = instance()
		)
	}


	// Settings
	bind<AAdvancedSettingsViewModel>() with provider {
		AdvancedSettingsViewModel(
			iSettingsRepository = instance(),
			context = instance(),
			reportExceptionUseCase = instance(),
			purgeNovelCacheUseCase = instance()
		)
	}
	bind<ABackupSettingsViewModel>() with provider {
		BackupSettingsViewModel(
			iSettingsRepository = instance(),
			reportExceptionUseCase = instance(),
			manager = instance(),
			startBackupWorkerUseCase = instance(),
			loadInternalBackupNamesUseCase = instance(),
			startRestoreWorkerUseCase = instance()
		)
	}
	bind<ADownloadSettingsViewModel>() with provider {
		DownloadSettingsViewModel(
			iSettingsRepository = instance(),
			instance()
		)
	}
	bind<AReaderSettingsViewModel>() with provider {
		ReaderSettingsViewModel(
			iSettingsRepository = instance(),
			app = instance(),
			reportExceptionUseCase = instance(),
			loadReaderThemes = instance()
		)
	}
	bind<AUpdateSettingsViewModel>() with provider {
		UpdateSettingsViewModel(
			iSettingsRepository = instance(),
			instance(),
			instance(),
			instance()
		)
	}
	bind<AViewSettingsViewModel>() with provider {
		ViewSettingsViewModel(
			iSettingsRepository = instance(),
			application = instance(),
			reportExceptionUseCase = instance()
		)
	}
	bind<ASplashScreenViewModel>() with provider {
		SplashScreenViewModel(
			settingsRepository = instance()
		)
	}
	bind<ATextAssetReaderViewModel>() with provider {
		TextAssetReaderViewModel(instance())
	}

}