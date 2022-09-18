package app.shosetsu.android.domain.model.local.backup

import app.shosetsu.android.common.consts.VERSION_BACKUP
import kotlinx.serialization.Serializable

/**
 * @param repos that must be added
 * @param extensions is a tree to lower redundant data duplication
 */
@Serializable
data class FleshedBackupEntity(
	val version: String = VERSION_BACKUP,
	val repos: List<BackupRepositoryEntity> = emptyList(),
	val extensions: List<BackupExtensionEntity> = emptyList(),
	val categories: List<BackupCategoryEntity> = emptyList()
)

@Serializable
data class MetaBackupEntity(
	val version: String?,
)