package app.shosetsu.android.domain.usecases.backuprestore

import android.content.Context
import app.shosetsu.common.domain.repositories.base.IChaptersRepository
import app.shosetsu.common.domain.repositories.base.INovelsRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository

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
 * 16 / 08 / 2019
 */
@Suppress("unused")
class BackupUseCase(
	private val iNovelsRepository: INovelsRepository,
	private val iChaptersRepository: IChaptersRepository,
	private val settings: ISettingsRepository,
	private val context: Context
) {
	operator fun invoke() {
	}
}