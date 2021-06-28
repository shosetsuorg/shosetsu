package app.shosetsu.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import app.shosetsu.common.domain.repositories.base.IBackupRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.junit.Test
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

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
class RestoreTest : KodeinAware {

	private val context: Context by lazy {
		InstrumentationRegistry.getInstrumentation().targetContext
	}

	override val kodein: Kodein by closestKodein(context)

	private val backupRepository by instance<IBackupRepository>()

	@Test
	fun test() {
		GlobalScope.future {
			backupRepository.loadBackups().handle {
				println("Ava backups: $it")
			}
		}.join()
	}
}