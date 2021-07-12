package app.shosetsu.android.viewmodel.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import app.shosetsu.common.dto.HResult

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
 * @since 12 / 07 / 2021
 * @author Doomsdayrs
 */
interface IWorkerUpdatingViewModel {
	/**
	 * Returns a [HResult.Success] of [WorkerIdentifier] when the worker is running,
	 * prompting the user to restart/requeue it.
	 *
	 * Otherwise will return [HResult.Empty]
	 */
	val workerSettingsChanged: LiveData<HResult<WorkerIdentifier>>

	/**
	 * Identifies which worker has been changed, prompting the user to update its settings
	 */
	data class WorkerIdentifier(
		val id: String,
		@StringRes val nameRes: Int
	)
}