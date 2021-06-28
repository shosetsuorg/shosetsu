package app.shosetsu.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import app.shosetsu.android.common.ext.logD
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.repositories.base.IExtensionRepoRepository
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.unwrap
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
		repo.loadRepositories().handle(
			onLoading = {
				logD("Loading")
			},
			onError = {
				logD(it.toString())
				it.exception?.printStackTrace()
			},
			onEmpty = {
				logD("Empty")
			},
			onSuccess = { list ->
				logD("========================")
				list.forEach {
					logD(it.toString())
				}
				logD("========================")
			})
	}

	@Test
	fun test() {
		GlobalScope.future {
			// Prints out the changes to the repository
			print()

			// Create the temp entity
			val tempRepoValue = RepositoryEntity(
				url = "Build test",
				name = "Temporary test",
				isEnabled = true
			)

			// Add the temp entity
			val result = repo.addRepository(tempRepoValue)
			result.handle(
				onError = {
					it.exception?.printStackTrace()
				}
			) {
				logD<ExtensionRepositoryTest>("Added successfully")
			}
			requireNotNull(result.unwrap()) { "Failed to add" }
			print()


			// Getting the entity from repo
			logD<ExtensionRepositoryTest>("Attempting to retrieve entity")
			val handle = repo.loadRepositories().unwrap()?.find { it.url == tempRepoValue.url }
			requireNotNull(handle) { "Failed to retrieve" }


			// Duplicate injection
			logD<ExtensionRepositoryTest>("Attempting database duplicate injection")
			require(repo.addRepository(handle).unwrap() == null) { "Did not properly reject" }
			logD<ExtensionRepositoryTest>("Properly rejected")

			print()

			// Clean up
			repo.remove(handle).handle(
				onError = { error ->
					error.exception?.let {
						throw it
					}
				}
			) {
				logD<ExtensionRepositoryTest>("Successfully removed!")
			}

			print()
		}.join()
	}
}