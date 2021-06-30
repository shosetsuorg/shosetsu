package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.domain.repositories.base.IExtensionDownloadRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.enums.DownloadStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

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
 * @since 30 / 06 / 2021
 * @author Doomsdayrs
 */
class ExtensionDownloadRepository : IExtensionDownloadRepository {
	private val statusMap = HashMap<Int, MutableStateFlow<HResult<DownloadStatus>>>()

	override val size: Int
		get() = statusMap.count {
			it.value.value is HResult.Success &&
					(it.value.value as HResult.Success).data == DownloadStatus.PENDING
		}

	override val first: HResult<Int>
		get() {
			return successResult(statusMap.entries.firstOrNull {
				it.value.value is HResult.Success
						&& (it.value.value as HResult.Success).data == DownloadStatus.PENDING
			}?.key ?: return emptyResult())
		}

	override suspend fun add(extension: Int): HResult<*> =
		successResult(
			statusMap.set(
				extension,
				MutableStateFlow(successResult(DownloadStatus.PENDING))
			)
		)

	override suspend fun remove(extension: Int): HResult<*> {
		return successResult()
	}

	override suspend fun getStatus(extension: Int): HResult<DownloadStatus> =
		statusMap.getOrPut(extension) { MutableStateFlow(emptyResult()) }.value

	override suspend fun getStatusFlow(extension: Int): Flow<HResult<DownloadStatus>> =
		flow {
			emitAll(statusMap.getOrPut(extension) { MutableStateFlow(emptyResult()) })
		}

	override suspend fun updateStatus(
		extension: Int,
		status: DownloadStatus
	): HResult<*> = successResult(statusMap[extension]?.tryEmit(successResult(status)))
}