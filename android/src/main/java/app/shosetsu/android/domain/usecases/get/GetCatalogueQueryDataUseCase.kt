package app.shosetsu.android.domain.usecases.get

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.shosetsu.android.common.GenericSQLiteException
import app.shosetsu.android.common.IncompatibleExtensionException
import app.shosetsu.android.common.LuaException
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.ext.convertTo
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.ConvertNCToCNUIUseCase
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import app.shosetsu.lib.PAGE_INDEX
import app.shosetsu.lib.exceptions.HTTPException
import coil.network.HttpException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

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
 * 15 / 05 / 2020
 */
class GetCatalogueQueryDataUseCase(
	private val getExt: GetExtensionUseCase,
	private val novelsRepository: INovelsRepository,
	private val convertNCToCNUIUseCase: ConvertNCToCNUIUseCase,
	private val iSettingsRepository: ISettingsRepository
) {
	inner class MyPagingSource(
		val iExtension: IExtension,
		val query: String,
		val data: Map<Int, Any>,
	) : PagingSource<Int, ACatalogNovelUI>() {
		override fun getRefreshKey(state: PagingState<Int, ACatalogNovelUI>): Int? {
			return state.anchorPosition?.let {
				state.closestPageToPosition(it)?.prevKey?.plus(1)
					?: state.closestPageToPosition(it)?.nextKey?.minus(1)
			}
		}

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ACatalogNovelUI> {
			return withContext(Dispatchers.IO) {
				try {
					// Key may be null during a refresh, if no explicit key is passed into Pager
					// construction. Use 0 as default, because our API is indexed started at index 0
					val pageNumber = params.key ?: iExtension.startIndex

					// Suspending network load via Retrofit. This doesn't need to be wrapped in a
					// withContext(Dispatcher.IO) { ... } block since Retrofit's Coroutine
					// CallAdapter dispatches on a worker thread.
					val response =
						novelsRepository.getCatalogueSearch(
							iExtension,
							query,
							HashMap(data).also { it[PAGE_INDEX] = pageNumber }
						).let {
							val data: List<Novel.Listing> = it
							iSettingsRepository.getInt(SettingKey.SelectedNovelCardType)
								.let { cardType ->
									(data.map { novelListing ->
										novelListing.convertTo(iExtension)
									}.mapNotNull { ne ->
										try {
											novelsRepository.insertReturnStripped(ne)?.let { card ->
												convertNCToCNUIUseCase(card, cardType)
											}
										} catch (e: GenericSQLiteException) {
											logE("Failed to load parse novel", e)
											null
										}
									})
								}
						}

					// Since 0 is the lowest page number, return null to signify no more pages should
					// be loaded before it.
					val prevKey = if (pageNumber > iExtension.startIndex) pageNumber - 1 else null

					// This API defines that it's out of data when a page returns empty. When out of
					// data, we return `null` to signify no more pages should be loaded
					val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null
					LoadResult.Page(
						data = response,
						prevKey = prevKey,
						nextKey = nextKey
					)
				} catch (e: IOException) {
					LoadResult.Error(e)
				} catch (e: HttpException) {
					LoadResult.Error(e)
				} catch (e: HTTPException) {
					LoadResult.Error(e)
				} catch (e: LuaException) {
					LoadResult.Error(e)
				}
			}
		}
	}

	@Throws(
		GenericSQLiteException::class,
		IncompatibleExtensionException::class,
		LuaException::class
	)
	suspend operator fun invoke(
		extID: Int,
		query: String,
		filters: Map<Int, Any>
	): MyPagingSource = getExt(extID)?.let {
		invoke(it, query, filters)
	} ?: throw Exception("Ext missing")

	@Throws(LuaException::class)
	operator fun invoke(
		ext: IExtension,
		query: String,
		filters: Map<Int, Any>
	): MyPagingSource = MyPagingSource(ext, query, filters)

}