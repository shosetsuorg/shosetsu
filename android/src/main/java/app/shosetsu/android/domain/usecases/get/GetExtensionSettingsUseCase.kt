package app.shosetsu.android.domain.usecases.get

import app.shosetsu.common.domain.model.local.FilterEntity
import app.shosetsu.common.domain.repositories.base.IExtensionSettingsRepository
import app.shosetsu.common.enums.TriStateState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

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
 * 14 / 09 / 2020
 *
 * Loads the extension settings
 */
class GetExtensionSettingsUseCase(
	private val extSettingsRepository: IExtensionSettingsRepository,
	private val getExt: GetExtensionUseCase
) {
	@OptIn(ExperimentalCoroutinesApi::class)
	private fun asSettingItem(
		extensionID: Int,
		filter: FilterEntity.Switch
	): Flow<FilterEntity> =
		extSettingsRepository.getBooleanFlow(
			extensionID,
			filter.id,
			filter.state
		).mapLatest { state ->
			filter.copy(state = state)
		}

	@OptIn(ExperimentalCoroutinesApi::class)
	private fun asSettingItem(
		extensionID: Int,
		filter: FilterEntity.Checkbox
	): Flow<FilterEntity> =
		extSettingsRepository.getBooleanFlow(
			extensionID,
			filter.id,
			filter.state
		).mapLatest { state ->
			filter.copy(state = state)
		}

	/**
	 * Converts a [List] of [FilterEntity] into a [List] of [Flow]s of [FilterEntity]s
	 */
	@OptIn(ExperimentalCoroutinesApi::class)
	private suspend fun List<FilterEntity>.convert(extensionID: Int): List<Flow<FilterEntity>> =
		map { filter ->
			when (filter) {
				is FilterEntity.Text -> {
					extSettingsRepository.getStringFlow(
						extensionID,
						filter.id,
						filter.state
					).mapLatest { state ->
						filter.copy(state = state)
					}
				}
				is FilterEntity.Switch -> {
					asSettingItem(extensionID, filter)
				}
				is FilterEntity.Checkbox -> {
					asSettingItem(extensionID, filter)
				}
				is FilterEntity.TriState -> {
					extSettingsRepository.getStringFlow(
						extensionID,
						filter.id,
						filter.state.name
					).mapLatest { newState ->
						filter.copy(state = TriStateState.valueOf(newState))
					}
				}
				is FilterEntity.Dropdown -> {
					extSettingsRepository.getIntFlow(
						extensionID,
						filter.id,
						filter.selected
					).mapLatest { state ->
						filter.copy(selected = state)
					}
				}
				is FilterEntity.RadioGroup -> {
					extSettingsRepository.getIntFlow(
						extensionID,
						filter.id,
						filter.selected
					).mapLatest { state ->
						//TODO RadioGroup
						filter.copy(selected = state)
					}
				}
				is FilterEntity.FList -> {
					filter.filters.convert(extensionID).combine().mapLatest { subList ->
						filter.copy(filters = subList)
					}
				}
				is FilterEntity.Group -> {
					filter.filters.convert(extensionID).combine().mapLatest { subList ->
						filter.copy(filters = subList)
					}
				}
				is FilterEntity.Header -> flow {
					emit(filter)
				}
				is FilterEntity.Separator -> flow {
					emit(filter)
				}
			}
		}

	private fun List<Flow<FilterEntity>>.combine(): Flow<List<FilterEntity>> =
		combine(this) { it.toList() }

	operator fun invoke(extensionID: Int): Flow<List<FilterEntity>> =
		flow {
			if (extensionID == -1) {
				emit(emptyList())
				return@flow
			}

			getExt(extensionID)?.let { extension ->
				val list: List<FilterEntity> =
					extension.settingsModel.map { FilterEntity.fromFilter(it) }

				emitAll(list.convert(extensionID).combine())
			}
		}
}