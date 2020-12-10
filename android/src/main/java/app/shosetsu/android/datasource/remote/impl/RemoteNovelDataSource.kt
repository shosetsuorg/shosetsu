package app.shosetsu.android.datasource.remote.impl

import app.shosetsu.android.common.ext.toHError
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.datasource.remote.base.IRemoteNovelDataSource
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel

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
 * shosetsu
 * 12 / May / 2020
 */
class RemoteNovelDataSource : IRemoteNovelDataSource {
	override suspend fun loadNovel(
			formatter: IExtension,
			novelURL: String,
			loadChapters: Boolean,
	): HResult<Novel.Info> {
		return try {
			successResult(
					formatter.parseNovel(novelURL, loadChapters)
			)
		} catch (e: Exception) {
			e.toHError()
		}
	}
}