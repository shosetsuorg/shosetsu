package app.shosetsu.android.datasource.remote.impl.fauk

import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.datasource.remote.base.IRemoteChaptersDataSource
import app.shosetsu.lib.IExtension
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

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
 * Shosetsu
 *
 * @since 22 / 03 / 2022
 * @author Doomsdayrs
 */
class FaukRemoteChaptersDataSource : IRemoteChaptersDataSource {

	override suspend fun loadChapterPassage(formatter: IExtension, chapterURL: String): ByteArray {
		logV("Fauk")
		// Change the number below and click apply in Android Studio
		// to change the error that is sent
		when (5) {
			1 -> throw IOException("Oops")
			2 -> throw SSLException("Aaa")
			3 -> throw SocketTimeoutException("Yikes")
			4 -> throw UnknownHostException("Huh")
			5 -> throw NullPointerException("Not here")
			else -> throw Exception("stub")
		}
	}
}