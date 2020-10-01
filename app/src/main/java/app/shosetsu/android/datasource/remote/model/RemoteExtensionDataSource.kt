package app.shosetsu.android.datasource.remote.model

import app.shosetsu.android.common.consts.ErrorKeys.ERROR_GENERAL
import app.shosetsu.android.common.consts.REPO_DIR_STRUCT
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.quickie
import app.shosetsu.android.datasource.remote.base.IRemoteExtensionDataSource
import app.shosetsu.android.domain.model.local.ExtensionEntity
import app.shosetsu.android.domain.model.local.RepositoryEntity
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

	private fun makeFormatterURL(repo: RepositoryEntity, fe: ExtensionEntity): String =
			"${repo.url}$REPO_DIR_STRUCT/src/${fe.lang}/${fe.fileName}.lua"

	override suspend fun downloadExtension(
			repositoryEntity: RepositoryEntity,
			extensionEntity: ExtensionEntity,
	): HResult<String> =
			try {
				@Suppress("BlockingMethodInNonBlockingContext")
				(successResult(client.quickie(makeFormatterURL(
						repositoryEntity,
						extensionEntity
				)).body!!.string()))
			} catch (e: Exception) {
				errorResult(ERROR_GENERAL, e.message ?: "Unknown general error")
			}
}