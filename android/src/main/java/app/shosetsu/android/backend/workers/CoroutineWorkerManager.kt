package app.shosetsu.android.backend.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkManager.getInstance
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI

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
 * 06 / 09 / 2020
 * @param context Context of the application
 */
abstract class CoroutineWorkerManager(
	val context: Context
) : DIAware {
	override val di: DI by closestDI(context)

	/**
	 * WorkManager
	 */
	val workerManager: WorkManager by lazy { getInstance(context) }

	/**
	 * Count of workers that match the ID
	 */
	abstract suspend fun getCount(): Int

	abstract suspend fun getWorkerState(index: Int = 0): WorkInfo.State

	abstract suspend fun getWorkerInfoList(): List<WorkInfo>

	open suspend fun isRunning(): Boolean =
		getWorkerState() == WorkInfo.State.RUNNING

	abstract fun start(data: Data = Data.EMPTY)

	abstract fun stop(): Operation
}