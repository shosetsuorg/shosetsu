package app.shosetsu.android.viewmodel.abstracted

import androidx.compose.runtime.Immutable
import app.shosetsu.android.view.uimodels.model.DownloadUI
import app.shosetsu.android.viewmodel.base.IsOnlineCheckViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.android.viewmodel.base.SubscribeViewModel
import kotlinx.coroutines.flow.Flow

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
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
abstract class ADownloadsViewModel :
	SubscribeViewModel<List<DownloadUI>>,
	ShosetsuViewModel(),
	IsOnlineCheckViewModel {

	abstract val selectedDownloadState: Flow<SelectedDownloadsState>
	abstract val isDownloadPaused: Flow<Boolean>
	abstract val hasSelectedFlow: Flow<Boolean>

	/**
	 * Toggles paused downloads
	 *
	 * @return if paused or not
	 */
	abstract fun togglePause()

	/**
	 * Delete all downloads that are not download
	 */
	abstract fun deleteAll()

	/**
	 * Pauses downloads, waits for downloads to pause, then sets all to pending
	 */
	abstract fun setAllPending()

	abstract fun selectBetween()
	abstract fun invertSelection()
	abstract fun selectAll()
	abstract fun deleteSelected()
	abstract fun pauseSelection()
	abstract fun restartSelection()
	abstract fun startSelection()
	abstract fun toggleSelection(entity: DownloadUI)
	abstract fun deselectAll()

	/**
	 * @param pauseVisible Show the pause button
	 * @param restartVisible Show the restart button
	 * @param startVisible Show the start button
	 * @param deleteVisible Show the delete button
	 */
	@Immutable
	data class SelectedDownloadsState(
		val pauseVisible: Boolean = false,
		val restartVisible: Boolean = false,
		val startVisible: Boolean = false,
		val deleteVisible: Boolean = false,
	)
}