package app.shosetsu.android.viewmodel.abstracted

import app.shosetsu.android.common.utils.ColumnCalculator
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.viewmodel.base.*
import app.shosetsu.common.com.enums.NovelUIType

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
abstract class ILibraryViewModel :
		SubscribeHandleViewModel<List<ABookmarkedNovelUI>>,
		ShosetsuViewModel(),
		IsOnlineCheckViewModel,
		StartUpdateManagerViewModel,
		ErrorReportingViewModel,
		ColumnCalculator {

	abstract fun getNovelUIType(): NovelUIType

	/**
	 * Remove the following from the library
	 */
	abstract fun removeFromLibrary(list: List<ABookmarkedNovelUI>)
}