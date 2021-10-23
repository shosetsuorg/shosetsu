package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.common.dto.HResult

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
	abstract fun setCSSId(int: Int): LiveData<HResult<*>>

	abstract val cssContent: LiveData<HResult<String>>
	abstract val cssTitle: LiveData<HResult<String>>

	abstract val isCSSValid: LiveData<Boolean>
	abstract val cssInvalidReason: LiveData<String?>

	abstract val canUndo: LiveData<Boolean>
	abstract val canRedo: LiveData<Boolean>

}