package app.shosetsu.android.viewmodel.impl

import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.viewmodel.abstracted.ASplashScreenViewModel
import app.shosetsu.common.consts.settings.SettingKey.FirstTime
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import kotlinx.coroutines.flow.collectLatest

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
 * 08 / 12 / 2020
 */
class SplashScreenViewModel(
	private val settingsRepository: ISettingsRepository
) : ASplashScreenViewModel() {
	private var showIntro = FirstTime.default

	init {
		launchIO {
			settingsRepository.observeBoolean(FirstTime).collectLatest {
				logV("Collected $it")
				showIntro = it
			}
		}
	}

	override fun showIntro(): Boolean = showIntro

	override fun toggleShowIntro() {
		launchIO {
			settingsRepository.setBoolean(FirstTime, !showIntro)
		}
	}
}