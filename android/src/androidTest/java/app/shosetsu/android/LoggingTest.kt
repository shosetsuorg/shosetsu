package app.shosetsu.android

//import app.shosetsu.android.common.ext.enclosingName
import org.junit.Test

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
 * 10 / 04 / 2021
 */
class LoggingTest {

	@Test
	fun log1() {
		advancedLogV("This is a log")
	}

	@Test
	fun log2() {
		advancedLogV("This is a log")
	}

	@Test
	fun log3() {
		advancedLogV("This is a log")
	}

	@Suppress("unused")
	inline fun <reified T> T.advancedLogV(message: String?) {
		// TODO Fix enclosing name
//		return Log.w(T::class.java.simpleName, "${enclosingName()}:\t$message", null)
	}

}