package app.shosetsu.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import app.shosetsu.common.enums.ExternalFileDir
import app.shosetsu.common.enums.InternalFileDir
import app.shosetsu.common.providers.file.base.IFileSystemProvider
import org.junit.Test
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

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
 * 05 / 09 / 2020
 */
class DirectoryTest : KodeinAware {
	private val context: Context by lazy {
		InstrumentationRegistry.getInstrumentation().targetContext
	}

	override val kodein: Kodein by closestKodein(context)
	private val iFileSystemProvider by instance<IFileSystemProvider>()

	@Test
	fun test() {
		InternalFileDir.values().forEach {
			println("### Internal Path: " + iFileSystemProvider.retrieveInternalPath(it, ""))
		}

		ExternalFileDir.values().forEach {
			println("### External Path: " + iFileSystemProvider.retrieveExternalPath(it, ""))
		}
	}
}