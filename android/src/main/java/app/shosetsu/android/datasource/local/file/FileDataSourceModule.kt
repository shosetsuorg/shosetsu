package app.shosetsu.android.datasource.local.file

import app.shosetsu.android.datasource.local.file.base.*
import app.shosetsu.android.datasource.local.file.impl.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

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
 * 12 / 05 / 2020
 */
val fileDataSourceModule: DI.Module = DI.Module("file_data_source") {
	bind<IFileExtensionDataSource>() with singleton {
		FileExtensionDataSource(instance())
	}

	bind<IFileChapterDataSource>() with singleton {
		FileChapterDataSource(instance())
	}

	bind<IFileExtLibDataSource>() with singleton {
		FileExtLibDataSource(instance())
	}

	bind<IFileCachedChapterDataSource>() with singleton {
		FileCachedChapterDataSource(instance())
//		QueuedFileCacheChapterDataSource(instance())
	}

	bind<IFileCachedAppUpdateDataSource>() with singleton {
		FileAppUpdateDataSource(instance())
	}

	bind<IFileSettingsDataSource>() with singleton {
		FileSharedPreferencesSettingsDataSource(
			instance()
		)
	}
	bind<IFileBackupDataSource>() with singleton { FileBackupDataSource(instance()) }
	bind<IFileCrashDataSource>() with singleton { FileCrashDataSource(instance()) }
}