package com.github.doomsdayrs.apps.shosetsu.di

import com.github.doomsdayrs.apps.shosetsu.domain.usecases.*
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
val useCaseModule = Kodein.Module("useCase") {
	bind<FormatterAsCardsUseCase>() with provider { FormatterAsCardsUseCase(instance()) }

	bind<GetDownloadsUseCase>() with provider { GetDownloadsUseCase(instance()) }

	bind<LibraryAsCardsUseCase>() with provider { LibraryAsCardsUseCase(instance()) }

	bind<SearchBookMarkedNovelsUseCase>() with provider { SearchBookMarkedNovelsUseCase(instance()) }

	bind<UnreadChaptersUseCase>() with provider { UnreadChaptersUseCase(instance()) }

	bind<GetExtensionsUIUseCase>() with provider { GetExtensionsUIUseCase(instance()) }

	bind<GetUpdateDaysUseCase>() with provider { GetUpdateDaysUseCase(instance()) }

	bind<RefreshRepositoryUseCase>() with provider { RefreshRepositoryUseCase(instance()) }

	bind<ReloadFormattersUseCase>() with provider { ReloadFormattersUseCase() }

	bind<InitializeExtensionsUseCase>() with provider {
		InitializeExtensionsUseCase(instance(), instance(), instance())
	}

	bind<InstallExtensionUIUseCase>() with provider { InstallExtensionUIUseCase(instance()) }

	bind<BookMarkNovelIDUseCase>() with provider { BookMarkNovelIDUseCase(instance()) }

	bind<GetFormatterUseCase>() with provider { GetFormatterUseCase(instance()) }

	bind<NovelBackgroundAddUseCase>() with provider {
		NovelBackgroundAddUseCase(instance(), instance())
	}

	bind<GetFormatterNameUseCase>() with provider { GetFormatterNameUseCase(instance()) }

	bind<GetNovelUIUseCase>() with provider { GetNovelUIUseCase(instance(), instance()) }

	bind<LoadNovelUseCase>() with provider {
		LoadNovelUseCase(instance(), instance(), instance())
	}

	bind<LoadCatalogueData>() with provider {
		LoadCatalogueData(instance(), instance())
	}

	bind<GetChapterUIsUseCase>() with provider { GetChapterUIsUseCase(instance()) }

}