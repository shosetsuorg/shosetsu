package app.shosetsu.android.domain.usecases

import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.domain.model.local.GenericExtensionEntity
import app.shosetsu.common.domain.repositories.base.IExtensionEntitiesRepository
import app.shosetsu.common.domain.repositories.base.IExtensionRepoRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.utils.asIEntity
import app.shosetsu.lib.Novel
import app.shosetsu.lib.exceptions.HTTPException

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
 * Shosetsu
 *
 * @since 16 / 08 / 2021
 * @author Doomsdayrs
 */
class InstallExtensionUseCase(
	private val extensionRepository: IExtensionsRepository,
	private val extensionEntitiesRepository: IExtensionEntitiesRepository,
	private val extensionRepoRepository: IExtensionRepoRepository
) {
	@Throws(HTTPException::class)
	suspend operator fun invoke(extensionEntity: GenericExtensionEntity): IExtensionsRepository.InstallExtensionFlags {
		val repo = extensionRepoRepository.getRepo(extensionEntity.repoID)!!

		val extensionContent: ByteArray = extensionRepository.downloadExtension(
			repo,
			extensionEntity
		)

		try {
			val iExt = extensionEntity.asIEntity(extensionContent)

			// Write to storage/cache
			extensionEntitiesRepository.save(extensionEntity, iExt, extensionContent)

			val oldType: Novel.ChapterType?
			val deleteChapters: Boolean

			if (extensionEntity.installedVersion != null && extensionEntity.installedVersion!! < iExt.exMetaData.version) {
				oldType = extensionEntity.chapterType
				deleteChapters = oldType != iExt.chapterType
			} else {
				deleteChapters = false
				oldType = null
			}

			extensionEntity.chapterType = iExt.chapterType
			extensionRepository.updateRepositoryExtension(
				extensionEntity.copy(
					chapterType = iExt.chapterType
				)
			)

			successResult(
				IExtensionsRepository.InstallExtensionFlags(
					deleteChapters,
					oldType
				)
			)


		} catch (e: IllegalArgumentException) {
			errorResult(ErrorKeys.ERROR_LUA_BROKEN, e)
		} catch (e: Exception) {
			errorResult(ErrorKeys.ERROR_GENERAL, e)
		}
	}
}