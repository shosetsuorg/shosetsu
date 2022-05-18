package app.shosetsu.android.view.uimodels.model

import app.shosetsu.android.domain.model.local.LibraryNovelEntity
import app.shosetsu.android.dto.Convertible

data class LibraryNovelUI(
	val id: Int,
	val title: String,
	val imageURL: String,
	var bookmarked: Boolean,
	val unread: Int,
	val genres: List<String>,
	val authors: List<String>,
	val artists: List<String>,
	val tags: List<String>,
	val isSelected: Boolean = false
) : Convertible<LibraryNovelEntity> {
	override fun convertTo(): LibraryNovelEntity =
		LibraryNovelEntity(
			id,
			title,
			imageURL,
			bookmarked,
			unread,
			genres,
			authors,
			artists,
			tags
		)
}