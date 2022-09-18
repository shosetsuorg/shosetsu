package app.shosetsu.android.di

import app.shosetsu.android.viewmodel.abstracted.*
import app.shosetsu.android.viewmodel.abstracted.settings.*
import app.shosetsu.android.viewmodel.impl.*
import app.shosetsu.android.viewmodel.impl.extension.ExtensionConfigureViewModel
import app.shosetsu.android.viewmodel.impl.extension.ExtensionsViewModel
import app.shosetsu.android.viewmodel.impl.settings.*
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
val viewModelsModule: DI.Module = DI.Module("view_models_module") {
	// Main
	bind<AMainViewModel>() with provider {
		MainViewModel(
			loadAppUpdateFlowLiveUseCase = instance(),
			isOnlineUseCase = instance(),
			loadNavigationStyleUseCase = instance(),
			loadLiveAppThemeUseCase = instance(),
			startInstallWorker = instance(),
			canAppSelfUpdateUseCase = instance(),
			loadAppUpdateUseCase = instance(),
			loadRequireDoubleBackUseCase = instance(),
			loadBackupProgress = instance(),
			settingsRepository = instance()
		)
	}

	// Library
	bind<ALibraryViewModel>() with provider {
		LibraryViewModel(
			libraryAsCardsUseCase = instance(),
			updateBookmarkedNovelUseCase = instance(),
			isOnlineUseCase = instance(),
			startUpdateWorkerUseCase = instance(),
			loadNovelUITypeUseCase = instance(),
			setNovelUITypeUseCase = instance(),
			setNovelsCategoriesUseCase = instance(),
			loadNovelUIColumnsH = instance(),
			loadNovelUIColumnsP = instance(),
			loadNovelUIBadgeToast = instance()
		)
	}

	// Other
	bind<ADownloadsViewModel>() with provider {
		DownloadsViewModel(
			getDownloadsUseCase = instance(),
			startDownloadWorkerUseCase = instance(),
			settings = instance(),
			isOnlineUseCase = instance(),
			downloadsRepository = instance()
		)
	}
	bind<ASearchViewModel>() with provider {
		SearchViewModel(
			searchBookMarkedNovelsUseCase = instance(),
			loadSearchRowUIUseCase = instance(),
			loadCatalogueQueryDataUseCase = instance(),
			getExtensionUseCase = instance(),
			loadNovelUITypeUseCase = instance()
		)
	}
	bind<AUpdatesViewModel>() with provider {
		UpdatesViewModel(
			getUpdatesUseCase = instance(),
			startUpdateWorkerUseCase = instance(),
			isOnlineUseCase = instance(),
			updateChapterUseCase = instance()
		)
	}

	bind<AAboutViewModel>() with provider {
		AboutViewModel(
			manager = instance()
		)
	}

	bind<AAddShareViewModel>() with provider {
		AddShareViewModel(
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			instance()
		)
	}

	// Catalog(s)
	bind<ACatalogViewModel>() with provider {
		CatalogViewModel(
			getExtensionUseCase = instance(),
			backgroundAddUseCase = instance(),
			getCatalogueListingData = instance(),
			loadCatalogueQueryDataUseCase = instance(),

			loadNovelUITypeUseCase = instance(),
			loadNovelUIColumnsHUseCase = instance(),
			loadNovelUIColumnsPUseCase = instance(),
			setNovelUIType = instance(),
			getCategoriesUseCase = instance(),
			setNovelCategoriesUseCase = instance()
		)
	}

	// Catalog(s)
	bind<ACategoriesViewModel>() with provider {
		CategoriesViewModel(
			instance(),
			instance(),
			instance(),
			instance()
		)
	}

	// Extensions
	bind<ABrowseViewModel>() with provider {
		ExtensionsViewModel(
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
		)
	}
	bind<AExtensionConfigureViewModel>() with provider {
		ExtensionConfigureViewModel(
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			instance(),
		)
	}

	// Novel View
	bind<ANovelViewModel>() with provider {
		NovelViewModel(
			getChapterUIsUseCase = instance(),
			loadNovelUIUseCase = instance(),

			updateNovelUseCase = instance(),
			loadRemoteNovel = instance(),
			isOnlineUseCase = instance(),
			downloadChapterPassageUseCase = instance(),
			deleteChapterPassageUseCase = instance(),
			isChaptersResumeFirstUnread = instance(),
			getNovelSettingFlowUseCase = instance(),
			updateNovelSettingUseCase = instance(),
			loadDeletePreviousChapterUseCase = instance(),
			startDownloadWorkerUseCase = instance(),
			startDownloadWorkerAfterUpdateUseCase = instance(),
			getContentURL = instance(),
			getLastReadChapter = instance(),
			getTrueDelete = instance(),
			trueDeleteChapter = instance(),
			getInstalledExtensionUseCase = instance(),
			getRepositoryUseCase = instance(),
			chapterRepo = instance(),
			getCategoriesUseCase = instance(),
			getNovelCategoriesUseCase = instance(),
			setNovelCategoriesUseCase = instance()
		)
	}

	// Chapter
	bind<AChapterReaderViewModel>() with provider {
		ChapterReaderViewModel(
			settingsRepo = instance(),
			instance(),
			instance(),
			instance(),
			instance(),
			loadReaderChaptersUseCase = instance(),
			loadChapterPassageUseCase = instance(),

			getReaderSettingsUseCase = instance(),
			instance(),
			instance(),
			instance(),
		)
	}
	bind<ARepositoryViewModel>() with provider {
		RepositoryViewModel(
			loadRepositoriesUseCase = instance(),

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
			purgeNovelCacheUseCase = instance(),
			instance(),
			instance(),
			instance(),
			instance()
		)
	}
	bind<ABackupSettingsViewModel>() with provider {
		BackupSettingsViewModel(
			iSettingsRepository = instance(),

			manager = instance(),
			startBackupWorkerUseCase = instance(),
			loadInternalBackupNamesUseCase = instance(),
			instance(),
			instance()
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

			loadReaderThemes = instance()
		)
	}
	bind<AUpdateSettingsViewModel>() with provider {
		UpdateSettingsViewModel(
			iSettingsRepository = instance(),
			instance(),
			instance(),
			instance(),
			instance()
		)
	}
	bind<AViewSettingsViewModel>() with provider {
		ViewSettingsViewModel(
			iSettingsRepository = instance(),
		)
	}
	bind<ATextAssetReaderViewModel>() with provider {
		TextAssetReaderViewModel(instance())
	}

	bind<AMigrationViewModel>() with provider {
		MigrationViewModel(instance(), instance(), instance())
	}

	bind<ACSSEditorViewModel>() with provider {
		CSSEditorViewModel(instance(), instance())
	}

}