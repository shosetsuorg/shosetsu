package app.shosetsu.android.di

import app.shosetsu.android.backend.workers.onetime.*
import app.shosetsu.android.backend.workers.perodic.AppUpdateCheckCycleWorker
import app.shosetsu.android.backend.workers.perodic.BackupCycleWorker
import app.shosetsu.android.backend.workers.perodic.NovelUpdateCycleWorker
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
 * 30 / 07 / 2020
 */
internal val othersModule = DI.Module("others") {

	// Workers

	// - onetime
	bind<DownloadWorker.Manager>() with singleton { DownloadWorker.Manager(instance()) }
	bind<AppUpdateCheckWorker.Manager>() with singleton { AppUpdateCheckWorker.Manager(instance()) }
	bind<NovelUpdateWorker.Manager>() with singleton { NovelUpdateWorker.Manager(instance()) }
	bind<AppUpdateInstallWorker.Manager>() with singleton { AppUpdateInstallWorker.Manager(instance()) }
	bind<BackupWorker.Manager>() with singleton { BackupWorker.Manager(instance()) }
	bind<RestoreBackupWorker.Manager>() with singleton { RestoreBackupWorker.Manager(instance()) }
	bind<RepositoryUpdateWorker.Manager>() with singleton { RepositoryUpdateWorker.Manager(instance()) }
	bind<ExtensionInstallWorker.Manager>() with singleton { ExtensionInstallWorker.Manager(instance()) }
	bind<ExportBackupWorker.Manager>() with singleton { ExportBackupWorker.Manager(instance()) }


	// - perodic
	bind<AppUpdateCheckCycleWorker.Manager>() with singleton {
		AppUpdateCheckCycleWorker.Manager(
			instance()
		)
	}
	bind<NovelUpdateCycleWorker.Manager>() with singleton { NovelUpdateCycleWorker.Manager(instance()) }
	bind<BackupCycleWorker.Manager>() with singleton { BackupCycleWorker.Manager(instance()) }

}