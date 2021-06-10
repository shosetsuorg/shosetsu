package app.shosetsu.android.datasource.remote.impl

import app.shosetsu.android.common.consts.REPO_SOURCE_DIR
import app.shosetsu.android.common.ext.quickie
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.common.datasource.remote.base.IRemoteExtensionDataSource
import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
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
class RemoteExtensionDataSource(
	private val client: OkHttpClient,
) : IRemoteExtensionDataSource {

	private fun makeExtensionURL(repo: RepositoryEntity, fe: ExtensionEntity): String =
		"${repo.url}$REPO_SOURCE_DIR/${fe.lang}/${fe.fileName}.lua"

	override suspend fun downloadExtension(
		repositoryEntity: RepositoryEntity,
		extensionEntity: ExtensionEntity,
	): HResult<ByteArray> =
		try {
			@Suppress("BlockingMethodInNonBlockingContext")
			(successResult(
				client.quickie(
					makeExtensionURL(
						repositoryEntity,
						extensionEntity
					)
				).body!!.bytes()
			))
		} catch (e: Exception) {
			e.toHError()
		}
}