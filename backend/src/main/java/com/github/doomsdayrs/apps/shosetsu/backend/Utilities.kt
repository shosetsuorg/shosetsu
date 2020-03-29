package com.github.doomsdayrs.apps.shosetsu.backend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import android.view.MenuItem
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.MarkingTypes
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import org.doomsdayrs.apps.shosetsulib.R
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

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

	fun isFormatterDisabled(jsonArray: JSONArray, name: String): Boolean {
		for (i in 0 until jsonArray.length())
			if (JSONObject(jsonArray[i].toString()).getString("name") == name)
				return true
		return false
	}

	fun convertNovelArrayToString2DArray(array: Array<Novel.Listing>): ArrayList<Array<String>> {
		val a: ArrayList<Array<String>> = ArrayList()
		for ((title, link, imageURL) in array) {
			a.add(arrayOf(title, link, imageURL))
		}
		return a
	}

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


	fun calculateNoOfColumns(context: Context, columnWidthDp: Float): Int { // For example columnWidthdp=180
		val c = if (context.resources.configuration.orientation == 1) Settings.columnsInNovelsViewP else Settings.columnsInNovelsViewH

		val displayMetrics = context.resources.displayMetrics
		val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density

		return if (c == -1) (screenWidthDp / columnWidthDp + 0.5).toInt() else (screenWidthDp / (screenWidthDp / c) + 0.5).toInt()
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

	fun setReaderMarkingType(markingType: MarkingTypes) {
		Settings.readerMarkingType = markingType.i
	}

	fun toggleTapToScroll(): Boolean {
		val b = Settings.isTapToScroll
		Settings.isTapToScroll = !b
		return !b
	}


	fun toggleInvertedSwipe(): Boolean {
		val b = Settings.isInvertedSwipe
		Settings.isInvertedSwipe = !b
		return !b
	}


	fun intToBoolean(a: Int): Boolean = a == 1

	fun changeIndentSize(newIndent: Int) {
		Settings.ReaderIndentSize = newIndent
	}


	//  fun changeMode(activity: Activity, newMode: Int) { if (newMode !in 0..2) throw IndexOutOfBoundsException("Non valid int passed");  Settings.themeMode = newMode; activity.recreate() // setupTheme(activity); }


	/**
	 * Toggles paused downloads
	 *
	 * @return if paused or not
	 */
	fun togglePause(): Boolean {
		Settings.downloadPaused = !Settings.downloadPaused
		return Settings.downloadPaused
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
	fun toggleBookmarkChapter(chapterID: Int): Boolean { //TODO Simplify
		return if (Database.DatabaseChapter.isBookMarked(chapterID)) {
			Database.DatabaseChapter.setBookMark(chapterID, 0)
			false
		} else {
			Database.DatabaseChapter.setBookMark(chapterID, 1)
			true
		}
	}


	fun openInBrowser(activity: Activity, url: String) = activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))


	/**
	 * Freezes the thread for x time
	 *
	 * @param time time in MS
	 */
	fun wait(time: Int) {
		try {
			TimeUnit.MILLISECONDS.sleep(time.toLong())
		} catch (e: InterruptedException) {
			Log.e("Error", e.message.toString())
		}
	}

	//TODO Online Trackers
//Methods below when tracking system setup
	//   public static boolean isTrackingEnabled() {
// --Commented out by Inspection START (12/22/19 11:10 AM):
//        return trackingPreferences.getBoolean("enabled", false);
//    }
//
//    @SuppressWarnings({"EmptyMethod", "unused"})
// --Commented out by Inspection STOP (12/22/19 11:10 AM)
//       public static void addTracker () {
//      }
	private var debug = false

	@Suppress("unused")

	fun toggleDebug() {
		debug = !debug
	}

	/**
	 * Abstraction for Actions to take after demarking items. To simplify bulky code
	 */
	interface DeMarkAction {
		fun action(spared: Int)
	}
}