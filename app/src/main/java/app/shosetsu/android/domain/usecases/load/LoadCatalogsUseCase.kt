package app.shosetsu.android.domain.usecases.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.shosetsu.android.common.dto.*
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.view.uimodels.model.catlog.CatalogOptionUI

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
 * 02 / 05 / 2020
 * Returns the formatters present as a list of formatterCards
 * @param formatterRepository Repository of formatters
 */
class LoadCatalogsUseCase(
		private val iExtensionsRepository: IExtensionsRepository,
) : (() -> LiveData<HResult<List<CatalogOptionUI>>>) {
	override fun invoke(): LiveData<HResult<List<CatalogOptionUI>>> {
		return liveData {
			emitSource(iExtensionsRepository.getCards().map { data ->
				when (data) {
					is HResult.Success -> {
						successResult(data.data.map { CatalogOptionUI(it.id.toLong(), it.title, it.imageURL) })
					}
					is HResult.Loading -> loading()
					is HResult.Error -> errorResult(data.code, data.message)
					is HResult.Empty -> emptyResult()
				}
			})
		}
	}
}