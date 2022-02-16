package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.trimDate
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.load.LoadUpdatesUseCase
import app.shosetsu.android.domain.usecases.start.StartUpdateWorkerUseCase
import app.shosetsu.android.view.uimodels.model.UpdateUI
import app.shosetsu.android.viewmodel.abstracted.AUpdatesViewModel
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.ReadingStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import org.joda.time.DateTime

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
class UpdatesViewModel(
	private val getUpdatesUseCase: LoadUpdatesUseCase,
	private val reportExceptionUseCase: ReportExceptionUseCase,
	private val startUpdateWorkerUseCase: StartUpdateWorkerUseCase,
	private val isOnlineUseCase: IsOnlineUseCase
) : AUpdatesViewModel() {
	private val updatesFlow by lazy {
		getUpdatesUseCase()
			.mapLatestResult { list ->
				successResult(list.sortedByDescending { it.time })
			}
			.mapLatest { hResult ->
				hResult.transform {
					if (it.isEmpty()) emptyResult()
					else successResult(it)
				}
			}
	}

	override val liveData: LiveData<HResult<List<UpdateUI>>> by lazy {
		updatesFlow.asIOLiveData()
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun startUpdateManager() = startUpdateWorkerUseCase()

	override fun isOnline(): Boolean = isOnlineUseCase()

	override val items: LiveData<HResult<Map<DateTime, List<UpdateUI>>>> by lazy {
		updatesFlow.mapLatest { result ->
			result.transformToSuccess { list ->
				list.groupBy {
					DateTime(it.time).trimDate()
				}
			}
		}.asIOLiveData()
	}

	override suspend fun updateChapter(updateUI: UpdateUI, readingStatus: ReadingStatus) {
	}
}