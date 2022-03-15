package app.shosetsu.android.viewmodel.abstracted

import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import kotlinx.coroutines.flow.Flow

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
 * @since 22 / 10 / 2021
 * @author Doomsdayrs
 */
abstract class ACSSEditorViewModel : ShosetsuViewModel() {
	abstract fun undo()
	abstract fun redo()

	/**
	 * Tell view model to save the CSS
	 */
	abstract fun saveCSS()

	abstract fun write(content: String)

	abstract fun appendText(pasteContent: String)
	abstract fun setCSSId(int: Int)

	abstract val cssContent: Flow<String>
	abstract val cssTitle: Flow<String>

	abstract val isCSSValid: Flow<Boolean>
	abstract val cssInvalidReason: Flow<String?>

	abstract val canUndo: Flow<Boolean>
	abstract val canRedo: Flow<Boolean>

}