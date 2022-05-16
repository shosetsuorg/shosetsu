package app.shosetsu.android.domain.usecases

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.ext.generify
import app.shosetsu.android.common.utils.asIEntity
import app.shosetsu.android.domain.model.local.GenericExtensionEntity
import app.shosetsu.android.domain.model.local.InstalledExtensionEntity
import app.shosetsu.android.domain.repository.base.IExtensionEntitiesRepository
import app.shosetsu.android.domain.repository.base.IExtensionRepoRepository
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.domain.repository.base.IExtensionsRepository.InstallExtensionFlags
import app.shosetsu.lib.Novel
import app.shosetsu.lib.exceptions.HTTPException
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
	@Throws(
		HTTPException::class,
		SQLiteException::class,
		FilePermissionException::class,
		IOException::class
	)
	suspend operator fun invoke(extToInstall: GenericExtensionEntity): InstallExtensionFlags {
		val repo = extensionRepoRepository.getRepo(extToInstall.repoID)!!

		val extensionContent: ByteArray = extensionRepository.downloadExtension(
			repo,
			extToInstall
		)

		val iExt = extToInstall.asIEntity(extensionContent)


		val oldType: Novel.ChapterType?
		val deleteChapters: Boolean

		val oldInstalledExt = extensionRepository.getInstalledExtension(extToInstall.id)

		if (oldInstalledExt != null && oldInstalledExt.version < iExt.exMetaData.version) {
			oldType = oldInstalledExt.chapterType
			deleteChapters = oldType != iExt.chapterType
		} else {
			oldType = null
			deleteChapters = false
		}

		// Uninstall the currently installed version of the extension
		if (oldInstalledExt != null)
			extensionEntitiesRepository.uninstall(oldInstalledExt.generify())

		// Write to storage/cache
		extensionEntitiesRepository.save(extToInstall, iExt, extensionContent)

		if (oldInstalledExt != null)
			extensionRepository.updateInstalledExtension(
				oldInstalledExt.copy(
					repoID = extToInstall.repoID,
					name = extToInstall.fileName,
					fileName = extToInstall.fileName,
					imageURL = extToInstall.imageURL,
					lang = extToInstall.lang,
					version = extToInstall.version,
					md5 = extToInstall.md5,
					type = extToInstall.type,
					chapterType = iExt.chapterType
				)
			)
		else {
			extensionRepository.insert(
				InstalledExtensionEntity(
					id = extToInstall.id,
					repoID = extToInstall.repoID,
					name = extToInstall.name,
					fileName = extToInstall.fileName,
					imageURL = extToInstall.imageURL,
					lang = extToInstall.lang,
					version = extToInstall.version,
					md5 = extToInstall.md5,
					type = extToInstall.type,
					enabled = true,
					chapterType = iExt.chapterType
				)
			)
		}

		return InstallExtensionFlags(
			deleteChapters,
			oldType
		)
	}
}