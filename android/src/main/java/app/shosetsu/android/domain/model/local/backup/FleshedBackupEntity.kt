package app.shosetsu.android.domain.model.local.backup

import kotlinx.serialization.Serializable

/**
 * @param repos that must be added
 * @param extensions is a tree to lower redundant data duplication
 */
@Serializable
data class FleshedBackupEntity(
	val version: String = "1.0.0",
	val repos: List<BackupRepositoryEntity>,
	val extensions: List<BackupExtensionEntity>,
)