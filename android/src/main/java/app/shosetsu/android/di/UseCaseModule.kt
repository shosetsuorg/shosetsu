package app.shosetsu.android.di

import app.shosetsu.android.domain.usecases.*
import app.shosetsu.android.domain.usecases.delete.DeleteChapterPassageUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteRepositoryUseCase
import app.shosetsu.android.domain.usecases.delete.TrueDeleteChapterUseCase
import app.shosetsu.android.domain.usecases.get.*
import app.shosetsu.android.domain.usecases.load.*
import app.shosetsu.android.domain.usecases.settings.LoadChaptersResumeFirstUnreadUseCase
import app.shosetsu.android.domain.usecases.settings.LoadNavigationStyleUseCase
import app.shosetsu.android.domain.usecases.settings.LoadRequireDoubleBackUseCase
import app.shosetsu.android.domain.usecases.settings.SetNovelUITypeUseCase
import app.shosetsu.android.domain.usecases.start.*
import app.shosetsu.android.domain.usecases.update.*
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
val useCaseModule: DI.Module = DI.Module("useCase") {

	bind<LoadDownloadsUseCase>() with provider { LoadDownloadsUseCase(instance()) }

	bind<LoadLibraryUseCase>() with provider { LoadLibraryUseCase(instance(), instance()) }

	bind<SearchBookMarkedNovelsUseCase>() with provider { SearchBookMarkedNovelsUseCase(instance()) }


	bind<LoadBrowseExtensionsUseCase>() with provider {
		LoadBrowseExtensionsUseCase(
			instance(),
			instance()
		)
	}

	bind<LoadUpdatesUseCase>() with provider { LoadUpdatesUseCase(instance()) }

	bind<StartRepositoryUpdateManagerUseCase>() with provider {
		StartRepositoryUpdateManagerUseCase(instance())
	}

	bind<RequestInstallExtensionUseCase>() with provider {
		RequestInstallExtensionUseCase(
			instance(),
			instance(),
			instance()
		)
	}

	bind<UpdateNovelUseCase>() with provider { UpdateNovelUseCase(instance()) }

	bind<GetExtensionUseCase>() with provider { GetExtensionUseCase(instance(), instance()) }

	bind<NovelBackgroundAddUseCase>() with provider {
		NovelBackgroundAddUseCase(instance(), instance(), instance())
	}

	bind<GetNovelUIUseCase>() with provider { GetNovelUIUseCase(instance(), instance()) }

	bind<GetRemoteNovelUseCase>() with provider {
		GetRemoteNovelUseCase(instance(), instance(), instance(), instance())
	}
	bind<StartDownloadWorkerAfterUpdateUseCase>() with provider {
		StartDownloadWorkerAfterUpdateUseCase(instance(), instance(), instance(), instance())
	}

	bind<GetCatalogueListingDataUseCase>() with provider {
		GetCatalogueListingDataUseCase(instance(), instance())
	}

	bind<GetChapterUIsUseCase>() with provider { GetChapterUIsUseCase(instance()) }

	bind<UpdateChapterUseCase>() with provider { UpdateChapterUseCase(instance()) }

	bind<GetReaderChaptersUseCase>() with provider {
		GetReaderChaptersUseCase(
			instance(),
		)
	}

	bind<GetChapterPassageUseCase>() with provider {
		GetChapterPassageUseCase(instance(), instance())
	}

	bind<DownloadChapterPassageUseCase>() with provider {
		DownloadChapterPassageUseCase(instance(), instance(), instance())
	}
	bind<DeleteChapterPassageUseCase>() with provider {
		DeleteChapterPassageUseCase(
			instance(),
			instance()
		)
	}

	bind<StartDownloadWorkerUseCase>() with provider {
		StartDownloadWorkerUseCase(instance(), instance())
	}
	bind<StartUpdateWorkerUseCase>() with provider {
		StartUpdateWorkerUseCase(instance())
	}

	bind<LoadRemoteAppUpdateUseCase>() with provider { LoadRemoteAppUpdateUseCase(instance()) }

	bind<UpdateBookmarkedNovelUseCase>() with provider { UpdateBookmarkedNovelUseCase(instance()) }

	bind<UninstallExtensionUseCase>() with provider {
		UninstallExtensionUseCase(
			instance(),
			instance()
		)
	}


	bind<GetURLUseCase>() with provider {
		GetURLUseCase(
			instance(),
		)
	}
	bind<IsOnlineUseCase>() with provider { IsOnlineUseCase(instance()) }

	bind<LoadAppUpdateFlowLiveUseCase>() with provider { LoadAppUpdateFlowLiveUseCase(instance()) }

	bind<GetCatalogueQueryDataUseCase>() with provider {
		GetCatalogueQueryDataUseCase(instance(), instance())
	}
	bind<LoadSearchRowUIUseCase>() with provider {
		LoadSearchRowUIUseCase(
			instance(),
			instance()
		)
	}
	bind<GetExtensionSettingsUseCase>() with provider {
		GetExtensionSettingsUseCase(
			instance(),
			instance(),
		)
	}
	bind<GetInstalledExtensionUseCase>() with provider { GetInstalledExtensionUseCase(instance()) }
	bind<GetRepositoryUseCase>() with provider { GetRepositoryUseCase(instance()) }
	bind<LoadRepositoriesUseCase>() with provider { LoadRepositoriesUseCase(instance()) }
	bind<LoadReaderThemes>() with provider {
		LoadReaderThemes(instance(), instance())
	}
	bind<LoadChaptersResumeFirstUnreadUseCase>() with provider {
		LoadChaptersResumeFirstUnreadUseCase(instance())
	}

	bind<LoadNavigationStyleUseCase>() with provider { LoadNavigationStyleUseCase(instance()) }
	bind<LoadRequireDoubleBackUseCase>() with provider { LoadRequireDoubleBackUseCase(instance()) }

	bind<LoadLiveAppThemeUseCase>() with provider { LoadLiveAppThemeUseCase(instance()) }

	bind<LoadNovelUIColumnsPUseCase>() with provider { LoadNovelUIColumnsPUseCase(instance()) }
	bind<LoadNovelUIColumnsHUseCase>() with provider { LoadNovelUIColumnsHUseCase(instance()) }
	bind<LoadNovelUIBadgeToastUseCase>() with provider { LoadNovelUIBadgeToastUseCase(instance()) }
	bind<LoadNovelUITypeUseCase>() with provider { LoadNovelUITypeUseCase(instance()) }
	bind<StartAppUpdateInstallWorkerUseCase>() with provider {
		StartAppUpdateInstallWorkerUseCase(instance())
	}
	bind<CanAppSelfUpdateUseCase>() with provider {
		CanAppSelfUpdateUseCase(instance())
	}
	bind<LoadAppUpdateUseCase>() with provider {
		LoadAppUpdateUseCase(instance())
	}
	bind<SetNovelUITypeUseCase>() with provider {
		SetNovelUITypeUseCase(instance())
	}

	bind<GetNovelSettingFlowUseCase>() with provider {
		GetNovelSettingFlowUseCase(instance())
	}
	bind<UpdateNovelSettingUseCase>() with provider {
		UpdateNovelSettingUseCase(instance())
	}
	bind<LoadDeletePreviousChapterUseCase>() with provider {
		LoadDeletePreviousChapterUseCase(instance())
	}

	bind<PurgeNovelCacheUseCase>() with provider {
		PurgeNovelCacheUseCase(instance())
	}

	bind<StartBackupWorkerUseCase>() with provider {
		StartBackupWorkerUseCase(instance(), instance())
	}
	bind<LoadInternalBackupNamesUseCase>() with provider {
		LoadInternalBackupNamesUseCase(instance())
	}

	bind<StartRestoreWorkerUseCase>() with provider {
		StartRestoreWorkerUseCase(instance(), instance())
	}

	bind<AddRepositoryUseCase>() with provider {
		AddRepositoryUseCase(instance())
	}
	bind<DeleteRepositoryUseCase>() with provider {
		DeleteRepositoryUseCase(instance())
	}
	bind<UpdateRepositoryUseCase>() with provider {
		UpdateRepositoryUseCase(instance())
	}
	bind<GetReaderSettingUseCase>() with provider {
		GetReaderSettingUseCase(instance(), instance())
	}

	bind<LoadLibraryFilterSettingsUseCase>() with provider {
		LoadLibraryFilterSettingsUseCase(instance())
	}

	bind<GetCategoriesUseCase>() with provider { GetCategoriesUseCase(instance()) }

	bind<AddCategoryUseCase>() with provider {
		AddCategoryUseCase(instance())
	}

	bind<DeleteCategoryUseCase>() with provider {
		DeleteCategoryUseCase(instance())
	}

	bind<MoveCategoryUseCase>() with provider {
		MoveCategoryUseCase(instance(), instance())
	}

	bind<GetNovelCategoriesUseCase>() with provider {
		GetNovelCategoriesUseCase(instance())
	}

	bind<SetNovelCategoriesUseCase>() with provider {
		SetNovelCategoriesUseCase(instance())
	}

	bind<SetNovelsCategoriesUseCase>() with provider {
		SetNovelsCategoriesUseCase(instance())
	}

	bind<UpdateLibraryFilterSettingsUseCase>() with provider {
		UpdateLibraryFilterSettingsUseCase(instance())
	}

	bind<GetExtListingNamesUseCase>() with provider {
		GetExtListingNamesUseCase(instance())
	}

	bind<UpdateExtSelectedListing>() with provider {
		UpdateExtSelectedListing(instance())
	}

	bind<GetExtSelectedListingUseCase>() with provider {
		GetExtSelectedListingUseCase(instance())
	}

	bind<GetExtSelectedListingFlowUseCase>() with provider {
		GetExtSelectedListingFlowUseCase(instance())
	}


	bind<UpdateExtensionSettingUseCase>() with provider {
		UpdateExtensionSettingUseCase(instance(), instance(), instance())
	}

	bind<ForceInsertRepositoryUseCase>() with provider {
		ForceInsertRepositoryUseCase(instance())
	}

	bind<CancelExtensionInstallUseCase>() with provider { CancelExtensionInstallUseCase(instance()) }

	bind<LoadBackupProgressFlowUseCase>() with provider { LoadBackupProgressFlowUseCase(instance()) }
	bind<RemoveExtensionEntityUseCase>() with provider {
		RemoveExtensionEntityUseCase(
			instance(),
			instance()
		)
	}

	bind<InstallExtensionUseCase>() with provider {
		InstallExtensionUseCase(
			instance(),
			instance(),
			instance()
		)
	}

	bind<StartExportBackupWorkerUseCase>() with provider {
		StartExportBackupWorkerUseCase(
			instance(),
			instance()
		)
	}

	bind<RecordChapterIsReadingUseCase>() with provider {
		RecordChapterIsReadingUseCase(
			instance(),
			instance()
		)
	}
	bind<RecordChapterIsReadUseCase>() with provider {
		RecordChapterIsReadUseCase(
			instance(),
			instance()
		)
	}

	bind<GetLastReadChapterUseCase>() with provider { GetLastReadChapterUseCase(instance()) }

	bind<TrueDeleteChapterUseCase>() with provider {
		TrueDeleteChapterUseCase(
			instance(),
			instance()
		)
	}
	bind<GetTrueDeleteChapterUseCase>() with provider { GetTrueDeleteChapterUseCase(instance()) }

}