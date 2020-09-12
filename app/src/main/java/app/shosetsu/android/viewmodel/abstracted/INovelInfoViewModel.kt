package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.base.SubscribeHandleViewModel

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
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
abstract class INovelInfoViewModel
	: SubscribeHandleViewModel<NovelUI>, ViewModel() {
	/** Name of the formatter */
	abstract val formatterName: LiveData<HResult<String>>

	/** Set's the value to be loaded */
	abstract fun setNovelID(novelID: Int)

	/** Toggles the bookmark of this ui */
	abstract fun toggleBookmark(novelUI: NovelUI)
	abstract fun openBrowser(it: NovelUI)
	abstract fun openWebView(it: NovelUI)
	abstract fun share(it: NovelUI)
}