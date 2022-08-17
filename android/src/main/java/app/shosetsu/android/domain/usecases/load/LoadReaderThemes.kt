package app.shosetsu.android.domain.usecases.load

import android.content.Context
import androidx.core.content.ContextCompat
import app.shosetsu.android.R
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.utils.uifactory.mapToFactory
import app.shosetsu.android.domain.model.local.ColorChoiceData
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.view.uimodels.model.ColorChoiceUI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

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
 * 24 / 09 / 2020
 */
class LoadReaderThemes(
	private val iSettingsRepository: ISettingsRepository,
	private val context: Context
) {
	@OptIn(ExperimentalCoroutinesApi::class)
	operator fun invoke(): Flow<List<ColorChoiceUI>> {
		return iSettingsRepository.getStringSetFlow(SettingKey.ReaderUserThemes)
			.mapLatest { set: Set<String> ->

				(if (set.isNotEmpty())
					set.map { ColorChoiceData.fromString(it) }
				else listOf(
					ColorChoiceData(
						-1,
						context.getString(R.string.light),
						-0x1000000,
						-0x1
					),
					ColorChoiceData(
						-2,
						context.getString(R.string.light_dark),
						-0x333334,
						-0xbbbbbc
					),
					ColorChoiceData(
						-3,
						context.getString(R.string.sepia),
						-0x1000000,
						ContextCompat.getColor(context, R.color.wheat).also {
							logE("Hey here is the color you need: $it")
						}
					),
					ColorChoiceData(
						-4,
						context.getString(R.string.amoled),
						-0x1,
						-0x1000000
					)
				).also {
					launchIO {
						it.map { it.toString() }.toSet().let {
							iSettingsRepository.setStringSet(SettingKey.ReaderUserThemes, it)
						}
					}
				}).mapToFactory().convertList()
			}
	}
}