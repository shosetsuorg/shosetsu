package app.shosetsu.android.common.utils.share

import app.shosetsu.lib.share.NovelLink
import java.net.URLEncoder

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
 *
 * @since 06 / 03 / 2022
 * @author Doomsdayrs
 */
val BASE_SHARE_URL: String = "https://share.shosetsu.app"

fun String.urlEncode(): String {
	@Suppress("CheckedExceptionsKotlin") // utf-8 likely wont be deprecated
	return URLEncoder.encode(this, "utf-8")
}

fun NovelLink.toURL(): String =
	"$BASE_SHARE_URL/novel" +
			"?name=${name.urlEncode()}" +
			"&url=${url.urlEncode()}" +
			"&imageURL=${imageURL.urlEncode()}" +
			"&extID=${extensionQRCode.id}" +
			"&extURL=${extensionQRCode.imageURL.urlEncode()}" +
			"&extName=${extensionQRCode.name.urlEncode()}" +
			"&repoName=${extensionQRCode.repo.name.urlEncode()}" +
			"&repoURL=${extensionQRCode.repo.url.urlEncode()}"