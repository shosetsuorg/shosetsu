package app.shosetsu.android.datasource.remote.impl

import app.shosetsu.android.common.EmptyResponseBodyException
import app.shosetsu.android.common.ext.quickie
import app.shosetsu.android.datasource.remote.base.IRemoteExtRepoDataSource
import app.shosetsu.android.domain.model.local.RepositoryEntity
import app.shosetsu.lib.exceptions.HTTPException
import app.shosetsu.lib.json.RepoIndex
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import okhttp3.OkHttpClient
import java.io.IOException

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
class RemoteExtRepoDataSource(
	private val client: OkHttpClient,
) : IRemoteExtRepoDataSource {

	@OptIn(ExperimentalSerializationApi::class)
	@Throws(HTTPException::class, IOException::class, EmptyResponseBodyException::class)
	override suspend fun downloadRepoData(
		repo: RepositoryEntity,
	): RepoIndex {
		val url = "${repo.url}/index.json"

		@Suppress("BlockingMethodInNonBlockingContext")
		val response = client.quickie(url)

		if (response.isSuccessful) {
			return response.body?.use {
				RepoIndex.repositoryJsonParser.decodeFromStream(it.byteStream())
			} ?: throw EmptyResponseBodyException(url)
		} else {
			throw HTTPException(response.code)
		}
	}
}