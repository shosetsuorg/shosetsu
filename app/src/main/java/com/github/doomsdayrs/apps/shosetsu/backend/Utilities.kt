package com.github.doomsdayrs.apps.shosetsu.backend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.ui.main.MainActivity
import com.github.doomsdayrs.apps.shosetsu.ui.main.Supporter
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader
import com.github.doomsdayrs.apps.shosetsu.ui.search.SearchFragment
import com.github.doomsdayrs.apps.shosetsu.ui.webView.Actions
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.MarkingTypes
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

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

    // Preference objects
    lateinit var downloadPreferences: SharedPreferences
    lateinit var viewPreferences: SharedPreferences
    lateinit var advancedPreferences: SharedPreferences
    lateinit var trackingPreferences: SharedPreferences
    lateinit var backupPreferences: SharedPreferences

    fun isFormatterDisabled(jsonArray: JSONArray, name: String): Boolean {
        for (i in 0 until jsonArray.length())
            if (JSONObject(jsonArray[i].toString()).getString("name") == name)
                return true
        return false
    }

    fun convertNovelArrayToString2DArray(array: Array<Novel.Listing>): ArrayList<Array<String>> {
        val a: ArrayList<Array<String>> = ArrayList()
        for (novel in array) {
            a.add(arrayOf(novel.title, novel.link, novel.imageURL))
        }
        return a
    }

    fun regret(context: Context) = context.toast(R.string.regret, duration = LENGTH_LONG)


    fun setActivityTitle(activity: Activity?, title: String?) {
        val supporter = activity as Supporter?
        supporter?.setTitle(title)
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
     * Serialize object to string
     *
     * @param object object serialize
     * @return Serialised string
     * @throws IOException exception
     */
    @Throws(IOException::class)
    fun serializeToString(`object`: Any): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(`object`)
        val bytes = byteArrayOutputStream.toByteArray()
        return "serial-" + Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /**
     * Deserialize a string to the object
     *
     * @param string serialized string
     * @return Object from string
     * @throws IOException            exception
     * @throws ClassNotFoundException exception
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    fun deserializeString(string: String): Any? {
        var editString = string
        if (editString != "serial-null") {
            editString = editString.substring(7)
            //Log.d("Deserialize", string);
            val bytes = Base64.decode(editString, Base64.NO_WRAP)
            val byteArrayInputStream = ByteArrayInputStream(bytes)
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            return objectInputStream.readObject()
        }
        return null
    }

    @JvmStatic
            /**
             * Checks string before deserialization
             * If null or empty, returns "". Else deserializes the string and returns
             *
             * @param string String to be checked
             * @return Completed String
             */
    fun checkStringDeserialize(string: String): String {
        if (string.isEmpty()) {
            return ""
        } else {
            try {
                val `object` = deserializeString(string) ?: return ""
                return `object` as String
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return ""
    }

    @JvmStatic
            /**
             * Checks string before serialization
             * If null or empty, returns "". Else serializes the string and returns
             *
             * @param string String to be checked
             * @return Completed String
             */
    fun checkStringSerialize(string: String?): String {
        if (string == null || string.isEmpty()) {
            return ""
        } else {
            try {
                return serializeToString(string)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return ""
    }

    @JvmStatic
            /**
             * Converts String Stati back into Stati
             *
             * @param s String title
             * @return Stati
             */
    fun convertStringToStati(s: String): Novel.Status {
        return when (s) {
            "Publishing" -> Novel.Status.PUBLISHING
            "Completed" -> Novel.Status.COMPLETED
            "Paused" -> Novel.Status.PAUSED
            "Unknown" -> Novel.Status.UNKNOWN
            else -> Novel.Status.UNKNOWN
        }
    }

    @JvmStatic
            /**
             * Converts Array of Strings into a String
             *
             * @param a array of strings
             * @return String Array
             */
    fun convertArrayToString(a: Array<String?>?): String {
        if (a != null && a.isNotEmpty()) {
            for (x in a.indices) {
                if (a[x] != null) a[x] = a[x]!!.replace(",", ">,<")
            }
            return Arrays.toString(a)
        }
        return "[]"
    }

    @JvmStatic
            /**
             * Converts a String Array back into an Array of Strings
             *
             * @param s String array
             * @return Array of Strings
             */
    fun convertStringToArray(s: String): Array<String> {
        val a = s.substring(1, s.length - 1).split(", ".toRegex()).toTypedArray()
        for (x in a.indices) {
            a[x] = a[x].replace(">,<", ",")
        }
        return a
    }


    /**
     * Initializes the settings
     *
     * @param mainActivity activity
     */
    fun initPreferences(mainActivity: Activity) {
        var dir = mainActivity.getExternalFilesDir(null)!!.absolutePath
        dir = dir.substring(0, dir.indexOf("/Android"))
        shoDir = downloadPreferences.getString("dir", "$dir/Shosetsu/")!!
    }

    fun setReaderMarkingType(markingType: MarkingTypes) {
        Settings.ReaderMarkingType = markingType.i
    }

    fun toggleTapToScroll(): Boolean {
        if (isTapToScroll) viewPreferences.edit().putBoolean("tapToScroll", false).apply() else viewPreferences.edit().putBoolean("tapToScroll", true).apply()
        return isTapToScroll
    }

    val isTapToScroll: Boolean
        get() = viewPreferences.getBoolean("tapToScroll", false)

    fun intToBoolean(a: Int): Boolean = a == 1

    fun changeIndentSize(newIndent: Int) {
        Settings.indentSize = newIndent
    }


    //  fun changeMode(activity: Activity, newMode: Int) { if (newMode !in 0..2) throw IndexOutOfBoundsException("Non valid int passed");  Settings.themeMode = newMode; activity.recreate() // setupTheme(activity); }

    fun setBackgroundByTheme(view: View) =
            when ((view.context as AppCompatActivity).delegate.localNightMode) {
                AppCompatDelegate.MODE_NIGHT_NO -> view.setBackgroundResource(R.color.white_trans)
                else -> view.setBackgroundResource(R.color.black_trans)
            }


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
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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


    fun setNightNode() = setReaderColor(Color.WHITE, Color.BLACK)

    fun setLightMode() = setReaderColor(Color.BLACK, Color.WHITE)

    fun setSepiaMode(context: Context) = setReaderColor(Color.BLACK, ContextCompat.getColor(context, R.color.wheat))


    /**
     * Sets the reader color
     *
     * @param text       Color of text
     * @param background Color of background
     */
    private fun setReaderColor(text: Int, background: Int) {
        Settings.ReaderTextColor = text
        Settings.ReaderTextBackgroundColor = background
    }

    fun getReaderColor(context: Context): Int =
            when (Settings.ReaderTextBackgroundColor) {
                Color.WHITE -> 1
                Color.BLACK -> 0
                ContextCompat.getColor(context, R.color.wheat) -> 2
                else -> 1
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

    /**
     * Pre resquite requires chapter to already have been added to library
     *
     * @param activity     activity
     * @param novelChapter novel chapter
     * @param novelID      id of novel
     * @param formatterID  formatter
     */
    fun openChapter(activity: Activity, novelChapter: Novel.Chapter, novelID: Int, formatterID: Int) = openChapter(activity, novelChapter, novelID, formatterID, null)


    private fun openChapter(activity: Activity, novelChapter: Novel.Chapter, novelID: Int, formatterID: Int, chapters: Array<String>?) {
        val chapterID = DatabaseIdentification.getChapterIDFromChapterURL(novelChapter.link)
        if (Settings.ReaderMarkingType == MarkingTypes.ONVIEW.i) Database.DatabaseChapter.setChapterStatus(chapterID, Status.READING)
        val intent = Intent(activity, ChapterReader::class.java)
        intent.putExtra("chapterID", chapterID)
        intent.putExtra("novelID", novelID)
        intent.putExtra("formatter", formatterID)
        intent.putExtra("chapters", chapters)
        activity.startActivity(intent)
    }

    fun search(activity: Activity, query: String) {
        val mainActivity = activity as MainActivity
        val searchFragment = SearchFragment()
        searchFragment.query = query
        mainActivity.transitionView(searchFragment)
    }

    fun openInWebview(activity: Activity, url: String) {
        val intent = Intent(activity, WebViewApp::class.java)
        intent.putExtra("url", url)
        intent.putExtra("action", Actions.VIEW.action)
        activity.startActivity(intent)
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