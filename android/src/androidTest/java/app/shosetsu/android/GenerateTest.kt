package app.shosetsu.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import app.shosetsu.android.common.utils.share.toURL
import app.shosetsu.lib.share.ExtensionLink
import app.shosetsu.lib.share.NovelLink
import app.shosetsu.lib.share.RepositoryLink
import org.junit.Test

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Shosetsu
 *
 * @since 06 / 03 / 2022
 * @author Doomsdayrs
 */
class GenerateTest {
	private val context: Context by lazy {
		InstrumentationRegistry.getInstrumentation().targetContext
	}

	private val data: NovelLink =
		NovelLink(
			"Novel",
			"https://www.phoronix.net/image.php?id=2022&image=nvapi",
			"https://www.phoronix.com/scan.php?page=news_item&px=DXVK-NVAPI-0.5.3",
			ExtensionLink(
				1,
				"Extension",
				"https://www.phoronix.net/image.php?id=2022&image=nvapi",
				RepositoryLink(
					"Repo",
					"https://www.phoronix.com/scan.php?page=news_item&px=DXVK-NVAPI-0.5.3"
				)
			)
		)

	@Test
	fun generateQRCodeTest() {
		println(data.toURL())
	}
}