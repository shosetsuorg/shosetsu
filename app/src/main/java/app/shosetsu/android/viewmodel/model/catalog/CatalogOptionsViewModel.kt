package app.shosetsu.android.viewmodel.model.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.loading
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.load.LoadCatalogsUseCase
import app.shosetsu.android.view.uimodels.model.catlog.CatalogOptionUI
import app.shosetsu.android.viewmodel.abstracted.ICatalogOptionsViewModel
import kotlinx.coroutines.Dispatchers

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
 * 30 / 04 / 2020
 */
class CatalogOptionsViewModel(
		private val loadCatalogsUseCase: LoadCatalogsUseCase,
		private val isOnlineUseCase: IsOnlineUseCase,
) : ICatalogOptionsViewModel() {
	override val liveData: LiveData<HResult<List<CatalogOptionUI>>> by lazy {
		liveData(context = viewModelScope.coroutineContext + Dispatchers.Main) {
			emit(loading())
			emitSource(loadCatalogsUseCase())
		}
	}

	override fun isOnline(): Boolean = isOnlineUseCase()
}