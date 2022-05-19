package app.shosetsu.android.viewmodel.impl

import app.shosetsu.android.common.SettingKey.IsDownloadPaused
import app.shosetsu.android.common.enums.DownloadStatus
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.utils.copy
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteDownloadUseCase
import app.shosetsu.android.domain.usecases.load.LoadDownloadsUseCase
import app.shosetsu.android.domain.usecases.start.StartDownloadWorkerUseCase
import app.shosetsu.android.domain.usecases.update.UpdateDownloadUseCase
import app.shosetsu.android.view.uimodels.model.DownloadUI
import app.shosetsu.android.viewmodel.abstracted.ADownloadsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DownloadsViewModel(
	private val getDownloadsUseCase: LoadDownloadsUseCase,
	private val startDownloadWorkerUseCase: StartDownloadWorkerUseCase,
	private val updateDownloadUseCase: UpdateDownloadUseCase,
	private val deleteDownloadUseCase: DeleteDownloadUseCase,
	private val settings: ISettingsRepository,
	private var isOnlineUseCase: IsOnlineUseCase,
) : ADownloadsViewModel() {

	private val selectedDownloads = MutableStateFlow<Map<Int, Boolean>>(mapOf())
	private suspend fun copySelected(): HashMap<Int, Boolean> =
		selectedDownloads.first().copy()

	private suspend fun clearSelectedSuspend() {
		selectedDownloads.emit(mapOf())
	}

	private fun List<DownloadUI>.sort() = sortedWith(compareBy<DownloadUI> {
		it.status == DownloadStatus.ERROR
	}.thenBy {
		it.status == DownloadStatus.PAUSED
	}.thenBy {
		it.status == DownloadStatus.PENDING
	}.thenBy {
		it.status == DownloadStatus.WAITING
	}.thenBy {
		it.status == DownloadStatus.DOWNLOADING
	})

	private val downloadsFlow by lazy {
		flow {
			emitAll(getDownloadsUseCase().mapLatest { list ->
				list.sort()
			}.combine(selectedDownloads) { list, map ->
				list.map {
					it.copy(isSelected = map.getOrElse(it.chapterID) { false })
				}
			})
		}
	}

	override val liveData: Flow<List<DownloadUI>> by lazy {
		downloadsFlow
	}

	override fun isOnline(): Boolean = isOnlineUseCase()

	override val isDownloadPaused: Flow<Boolean> by lazy {
		settings.getBooleanFlow(IsDownloadPaused).onIO()
	}
	override val hasSelectedFlow: Flow<Boolean> by lazy {
		selectedDownloads.mapLatest { map ->
			map.values.any { it }
		}
	}

	override fun togglePause() {
		launchIO {
			settings.getBoolean(IsDownloadPaused).let { isPaused ->
				settings.setBoolean(IsDownloadPaused, !isPaused)
				if (isPaused) startDownloadWorkerUseCase()
			}
		}
	}

	override fun deleteAll() {
		launchIO {
			pauseAndWait()

			downloadsFlow.first { it.isNotEmpty() }.let { list ->
				list.forEach { deleteDownloadUseCase(it) }
			}
		}
	}

	/**
	 * Pause the downloads and wait 1 second
	 */
	private suspend fun pauseAndWait() {
		settings.setBoolean(IsDownloadPaused, true)
		delay(1000)
	}

	override fun setAllPending() {
		launchIO {
			pauseAndWait()
			downloadsFlow.first { it.isNotEmpty() }.let { list ->
				list.forEach { updateDownloadUseCase(it.copy(status = DownloadStatus.PENDING)) }
			}
		}
	}

	override fun selectBetween() {
		launchIO {
			val list = liveData.first()
			val selection = copySelected()

			val firstSelected = list.indexOfFirst { it.isSelected }
			val lastSelected = list.indexOfLast { it.isSelected }

			if (listOf(firstSelected, lastSelected).any { it == -1 }) {
				logE("Received -1 index")
				return@launchIO
			}

			if (firstSelected == lastSelected) {
				logE("Ignoring select between, requires more then 1 selected item")
				return@launchIO
			}

			if (firstSelected + 1 == lastSelected) {
				logE("Ignoring select between, requires gap between items")
				return@launchIO
			}

			list.subList(firstSelected + 1, lastSelected).forEach {
				selection[it.chapterID] = true
			}

			selectedDownloads.emit(selection)
		}
	}

	override fun invertSelection() {
		launchIO {
			val list = liveData.first()
			val selection = copySelected()

			list.forEach {
				selection[it.chapterID] = !it.isSelected
			}

			selectedDownloads.emit(selection)
		}
	}

	override fun selectAll() {
		launchIO {
			val list = liveData.first()
			val selection = copySelected()

			list.forEach {
				selection[it.chapterID] = true
			}

			selectedDownloads.emit(selection)
		}
	}

	override fun deleteSelected() {
		launchIO {
			val selected = liveData.first().filter { it.isSelected }
			clearSelectedSuspend()
			selected.forEach { deleteDownloadUseCase(it) }
		}
	}

	override fun pauseSelection() {
		launchIO {
			val selected = liveData.first().filter { it.isSelected }
			clearSelectedSuspend()
			selected.forEach { updateDownloadUseCase(it.copy(status = DownloadStatus.PAUSED)) }
		}
	}

	override fun restartFailedSelection() {
		launchIO {
			val selected = liveData.first().filter { it.isSelected }
				.filter {
					it.status == DownloadStatus.PAUSED || it.status == DownloadStatus.ERROR
				}
			clearSelectedSuspend()
			selected.forEach { updateDownloadUseCase(it.copy(status = DownloadStatus.PENDING)) }
		}
	}

	override fun startSelection() {
		launchIO {
			val selected = liveData.first().filter { it.isSelected }
				.filter {
					it.status == DownloadStatus.PAUSED || it.status == DownloadStatus.ERROR
				}
			clearSelectedSuspend()
			selected.forEach { deleteDownloadUseCase(it) }
		}
	}

	override fun toggleSelection(entity: DownloadUI) {
		launchIO {
			val selection = copySelected()
			selection[entity.chapterID] = !entity.isSelected
			selectedDownloads.emit(selection)
		}
	}

	override fun deselectAll() {
		launchIO {
			if (selectedDownloads.value.values.any { it })
				clearSelectedSuspend()
		}
	}
}