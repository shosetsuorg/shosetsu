package app.shosetsu.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import app.shosetsu.android.domain.usecases.get.GetChapterPassageUseCase
import app.shosetsu.android.domain.usecases.get.GetReaderChaptersUseCase
import app.shosetsu.android.domain.usecases.load.LoadLibraryUseCase
import app.shosetsu.common.utils.asHtml
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.future.future
import org.junit.Test
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * 19 / 02 / 2021
 */
class StringChapterAsHtmlTest : DIAware {

	private val context: Context by lazy {
		InstrumentationRegistry.getInstrumentation().targetContext
	}

	override val di: DI by closestDI(context)

	private val getReaderChapterUseCase: GetReaderChaptersUseCase by instance()

	private val getBookMarkedNovelsUseCase: LoadLibraryUseCase by instance()
	private val getChapterPassageUseCase: GetChapterPassageUseCase by instance()

	@Test
	fun main() {
		GlobalScope.future {
			getBookMarkedNovelsUseCase().collectLatest { novelList ->
				getReaderChapterUseCase(novelList.first().id).collectLatest { chapterList ->
					val bytes = getChapterPassageUseCase(chapterList.first())
					println(asHtml(bytes.toString(), title = chapterList.first().title))
				}
			}
		}.join()
	}
}