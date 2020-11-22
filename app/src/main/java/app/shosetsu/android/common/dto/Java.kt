package app.shosetsu.android.common.dto

import app.shosetsu.android.common.consts.ErrorKeys
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.InvalidParameterException

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


fun errorResult(e: InvalidParameterException): HResult.Error =
		HResult.Error(ErrorKeys.ERROR_NPE, e.message ?: "UnknownNullException", e)

fun errorResult(e: SocketTimeoutException): HResult.Error =
		HResult.Error(ErrorKeys.ERROR_TIMEOUT, e.message ?: "UnknownTimeoutException", e)

fun errorResult(e: UnknownHostException): HResult.Error =
		HResult.Error(ErrorKeys.ERROR_HOST_UNKNOWN, e.message ?: "Unknown-UnknownHostException", e)
