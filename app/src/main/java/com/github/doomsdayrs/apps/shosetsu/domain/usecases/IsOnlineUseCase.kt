package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.core.content.getSystemService

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
 * 04 / 09 / 2020
 */
class IsOnlineUseCase(
		private val application: Application
) {
	private val connectivityManager by lazy {
		application.getSystemService<ConnectivityManager>()!!
	}

	operator fun invoke(): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val networkCapabilities = connectivityManager.activeNetwork ?: return false
			val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities)
					?: return false
			when {
				actNw.hasTransport(TRANSPORT_BLUETOOTH) -> true
				actNw.hasTransport(TRANSPORT_CELLULAR) -> true
				actNw.hasTransport(TRANSPORT_ETHERNET) -> true
				actNw.hasTransport(TRANSPORT_VPN) -> true
				actNw.hasTransport(TRANSPORT_WIFI) -> true
				else -> false
			}
		} else {
			// Suppressing warnings since this is old API usage
			@Suppress("DEPRECATION")
			val type = connectivityManager.activeNetworkInfo ?: return false
			@Suppress("DEPRECATION")
			when (type.type) {
				ConnectivityManager.TYPE_WIFI -> true
				ConnectivityManager.TYPE_MOBILE -> true
				ConnectivityManager.TYPE_ETHERNET -> true
				ConnectivityManager.TYPE_VPN -> true
				ConnectivityManager.TYPE_BLUETOOTH -> true
				else -> false
			}
		}
	}
}