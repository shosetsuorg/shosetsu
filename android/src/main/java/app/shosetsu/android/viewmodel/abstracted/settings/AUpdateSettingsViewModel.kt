package app.shosetsu.android.viewmodel.abstracted.settings

import app.shosetsu.common.domain.repositories.base.ISettingsRepository

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
 * 31 / 08 / 2020
 */
abstract class AUpdateSettingsViewModel(iSettingsRepository: ISettingsRepository) :
	ASubSettingsViewModel(iSettingsRepository) {
	/**
	 * Has the novel updater settings changed?
	 */
	abstract var novelUpdateSettingsChanged: Boolean

	/**
	 * Has the repository updater settings changed?
	 */
	abstract var repoUpdateSettingsChanged: Boolean

	/**
	 * Restart the Novel updater to apply the new settings
	 */
	abstract fun restartNovelUpdater()

	/**
	 * Restart the Repository Updater to apply the new settings
	 */
	abstract fun restartRepoUpdater()
}