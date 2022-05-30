package app.shosetsu.android.domain.usecases

import app.shosetsu.android.backend.workers.onetime.RepositoryUpdateWorker
import app.shosetsu.android.common.ext.launchIO

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
 * <p>
 *     Initializes formatters, libraries, and repositories
 * </p>
 */
class StartRepositoryUpdateManagerUseCase(
	private val manager: RepositoryUpdateWorker.Manager
) {
	operator fun invoke(force: Boolean = false) {
		launchIO {
			if (!manager.isRunning())
				manager.start(force = force)
		}
	}

}