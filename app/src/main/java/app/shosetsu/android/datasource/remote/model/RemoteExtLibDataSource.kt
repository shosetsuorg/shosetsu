package app.shosetsu.android.datasource.remote.model

import app.shosetsu.android.common.consts.REPO_DIR_STRUCT
import app.shosetsu.android.common.ext.quickie
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.dto.successResult
import app.shosetsu.common.datasource.remote.base.IRemoteExtLibDataSource
import app.shosetsu.common.domain.model.local.ExtLibEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import okhttp3.OkHttpClient

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
 * 13 / 05 / 2020
 */
class RemoteExtLibDataSource(
		private val client: OkHttpClient,
) : IRemoteExtLibDataSource {
	private fun makeLibraryURL(repo: RepositoryEntity, le: ExtLibEntity): String =
			"${repo.url}$REPO_DIR_STRUCT/lib/${le.scriptName}.lua"

	override fun downloadLibrary(
			repo: RepositoryEntity,
			extLibEntity: ExtLibEntity,
	): HResult<String> = try {
		successResult(client.quickie(makeLibraryURL(
				repo,
				extLibEntity
		)).body!!.string())
	} catch (e: Exception) {
		e.toHError()
	}
}