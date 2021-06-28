package app.shosetsu.android.domain

import app.shosetsu.android.common.HResultException
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.domain.usecases.toast.ToastErrorUseCase
import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
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
 * This class handles HResult error reporting in shosetsu
 * It will merge the settings provided by the [ISettingsRepository]
 * to determine how errors are reported
 *
 */
@Deprecated("Feed to UI instead")
class ReportExceptionUseCase(
	private val iSettingsRepository: ISettingsRepository,
	private val toastUseCase: ToastErrorUseCase
) {

	private fun report(result: HResult.Error, isSilent: Boolean = true) {
		try {
			ACRA.errorReporter
		} catch (e: IllegalStateException) {
			null
		}?.let { reporter ->
			(if (isSilent)
				reporter::handleSilentException
			else reporter::handleException).invoke(HResultException(result))
		}
	}

	private fun toast(result: HResult.Error) {
		toastUseCase<ReportExceptionUseCase>(result)
	}

	operator fun invoke(result: HResult.Error, isSilent: Boolean = true) {
		when (result.code) {
			ErrorKeys.ERROR_IMPOSSIBLE -> {
				report(result)
			}

			ErrorKeys.ERROR_GENERAL -> {
				report(result)
			}

			ErrorKeys.ERROR_LUA_GENERAL -> {
				logE("This is the lua error $result")
				report(result)
			}

			ErrorKeys.ERROR_NETWORK -> {
				toast(result)
			}

			ErrorKeys.ERROR_DUPLICATE -> {
				toast(result)
			}

			ErrorKeys.ERROR_NOT_FOUND -> {
				report(result)
			}

			ErrorKeys.ERROR_LUA_BROKEN -> {
				report(result)
			}

			ErrorKeys.ERROR_HTTP_ERROR -> {
				toast(result)
			}

			ErrorKeys.ERROR_HTTP_SQL -> {
				report(result)
			}

			ErrorKeys.ERROR_JSON -> {
				toast(result)
				report(result, isSilent)
			}

			ErrorKeys.ERROR_IO -> {
				toast(result)
				report(result, isSilent)
			}

			ErrorKeys.ERROR_LACK_PERM -> {
				toast(result)
				report(result, isSilent)
			}

			ErrorKeys.ERROR_NPE -> {
				toast(result)
				report(result, isSilent)
			}

			ErrorKeys.ERROR_EXT_INCOMPATIBLE -> {
				toast(result)
			}

			ErrorKeys.ERROR_FILE_IO -> {
				toast(result)
			}

			ErrorKeys.ERROR_NETWORK_IO -> {
				toast(result)
			}

			ErrorKeys.ERROR_HOST_UNKNOWN -> {
				toast(result)
			}

			ErrorKeys.ERROR_TIMEOUT -> {
				toast(result)
			}

			ErrorKeys.ERROR_INVALID_FEATURE -> {
				toast(result)
				report(result, isSilent)
			}
		}
	}
}