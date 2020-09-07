package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import android.content.Context
import com.github.doomsdayrs.apps.shosetsu.backend.workers.DownloadWorker.Manager

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
 * 20 / 06 / 2020
 */
class StartDownloadWorkerUseCase(
		private val context: Context,
		private val manager: Manager,
) {
	/**
	 * Starts the download worker
	 * @param override if true then will override the current download loop
	 */
	operator fun invoke(override: Boolean = false) {
		if (!manager.isRunning(context) || override)
			manager.start(context)
	}
}