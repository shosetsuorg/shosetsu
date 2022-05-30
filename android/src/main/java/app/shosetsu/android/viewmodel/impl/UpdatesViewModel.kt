package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.viewModelScope
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.ext.trimDate
import app.shosetsu.android.domain.model.local.UpdateCompleteEntity
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.load.LoadUpdatesUseCase
import app.shosetsu.android.domain.usecases.start.StartUpdateWorkerUseCase
import app.shosetsu.android.domain.usecases.update.UpdateChapterUseCase
import app.shosetsu.android.viewmodel.abstracted.AUpdatesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
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
@OptIn(ExperimentalCoroutinesApi::class)
class UpdatesViewModel(
	private val getUpdatesUseCase: LoadUpdatesUseCase,
	private val startUpdateWorkerUseCase: StartUpdateWorkerUseCase,
	private val isOnlineUseCase: IsOnlineUseCase,
	private val updateChapterUseCase: UpdateChapterUseCase
) : AUpdatesViewModel() {
	override val liveData: Flow<Map<DateTime, List<UpdateCompleteEntity>>> by lazy {
		getUpdatesUseCase().transformLatest {
			isRefreshing.emit(true)
			emit(it.ifEmpty { emptyList() }.sortedByDescending { it.time })
			isRefreshing.emit(false)
		}.mapLatest { result ->
			result.groupBy {
				DateTime(it.time).trimDate()
			}
		}.shareIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, 1)
	}

	override fun startUpdateManager() = startUpdateWorkerUseCase()

	override fun isOnline(): Boolean = isOnlineUseCase()

	override val isRefreshing: MutableStateFlow<Boolean> by lazy { MutableStateFlow(false) }

	override suspend fun updateChapter(
		updateUI: UpdateCompleteEntity,
		readingStatus: ReadingStatus
	) {
		TODO("Not yet implemented")
	}
}