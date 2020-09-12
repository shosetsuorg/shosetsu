package app.shosetsu.android.ui.reader.demarkActions

import app.shosetsu.android.backend.DeMarkAction
import app.shosetsu.android.ui.reader.ChapterReader

class ReaderChange(private val chapterReader: ChapterReader) : DeMarkAction {
	override fun action(spared: Int) {
		//chapterReader.readerType = spared;
	}
}