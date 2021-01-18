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
 * 16 / 01 / 2021
 */
class ExtensionRepositoryTest : KodeinAware {
	private val context: Context by lazy {
		InstrumentationRegistry.getInstrumentation().targetContext
	}
	override val kodein: Kodein by closestKodein(context)

	private val repo: IExtensionRepoRepository by instance()

	private suspend fun print() {
		repo.loadRepositories().handle(
			onLoading = {
				logD<ExtensionRepositoryTest>("Loading")
			},
			onError = {
				logD<ExtensionRepositoryTest>(it.toString())
				it.exception?.printStackTrace()
			},
			onEmpty = {
				logD<ExtensionRepositoryTest>("Empty")
			},
			onSuccess = { list ->
				logD<ExtensionRepositoryTest>("========================")
				list.forEach {
					logD<ExtensionRepositoryTest>(it.toString())
				}
				logD<ExtensionRepositoryTest>("========================")
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
				name = "Temporary test"
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
				onError = {
					it.exception?.let {
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