package app.shosetsu.android.view.uimodels.model

import androidx.compose.runtime.Immutable
import app.shosetsu.android.domain.model.local.ExtensionInstallOptionEntity
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
data class BrowseExtensionUI(
    val id: Int,
    val name: String,
    val imageURL: String,
    val lang: String,
    val installOptions: List<ExtensionInstallOptionEntity>? = null,
    val isInstalled: Boolean,
    val installedVersion: Version? = null,
    val installedRepo: Int,
    val isUpdateAvailable: Boolean,
    val updateVersion: Version? = null,
    val isInstalling: Boolean
) {
    val displayLang: String = Locale.forLanguageTag(lang).displayName
}
