package app.shosetsu.android.domain.repository.base

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.domain.model.local.RepositoryEntity
import org.json.JSONObject

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
 *
 * @author github.com/doomsdayrs
 */
interface IExtRepoRepository {
	/** Loads repository data */
	suspend fun loadRepoDataJSON(repositoryEntity: RepositoryEntity): HResult<JSONObject>

	/** Loads all repositories present */
	suspend fun loadRepositories(): HResult<List<RepositoryEntity>>

	/** Loads all repositories present */
	suspend fun loadRepositoriesLive(): LiveData<HResult<List<RepositoryEntity>>>
}