package com.github.doomsdayrs.apps.shosetsu.backend

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.MenuItem
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import java.util.*

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
 * ====================================================================
 */
/**
 * shosetsu
 * 26 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 *
 * <p>
 *     This file contains random methods/pieces of code that don't seem to be important or make their respective files messy
 * </p>
 */
object Utilities {
	internal class SHOWCASE {
		val catalogue = 1
		val downloads = 2
		val library = 3
		val main = 4
		val migration = 5
		val novel = 6
		val novelINFO = 7
		val novelCHAPTERS = 8
		val novelTRACKING = 9
		val reader = 10
		val search = 11
		val updates = 12
		val webView = 13
	}

	/**
	 * global connectivity manager variable
	 */
	var connectivityManager: ConnectivityManager? = null

	const val selectedStrokeWidth = 8
	var shoDir: String = "/Shosetsu/"

	fun regret(context: Context) = context.toast(R.string.regret, duration = LENGTH_LONG)

	fun setActivityTitle(activity: Activity?, title: String?) {
		activity?.let { it -> if (it is AppCompatActivity) it.supportActionBar?.let { it.title = title } }
	}

	/**
	 * Demarks a list of items, setting only one to be checked.
	 *
	 * @param menuItems      Items to sort through
	 * @param positionSpared Item to set checked
	 * @param demarkAction   Any action to proceed with
	 */
	fun unmarkMenuItems(menuItems: Array<MenuItem>, positionSpared: Int, demarkAction: DeMarkAction?) {
		for (x in menuItems.indices) menuItems[x].isChecked = (x == positionSpared)
		demarkAction?.action(positionSpared)
	}


	fun calculateColumnCount(context: Context, columnWidthDp: Float): Int { // For example columnWidthdp=180
		val c = if (context.resources.configuration.orientation == 1)
			Settings.columnsInNovelsViewP
		else Settings.columnsInNovelsViewH

		val displayMetrics = context.resources.displayMetrics
		val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density

		return if (c == -1) (screenWidthDp / columnWidthDp + 0.5).toInt()
		else (screenWidthDp / (screenWidthDp / c) + 0.5).toInt()
	}

	/**
	 * Initializes the settings
	 *
	 * @param mainActivity activity
	 */
	fun initPreferences(mainActivity: Activity) {
		var dir = mainActivity.getExternalFilesDir(null)!!.absolutePath
		dir = dir.substring(0, dir.indexOf("/Android"))
		shoDir = Settings.settings.getString("dir", "$dir/Shosetsu/")!!
	}



	/**
	 * Checks if online
	 *
	 * @return true if so, otherwise false
	 */
	val isOnline: Boolean
		get() {
			return if (VERSION.SDK_INT >= VERSION_CODES.M) {
				val networkCapabilities = connectivityManager?.activeNetwork ?: return false
				val actNw = connectivityManager?.getNetworkCapabilities(networkCapabilities)
						?: return false
				when {
					actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
					actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
					actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
					else -> false
				}
			} else {
				// Suppressing warnings since this is old API usage
				@Suppress("DEPRECATION")
				val type = connectivityManager?.activeNetworkInfo ?: return false
				@Suppress("DEPRECATION")
				when (type.type) {
					ConnectivityManager.TYPE_WIFI -> true
					ConnectivityManager.TYPE_MOBILE -> true
					ConnectivityManager.TYPE_ETHERNET -> true
					else -> false
				}
			}
		}

	/**
	 * Toggles bookmark
	 *
	 * @param chapterID id
	 * @return true means added, false means removed
	 */
	@Throws(MissingResourceException::class)
	@Deprecated("ROOM", level = DeprecationLevel.WARNING)
	fun toggleBookmarkChapter(chapterID: Int): Boolean = throw Exception("STUD")

	/**
	 * Abstraction for Actions to take after demarking items. To simplify bulky code
	 */
	interface DeMarkAction {
		fun action(spared: Int)
	}
}
