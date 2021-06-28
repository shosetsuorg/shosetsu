package app.shosetsu.android.viewmodel.base

import app.shosetsu.android.common.HResultException
import app.shosetsu.common.dto.HResult.Error
import org.acra.ACRA

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
 * 26 / 10 / 2020
 */
interface ErrorReportingViewModel {

	/**
	 * Reports an error to shosetsuOrg
	 */
	fun reportError(error: Error, isSilent: Boolean = true)

	fun basicReport(error: Error, isSilent: Boolean = true) {
		val reporter = try {
			ACRA.errorReporter
		} catch (e: IllegalStateException) {
			null
		}
		reporter?.let {
			(if (isSilent)
				reporter::handleSilentException
			else reporter::handleException).invoke(HResultException(error))
		}
	}
}