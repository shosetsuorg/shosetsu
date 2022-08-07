package app.shosetsu.android.view.uimodels.model

import androidx.compose.runtime.Immutable
import app.shosetsu.android.domain.model.local.InstalledExtensionEntity
import app.shosetsu.android.dto.Convertible
import app.shosetsu.lib.ExtensionType
import app.shosetsu.lib.Novel
import app.shosetsu.lib.Version
import java.util.Locale

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
 * ====================================================================
 */

@Immutable
data class InstalledExtensionUI(
    val id: Int,
    val repoID: Int,
    val name: String,
    val fileName: String,
    val imageURL: String,
    val lang: String,
    val version: Version,
    val md5: String,
    val type: ExtensionType,
    val enabled: Boolean,
    val chapterType: Novel.ChapterType,
) : Convertible<InstalledExtensionEntity> {
    val displayLang: String = Locale.forLanguageTag(lang).displayName

    override fun convertTo(): InstalledExtensionEntity  = InstalledExtensionEntity(
        id = id,
        repoID = repoID,
        name = name,
        fileName = fileName,
        imageURL = imageURL,
        lang = lang,
        version = version,
        md5 = md5,
        type = type,
        enabled = enabled,
        chapterType = chapterType,
    )
}
