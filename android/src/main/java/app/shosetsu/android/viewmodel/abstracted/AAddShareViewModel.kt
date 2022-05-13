package app.shosetsu.android.viewmodel.abstracted

import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.lib.share.ExtensionLink
import app.shosetsu.lib.share.NovelLink
import app.shosetsu.lib.share.RepositoryLink
import kotlinx.coroutines.flow.Flow
import javax.security.auth.Destroyable

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
 * shosetsu
 * 01 / 10 / 2020
 */
abstract class AAddShareViewModel : ShosetsuViewModel(), Destroyable {

	abstract val isAdding: Flow<Boolean>
	abstract val isComplete: Flow<Boolean>

	/**
	 * Prompts UI to allow user to open novel
	 */
	abstract val isNovelOpenable: Flow<Boolean>

	abstract val isNovelAlreadyPresent: Flow<Boolean>
	abstract val isStyleAlreadyPresent: Flow<Boolean>
	abstract val isExtAlreadyPresent: Flow<Boolean>
	abstract val isRepoAlreadyPresent: Flow<Boolean>

	abstract val isProcessing: Flow<Boolean>

	abstract val isQRCodeValid: Flow<Boolean>

	abstract val novelLink: Flow<NovelLink?>
	abstract val extLink: Flow<ExtensionLink?>
	abstract val repoLink: Flow<RepositoryLink?>

	abstract val hasData: Flow<Boolean>

	abstract val exception: Flow<Exception?>

	/**
	 * Take the data from a correct QR code
	 */
	abstract fun takeData(url: String)

	/**
	 * Set that the QR code scanned is invalid
	 */
	abstract fun setInvalidQRCode()

	abstract fun add()

	abstract fun retry()

	abstract fun getNovel(): NovelEntity?
}