package app.shosetsu.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
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

	private suspend fun printEntites() {
		println("====================")
		repo.loadRepositories().handle(
			onLoading = {
				println("Loading")
			},
			onError = {
				println(it)
			},
			onEmpty = {
				println("Empty")
			},
			onSuccess = {
				it.forEach {
					println(it)
				}
			}
		)
		println("====================")
	}

	@Test
	fun test() {
		GlobalScope.future {

			println("Current repositories")
			printEntites()

			val tempRepoValue = RepositoryEntity(
				url = "Build test",
				name = "Temporary test"
			)

			val result = repo.addRepository(tempRepoValue)
			result.handle(
				onError = {
					it.exception?.printStackTrace()
				}
			) {
				println("Added successfully")
			}
			requireNotNull(result.unwrap()) { "Failed to add" }

			println("With new element")
			printEntites()

			val handle = repo.loadRepositories().unwrap()?.find { it.url == tempRepoValue.url }
			requireNotNull(handle) { "Failed to retrieve" }

			println("Attempting database duplicate injection")
			require(repo.addRepository(tempRepoValue).unwrap() == null) { "Properly failed" }

			println("Should match previous output")
			printEntites()

			// Clean up
			requireNotNull(repo.remove(handle).unwrap()) { "Failed to remove!" }

		}.join()
	}

}