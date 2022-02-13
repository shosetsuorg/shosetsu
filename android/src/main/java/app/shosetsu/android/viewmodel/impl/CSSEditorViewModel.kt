package app.shosetsu.android.viewmodel.impl

import android.app.Application
import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.viewmodel.abstracted.ACSSEditorViewModel
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.StyleEntity
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.*

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
class CSSEditorViewModel(
	private val app: Application,
	private val settingsRepo: ISettingsRepository
) : ACSSEditorViewModel() {
	private val undoStack by lazy { Stack<String>() }
	private val redoStack by lazy { Stack<String>() }
	private val cssIDFlow by lazy { MutableStateFlow(-2) }
	private val cssContentFlow by lazy { MutableStateFlow("") }

	private val styleFlow by lazy {
		cssIDFlow.map { id ->
			StyleEntity(
				id,
				app.resources.getString(R.string.default_reader)
			)
		}
	}

	private val canUndoFlow by lazy { MutableStateFlow(false) }
	private val canRedoFlow by lazy { MutableStateFlow(false) }

	override fun undo() {
		logI("Undo")
		redoStack.add(cssContentFlow.value) // Save currentText as a redo action
		canRedoFlow.tryEmit(true)
		cssContentFlow.tryEmit(undoStack.pop())
		if (undoStack.size == 0) {
			canUndoFlow.tryEmit(false)
		}
	}

	override fun redo() {
		logI("Redo")
		undoStack.add(cssContentFlow.value)
		canUndoFlow.tryEmit(true)
		cssContentFlow.tryEmit(redoStack.pop())
		if (redoStack.size == 0) {
			canRedoFlow.tryEmit(false)
		}
	}

	override fun write(content: String) {
		launchIO {
			if (undoStack.size > 0 && undoStack.peek() == content) return@launchIO // ignore if nothing changed
			undoStack.add(cssContentFlow.value)
			canUndoFlow.tryEmit(true)
			redoStack.clear()
			canRedoFlow.tryEmit(false)
		}
		cssContentFlow.tryEmit(content)
	}

	override fun saveCSS() {
		launchIO {
			settingsRepo.setString(SettingKey.ReaderHtmlCss, cssContentFlow.value)
		}
	}

	override fun appendText(pasteContent: String) {
		val value = cssContentFlow.value
		val combined = value + pasteContent
		if (value == combined) return // ignore paste if the old value equals paste
		launchIO {
			if (undoStack.size > 0 && undoStack.peek() == combined) return@launchIO // ignore if nothing changed
			undoStack.add(value)
			canUndoFlow.tryEmit(true)
			redoStack.clear()
			canRedoFlow.tryEmit(false)
		}
		cssContentFlow.tryEmit(combined)
	}

	override fun setCSSId(int: Int): LiveData<Unit> =
		flow {
			when {
				int == cssIDFlow.value -> emit(Unit)
				int != cssIDFlow.value -> {
					undoStack.clear()
					redoStack.clear()
					cssContentFlow.emit("")
					cssIDFlow.emit(int)
					emit(Unit)
				}
				else -> {
				}
			}
		}.asIOLiveData()

	init {
		launchIO {
			styleFlow.collect { result ->
				result.let {
					settingsRepo.getString(SettingKey.ReaderHtmlCss).let {
						cssContentFlow.emit(it)
					}
				}
			}
		}
	}


	override val cssContent: LiveData<String> by lazy {
		cssContentFlow.map { it }.asIOLiveData()
	}

	override val cssTitle: LiveData<String> by lazy {
		styleFlow.map { it.title }.asIOLiveData()
	}

	override val isCSSValid: LiveData<Boolean> by lazy {
		flow { emit(true) }.asIOLiveData()
	}

	override val cssInvalidReason: LiveData<String?> by lazy {
		flow { emit(null) }.asIOLiveData()
	}

	override val canRedo: LiveData<Boolean> by lazy {
		canRedoFlow.asIOLiveData()
	}

	override val canUndo: LiveData<Boolean> by lazy {
		canUndoFlow.asIOLiveData()
	}
}