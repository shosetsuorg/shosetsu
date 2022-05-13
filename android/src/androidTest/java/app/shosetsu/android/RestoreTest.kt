package app.shosetsu.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import app.shosetsu.android.domain.repository.base.IBackupRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.junit.Test
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * 13 / 02 / 2021
 */
class RestoreTest : DIAware {

	private val context: Context by lazy {
		InstrumentationRegistry.getInstrumentation().targetContext
	}

	override val di: DI by closestDI(context)

	private val backupRepository by instance<IBackupRepository>()

	@Test
	fun test() {
		GlobalScope.future {
			val it = backupRepository.loadBackups()
			println("Ava backups: $it")

		}.join()
	}
}