package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.common.enums.DownloadStatus
import app.shosetsu.android.common.ext.onIO
import app.shosetsu.android.domain.repository.base.IExtensionDownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
	private val statusMap = HashMap<Int, MutableStateFlow<DownloadStatus>>()

	private fun HashMap<Int, MutableStateFlow<DownloadStatus>>.iGet(extension: Int) =
		getOrPut(extension) { MutableStateFlow(DownloadStatus.WAITING) }

	override suspend fun add(extension: Int) {
		onIO { statusMap.iGet(extension).emit(DownloadStatus.PENDING) }
	}

	override suspend fun remove(extension: Int) {
		onIO { statusMap.iGet(extension).emit(DownloadStatus.WAITING) }
	}

	override suspend fun getStatus(extension: Int): DownloadStatus =
		onIO { statusMap.iGet(extension).value }

	override suspend fun getStatusFlow(extension: Int): Flow<DownloadStatus> =
		statusMap.iGet(extension).onIO()

	override suspend fun updateStatus(
		extension: Int,
		status: DownloadStatus
	) {
		onIO { statusMap.iGet(extension).emit(status) }
	}
}