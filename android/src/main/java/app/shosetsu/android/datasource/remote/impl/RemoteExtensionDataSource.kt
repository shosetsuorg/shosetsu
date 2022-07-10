package app.shosetsu.android.datasource.remote.impl

import app.shosetsu.android.common.EmptyResponseBodyException
import app.shosetsu.android.common.consts.REPO_SOURCE_DIR
import app.shosetsu.android.common.ext.quickie
import app.shosetsu.android.datasource.remote.base.IRemoteExtensionDataSource
import app.shosetsu.android.domain.model.local.GenericExtensionEntity
import app.shosetsu.android.domain.model.local.RepositoryEntity
import app.shosetsu.lib.exceptions.HTTPException
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
class RemoteExtensionDataSource(
	private val client: OkHttpClient,
) : IRemoteExtensionDataSource {

	private fun makeExtensionURL(repo: RepositoryEntity, fe: GenericExtensionEntity): String =
		"${repo.url}$REPO_SOURCE_DIR/${fe.lang}/${fe.fileName}.lua"

	@Throws(HTTPException::class, IOException::class, EmptyResponseBodyException::class)
	override suspend fun downloadExtension(
		repositoryEntity: RepositoryEntity,
		extensionEntity: GenericExtensionEntity,
	): ByteArray {
		val url = makeExtensionURL(
			repositoryEntity,
			extensionEntity
		)

		@Suppress("BlockingMethodInNonBlockingContext")
		val response = client.quickie(url)
		if (response.isSuccessful)
			@Suppress("BlockingMethodInNonBlockingContext")
			return response.body?.bytes() ?: throw EmptyResponseBodyException(url)
		else throw HTTPException(response.code)
	}

}