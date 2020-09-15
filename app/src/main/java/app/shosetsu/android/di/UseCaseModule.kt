package app.shosetsu.android.di

import app.shosetsu.android.domain.usecases.*
import app.shosetsu.android.domain.usecases.delete.DeleteChapterPassageUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteDownloadUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionSettingsUseCase
import app.shosetsu.android.domain.usecases.load.*
import app.shosetsu.android.domain.usecases.open.OpenInBrowserUseCase
import app.shosetsu.android.domain.usecases.open.OpenInWebviewUseCase
import app.shosetsu.android.domain.usecases.toast.StringToastUseCase
import app.shosetsu.android.domain.usecases.toast.ToastErrorUseCase
import app.shosetsu.android.domain.usecases.update.*
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
val useCaseModule: Kodein.Module = Kodein.Module("useCase") {

	bind<LoadDownloadsUseCase>() with provider { LoadDownloadsUseCase(instance()) }

	bind<LoadLibraryUseCase>() with provider { LoadLibraryUseCase(instance(), instance()) }

	bind<SearchBookMarkedNovelsUseCase>() with provider { SearchBookMarkedNovelsUseCase(instance()) }


	bind<LoadExtensionsUIUseCase>() with provider { LoadExtensionsUIUseCase(instance()) }

	bind<LoadUpdatesUseCase>() with provider { LoadUpdatesUseCase(instance()) }

	bind<RefreshRepositoryUseCase>() with provider { RefreshRepositoryUseCase(instance()) }

	bind<ReloadFormattersUseCase>() with provider { ReloadFormattersUseCase() }

	bind<InitializeExtensionsUseCase>() with provider {
		InitializeExtensionsUseCase(instance(), instance(), instance(), instance())
	}

	bind<InstallExtensionUIUseCase>() with provider { InstallExtensionUIUseCase(instance()) }

	bind<UpdateNovelUseCase>() with provider { UpdateNovelUseCase(instance()) }

	bind<LoadFormatterUseCase>() with provider { LoadFormatterUseCase(instance()) }

	bind<NovelBackgroundAddUseCase>() with provider {
		NovelBackgroundAddUseCase(instance(), instance())
	}

	bind<LoadFormatterNameUseCase>() with provider { LoadFormatterNameUseCase(instance()) }

	bind<LoadNovelUIUseCase>() with provider { LoadNovelUIUseCase(instance()) }

	bind<LoadNovelUseCase>() with provider {
		LoadNovelUseCase(instance(), instance(), instance(), instance())
	}

	bind<LoadCatalogueListingDataUseCase>() with provider {
		LoadCatalogueListingDataUseCase(instance(), instance(), instance())
	}

	bind<LoadChapterUIsUseCase>() with provider { LoadChapterUIsUseCase(instance()) }

	bind<UpdateChapterUseCase>() with provider { UpdateChapterUseCase(instance()) }

	bind<UpdateReaderChapterUseCase>() with provider { UpdateReaderChapterUseCase(instance()) }
	bind<LoadReaderChaptersUseCase>() with provider { LoadReaderChaptersUseCase(instance()) }

	bind<LoadChapterPassageUseCase>() with provider {
		LoadChapterPassageUseCase(instance(), instance())
	}

	bind<DownloadChapterPassageUseCase>() with provider {
		DownloadChapterPassageUseCase(instance(), instance(), instance())
	}
	bind<DeleteChapterPassageUseCase>() with provider { DeleteChapterPassageUseCase(instance()) }

	bind<StartDownloadWorkerUseCase>() with provider {
		StartDownloadWorkerUseCase(instance())
	}
	bind<StartUpdateWorkerUseCase>() with provider {
		StartUpdateWorkerUseCase(instance(), instance())
	}

	bind<LoadAppUpdateUseCase>() with provider { LoadAppUpdateUseCase(instance()) }
	bind<UpdateDownloadUseCase>() with provider { UpdateDownloadUseCase(instance()) }
	bind<DeleteDownloadUseCase>() with provider { DeleteDownloadUseCase(instance()) }

	bind<UpdateExtensionEntityUseCase>() with provider { UpdateExtensionEntityUseCase(instance()) }

	bind<UpdateBookmarkedNovelUseCase>() with provider { UpdateBookmarkedNovelUseCase(instance()) }

	bind<UninstallExtensionUIUseCase>() with provider { UninstallExtensionUIUseCase(instance()) }

	bind<StringToastUseCase>() with provider { StringToastUseCase(instance()) }

	bind<OpenInWebviewUseCase>() with provider { OpenInWebviewUseCase(instance(), instance(), instance()) }
	bind<OpenInBrowserUseCase>() with provider { OpenInBrowserUseCase(instance(), instance(), instance()) }
	bind<ShareUseCase>() with provider { ShareUseCase(instance(), instance(), instance()) }
	bind<ToastErrorUseCase>() with provider { ToastErrorUseCase(instance()) }
	bind<IsOnlineUseCase>() with provider { IsOnlineUseCase(instance()) }

	bind<LoadAppUpdateLiveUseCase>() with provider { LoadAppUpdateLiveUseCase(instance()) }

	bind<LoadCatalogueQueryDataUseCase>() with provider {
		LoadCatalogueQueryDataUseCase(instance(), instance(), instance())
	}
	bind<ConvertNCToCNUIUseCase>() with provider { ConvertNCToCNUIUseCase(instance()) }
	bind<LoadSearchRowUIUseCase>() with provider { LoadSearchRowUIUseCase((instance())) }
	bind<GetExtensionSettingsUseCase>() with provider { GetExtensionSettingsUseCase(instance()) }
	bind<LoadExtensionUIUseCase>() with provider { LoadExtensionUIUseCase(instance()) }

}