package app.shosetsu.android.domain.model.local.backup

import kotlinx.serialization.Serializable

/**
 * Each extension that needs to be installed
 * @param novels novels to add after word
 */
@Serializable
data class BackupExtensionEntity(
	val id: Int,
	val novels: List<BackupNovelEntity> = emptyList(),
)
