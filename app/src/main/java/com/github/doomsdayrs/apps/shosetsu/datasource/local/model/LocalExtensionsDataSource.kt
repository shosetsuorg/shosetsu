package com.github.doomsdayrs.apps.shosetsu.datasource.local.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_LUA
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.utils.base.IFormatterUtils
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalExtensionsDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImage
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ExtensionsDao
import org.luaj.vm2.LuaError

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */


/**
 * shosetsu
 * 12 / May / 2020
 */
class LocalExtensionsDataSource(
		val extensionsDao: ExtensionsDao,
		val context: Context,
		val formatterUtils: IFormatterUtils
) : ILocalExtensionsDataSource {
	override suspend fun loadExtensions(): LiveData<HResult<List<ExtensionEntity>>> {
		return liveData {
			emitSource(extensionsDao.loadFormatters().map {
				successResult(it)
			})
		}
	}

	override suspend fun loadPoweredExtensionsCards(): LiveData<HResult<List<IDTitleImage>>> {
		return liveData {
			emitSource(extensionsDao.loadPoweredFormattersBasic().map { list ->
				successResult(list.map { IDTitleImage(it.id, it.name, it.imageURL) })
			})
		}
	}

	override suspend fun updateExtension(extensionEntity: ExtensionEntity) {
		TODO("Not yet implemented")
	}

	override suspend fun deleteExtension(extensionEntity: ExtensionEntity) {
		TODO("Not yet implemented")
	}

	override suspend fun loadFormatterFromFiles(extensionEntity: ExtensionEntity): HResult<Formatter> {
		return try {
			successResult(LuaFormatter(formatterUtils.makeFormatterFile(extensionEntity)))
		} catch (e: LuaError) {
			errorResult(ERROR_LUA, e.message ?: "Unknown Lua Error")
		}
	}
}