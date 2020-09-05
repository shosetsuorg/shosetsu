package com.github.doomsdayrs.apps.shosetsu

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import java.io.File

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
class DirectoryTest {

	lateinit var context: Context

	@Before
	fun setup() {
		println("Setting up")
		context = InstrumentationRegistry.getInstrumentation().targetContext
	}

	@Test
	fun test() {
		println("Running a test")
		val d = context.getExternalFilesDir(null)!!
		println("Directory: $d")

		print("Can read? ")
		assert(d.canRead()) { "Cannot read" }
		println("Yes")

		print("Can write? ")
		assert(d.canWrite()) { "Cannot write" }
		println("Yes")

		print("Can create? ")
		assert(d.mkdirs()) { "Cannot create directories" }
		println("Yes")

		print("Exists? ")
		assert(d.exists()) { "Does not exist" }
		println("Yes")

		print("Can create downloads folder? ")
		assert(File(d.absolutePath + "/download").mkdirs()) { "Cannot create download folder" }
		println("Yes")
	}
}