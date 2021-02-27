package app.shosetsu.android.view.uimodels.model.reader

import android.view.View
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logError
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.ui.reader.ChapterReader
import app.shosetsu.android.ui.reader.types.base.ReaderChapterViewHolder
import app.shosetsu.android.ui.reader.types.model.HtmlReader
import app.shosetsu.android.ui.reader.types.model.StringReader
import app.shosetsu.common.domain.model.local.ReaderChapterEntity
import app.shosetsu.common.dto.Convertible
import app.shosetsu.common.dto.handle
import app.shosetsu.common.enums.ReadingStatus
import app.shosetsu.common.utils.asHtml
import app.shosetsu.lib.Novel.ChapterType
import com.github.doomsdayrs.apps.shosetsu.R

/**
 * Data class that holds each chapter and its data (not including text content)
 *
 * @param id Id of the chapter in shosetsu db
 * @param link URL of the chapter
 * @param readingPosition Where the user last left off while reading
 * @param readingStatus What is the reading status of the chapter
 * @param bookmarked Is the chapter bookmarked
 * @param chapterType What type of [ReaderChapterViewHolder] to use for loading,
 * this is defined by the the extension first,
 * otherwise the user choice will dictate what reader is used
 *
 * @param convertStringToHtml Convert a string chapter to an html chapter
 */
data class ReaderChapterUI(
	val id: Int,
	val link: String,
	val title: String,
	var readingPosition: Double,
	var readingStatus: ReadingStatus,
	var bookmarked: Boolean,
	private val chapterType: ChapterType,
	private val convertStringToHtml: Boolean = false
) : Convertible<ReaderChapterEntity>, ReaderUIItem<ReaderChapterUI, ReaderChapterViewHolder>() {
	override var identifier: Long
		get() = id.toLong()
		set(_) {}

	var chapterReader: ChapterReader? = null
		set(value) {
			field = value?.apply {
				viewHolder?.chapterReader = value
			}
		}

	var viewHolder: ReaderChapterViewHolder? = null
		set(value) {
			field = value?.apply {
				logV("Applying view holder")
				this.chapter = this@ReaderChapterUI
				this@ReaderChapterUI.chapterReader?.let {
					this.chapterReader = it
				} ?: logE("ChapterReader reference is null")
				this.chapterReader.syncReader(this)
			}
		}

	override val layoutRes: Int by lazy {
		when (chapterType) {
			ChapterType.STRING -> if (!convertStringToHtml) R.layout.chapter_reader_text_view else R.layout.chapter_reader_html
			ChapterType.HTML -> R.layout.chapter_reader_html
			ChapterType.MARKDOWN -> R.layout.chapter_reader_mark_down

			ChapterType.EPUB -> R.layout.chapter_reader_text_view
			ChapterType.PDF -> R.layout.chapter_reader_text_view
		}
	}

	override val type: Int by lazy {
		when (chapterType) {
			ChapterType.STRING -> if (!convertStringToHtml) R.layout.chapter_reader_text_view else R.layout.chapter_reader_html
			ChapterType.HTML -> R.layout.chapter_reader_html
			ChapterType.MARKDOWN -> R.layout.chapter_reader_mark_down

			ChapterType.EPUB -> R.layout.chapter_reader_text_view
			ChapterType.PDF -> R.layout.chapter_reader_text_view
		}
	}

	override fun getViewHolder(v: View): ReaderChapterViewHolder {
		return when (chapterType) {
			ChapterType.STRING -> if (!convertStringToHtml) StringReader(v) else HtmlReader(v)
			ChapterType.HTML -> HtmlReader(v)
			else -> TODO("Not implemented")
		}
	}

	override fun bindView(holder: ReaderChapterViewHolder, payloads: List<Any>) {
		super.bindView(holder, payloads)
		viewHolder = holder
		chapterReader?.let { reader ->
			reader.viewModel.getChapterPassage(this).observe(reader) { result ->
				result.handle(
					{ holder.showLoadingProgress() },
					{ holder.hideLoadingProgress() },
					{
						logError { it }
						//	holder.setError(it.message, "Retry") {
						//		TODO("Figure out how to restart the liveData")
						//		}
					}) {
					//logD("Successfully loaded :D")
					holder.hideLoadingProgress()
					holder.setData(if (!convertStringToHtml) it else asHtml(it, title = title))
					holder.itemView.post {
						holder.setProgress(this.readingPosition)
					}
				}
			}
		}
	}

	override fun convertTo(): ReaderChapterEntity = ReaderChapterEntity(
		id,
		link,
		title,
		readingPosition,
		readingStatus,
		bookmarked
	)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as ReaderChapterUI

		if (id != other.id) return false
		if (link != other.link) return false
		if (title != other.title) return false
		if (bookmarked != other.bookmarked) return false
		if (chapterType != other.chapterType) return false
		if (convertStringToHtml != other.convertStringToHtml) return false
		return true
	}

	override fun hashCode(): Int {
		var result = id
		result = 31 * result + link.hashCode()
		result = 31 * result + title.hashCode()
		result = 31 * result + bookmarked.hashCode()
		result = 31 * result + chapterType.hashCode()
		result = 31 * result + convertStringToHtml.hashCode()
		return result
	}
}