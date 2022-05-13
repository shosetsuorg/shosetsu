package app.shosetsu.android.domain.model.local


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
 * ====================================================================
 */

/**
 * shosetsu
 * 22 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 *
 * @param id of the repository, is nearly always not null, but only null when first being inserted
 * @param url of the repository. Must be a valid URL
 * @param name of the repository
 * @param isEnabled Is the repository enabled?
 *  Might be disabled due to error or user choice.
 */
data class RepositoryEntity(
	val id: Int,
	val url: String,
	var name: String,
	var isEnabled: Boolean
)