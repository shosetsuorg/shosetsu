package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.domain.model.local.AppUpdateEntity
import app.shosetsu.android.domain.repository.base.IAppUpdatesRepository
import app.shosetsu.lib.exceptions.HTTPException
import java.io.IOException
import java.net.UnknownHostException

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
class LoadRemoteAppUpdateUseCase(
	private val iAppUpdatesRepository: IAppUpdatesRepository
) {
	@Throws(
		FilePermissionException::class,
		UnknownHostException::class,
		IOException::class,
		HTTPException::class
	)
	suspend operator fun invoke(): AppUpdateEntity? =
		iAppUpdatesRepository.loadRemoteUpdate()
}