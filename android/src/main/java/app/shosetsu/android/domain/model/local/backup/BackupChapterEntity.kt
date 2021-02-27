package app.shosetsu.android.domain.model.local.backup

import app.shosetsu.common.enums.ReadingStatus
import kotlinx.serialization.Serializable

/**
 * @param rS ReadingStatus
 * @param rP Reading position
 */
@Serializable
data class BackupChapterEntity(
	val url: String,
	val name: String,
	val bookmarked: Boolean,
	val rS: ReadingStatus,
	val rP: Double
)