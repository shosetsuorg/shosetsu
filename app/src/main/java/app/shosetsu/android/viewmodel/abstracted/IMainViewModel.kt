package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.domain.model.remote.DebugAppUpdate
import app.shosetsu.android.viewmodel.base.ErrorReportingViewModel
import app.shosetsu.android.viewmodel.base.IsOnlineCheckViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel

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
abstract class IMainViewModel : ShosetsuViewModel(), IsOnlineCheckViewModel, ErrorReportingViewModel {
	abstract fun share(string: String, int: String)


	abstract fun startDownloadWorker()

	@Deprecated("Now auto started")
	abstract fun startUpdateWorker()

	abstract fun startUpdateCheck(): LiveData<HResult<DebugAppUpdate>>

	/**
	 * If 0, Bottom
	 * If 1, Drawer
	 */
	abstract fun navigationStyle(): Int
}