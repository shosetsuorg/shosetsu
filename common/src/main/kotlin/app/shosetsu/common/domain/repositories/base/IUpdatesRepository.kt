package app.shosetsu.common.domain.repositories.base
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
import app.shosetsu.common.domain.model.local.UpdateCompleteEntity
import app.shosetsu.common.domain.model.local.UpdateEntity
import app.shosetsu.common.dto.HResult
import kotlinx.coroutines.flow.Flow


/**
 * shosetsu
 * 25 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface IUpdatesRepository {
	/**
	 * Adds updates
	 *
	 * @return
	 * [HResult.Success] Added, returning rowIDs
	 *
	 * [HResult.Error] Something went wrong adding
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun addUpdates(list: List<UpdateEntity>): HResult<Array<Long>>

	/**
	 * [HResult] [Flow] of [List] of [UpdateEntity] of all entities present
	 *
	 * @return
	 * [HResult.Success] Successfully retrieved updates
	 *
	 * [HResult.Error] Something went wrong getting the updates
	 *
	 * [HResult.Empty] No updates
	 *
	 * [HResult.Loading] Initial value
	 */
	suspend fun getUpdatesFlow(): Flow<HResult<List<UpdateEntity>>>

	/**
	 * [HResult] [Flow] of [List] of [UpdateCompleteEntity] of all entities present
	 *
	 * @return
	 * [HResult.Success] Successfully retrieved fleshed out updates
	 *
	 * [HResult.Error] Something went wrong getting the updates
	 *
	 * [HResult.Empty] No updates
	 *
	 * [HResult.Loading] Initial value
	 */
	suspend fun getCompleteUpdatesFlow(): Flow<HResult<List<UpdateCompleteEntity>>>
}