package app.shosetsu.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import app.shosetsu.common.utils.asHtml
import app.shosetsu.android.domain.usecases.get.GetChapterPassageUseCase
import app.shosetsu.android.domain.usecases.get.GetReaderChaptersUseCase
import app.shosetsu.android.domain.usecases.load.LoadLibraryUseCase
import app.shosetsu.common.dto.handle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.future.future
import org.junit.Test
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

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
class StringChapterAsHtmlTest : KodeinAware {

	private val context: Context by lazy {
		InstrumentationRegistry.getInstrumentation().targetContext
	}

	override val kodein: Kodein by closestKodein(context)

	private val getReaderChapterUseCase: GetReaderChaptersUseCase by instance()

	private val getBookMarkedNovelsUseCase: LoadLibraryUseCase by instance()
	private val getChapterPassageUseCase: GetChapterPassageUseCase by instance()

	@Test
	fun main() {
		GlobalScope.future {
			getBookMarkedNovelsUseCase().collectLatest { novelResult ->
				novelResult.handle { novelList ->
					getReaderChapterUseCase(novelList.first().id).collectLatest { chapterResult ->
						chapterResult.handle { chapterList ->
							getChapterPassageUseCase(chapterList.first()).handle {
								println(asHtml(it, title = chapterList.first().title))
							}
						}
					}
				}
			}
		}.join()
	}
}