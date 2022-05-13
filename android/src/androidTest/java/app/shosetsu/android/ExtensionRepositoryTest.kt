package app.shosetsu.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.domain.repository.base.IExtensionRepoRepository
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
 * 16 / 01 / 2021
 */
class ExtensionRepositoryTest : DIAware {
	private val context: Context by lazy {
		InstrumentationRegistry.getInstrumentation().targetContext
	}
	override val di: DI by closestDI(context)

	private val repo: IExtensionRepoRepository by instance()

	private suspend fun print() {
		val list = repo.loadRepositories()
		logD("========================")
		list.forEach {
			logD(it.toString())
		}
		logD("========================")
	}

	@Test
	fun test() {
		GlobalScope.future {
			// Prints out the changes to the repository
			print()

			// Create the temp entity
			val url = "Build test"
			val name = "Temporary test"


			// Add the temp entity
			val result = repo.addRepository(
				url = "Build test",
				name = "Temporary test",
			)
			result
			logD<ExtensionRepositoryTest>("Added successfully")
			print()


			// Getting the entity from repo
			logD<ExtensionRepositoryTest>("Attempting to retrieve entity")
			val handle = repo.loadRepositories().find { it.url == url }
			requireNotNull(handle) { "Failed to retrieve" }


			// Duplicate injection
			logD<ExtensionRepositoryTest>("Attempting database duplicate injection")
			try {
				repo.addRepository(handle.url, name)
			} catch (e: Exception) {
				logD<ExtensionRepositoryTest>("Properly rejected")
			}
			logE("Failed to reject")

			print()

			// Clean up
			repo.remove(handle)
			logD<ExtensionRepositoryTest>("Successfully removed!")

			print()
		}.join()
	}
}