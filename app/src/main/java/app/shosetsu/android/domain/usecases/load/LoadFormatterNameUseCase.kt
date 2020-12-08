package app.shosetsu.android.domain.usecases.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.dto.handleReturn
import app.shosetsu.common.com.dto.loading
import app.shosetsu.common.com.dto.successResult
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
 * 18 / 05 / 2020
 */
class LoadFormatterNameUseCase(
		private val getFormatterUseCase: LoadFormatterUseCase,
) : ((@kotlin.ParameterName("formatterID") Int) -> LiveData<HResult<String>>) {
	override fun invoke(formatterID: Int): LiveData<HResult<String>> {
		return liveData(context = Dispatchers.IO) {
			emit(loading())
			if (formatterID != -1)
				emit(getFormatterUseCase(formatterID).handleReturn { successResult(it.name) })
		}
	}
}