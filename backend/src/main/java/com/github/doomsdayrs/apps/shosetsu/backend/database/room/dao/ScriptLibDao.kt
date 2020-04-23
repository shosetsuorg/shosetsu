package com.github.doomsdayrs.apps.shosetsu.backend.database.room.dao

import androidx.room.*
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.ExtensionLibraryEntity

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
 * ====================================================================
 */

/**
 * shosetsu
 * 22 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Dao
interface ScriptLibDao {
	@Insert(onConflict = OnConflictStrategy.IGNORE, entity = ExtensionLibraryEntity::class)
	fun insertScriptLib(extensionLibraryEntity: ExtensionLibraryEntity)

	@Query("SELECT * FROM libs WHERE repositoryID = :repositoryID")
	fun loadLibByRepoID(repositoryID: Int): Array<ExtensionLibraryEntity>

	@Update
	fun updateScriptLib(extensionLibraryEntity: ExtensionLibraryEntity)

	@Query("SELECT COUNT(*) FROM libs WHERE scriptName = :name")
	fun scriptLibCountFromName(name: String): Int

	@Ignore
	fun doesRepositoryExist(url: String): Boolean = scriptLibCountFromName(url) > 0

	@Transaction
	fun insertOrUpdateScriptLib(extensionLibraryEntity: ExtensionLibraryEntity) {
		if (scriptLibCountFromName(extensionLibraryEntity.scriptName) > 0) {
			updateScriptLib(extensionLibraryEntity)
		} else insertScriptLib(extensionLibraryEntity)
	}

}
