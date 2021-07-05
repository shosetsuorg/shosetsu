package app.shosetsu.android.domain.model.local.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupNovelEntity(
	val url: String,
	val name: String,
	val imageURL: String = "",
	val chapters: List<BackupChapterEntity> = listOf(),
	val settings: BackupNovelSettingEntity = BackupNovelSettingEntity()
)