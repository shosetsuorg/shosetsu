package app.shosetsu.android.domain.usecases.get

import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.common.domain.repositories.base.INovelReaderSettingsRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transformLatest

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
 * 24 / 02 / 2021
 */
class GetReaderSettingUseCase(
	private val readerRepo: INovelReaderSettingsRepository,
	private val settingsRepo: ISettingsRepository,
) {
	@ExperimentalCoroutinesApi
	operator fun invoke(novelID: Int): Flow<NovelReaderSettingEntity> = flow {
		emitAll(readerRepo.getFlow(novelID).transformLatest { result ->
			if (result != null) {
				emit(result)
			} else {
				readerRepo.insert(
					NovelReaderSettingEntity(
						novelID,
						settingsRepo.getInt(SettingKey.ReaderIndentSize),
						settingsRepo.getFloat(SettingKey.ReaderParagraphSpacing),
					)
				)
			}
		})
	}
}