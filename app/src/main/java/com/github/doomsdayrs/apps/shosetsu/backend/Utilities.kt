package com.github.doomsdayrs.apps.shosetsu.backend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelChapter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelStatus
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.ui.main.MainActivity
import com.github.doomsdayrs.apps.shosetsu.ui.main.Supporter
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader
import com.github.doomsdayrs.apps.shosetsu.ui.search.SearchFragment
import com.github.doomsdayrs.apps.shosetsu.ui.webView.Actions
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import com.github.doomsdayrs.apps.shosetsu.variables.Settings.MarkingTypes
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
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
 */
object Utilities {
    const val SELECTED_STROKE_WIDTH = 8
    var shoDir: String = "/Shosetsu/"

    // Preference objects
    lateinit var downloadPreferences: SharedPreferences
    lateinit var viewPreferences: SharedPreferences
    lateinit var advancedPreferences: SharedPreferences
    lateinit var trackingPreferences: SharedPreferences
    lateinit var backupPreferences: SharedPreferences

    fun convertNovelArrayToString2DArray(array: List<Novel>): ArrayList<Array<String>> {
        val a: ArrayList<Array<String>> = ArrayList()
        for (novel in array) {
            a.add(arrayOf(novel.title, novel.link, novel.imageURL))
        }
        return a
    }

    fun regret(context: Context) {
        Toast.makeText(context, context.getString(R.string.regret), Toast.LENGTH_LONG).show()
    }

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

    /**
     * Cleans a string
     *
     * @param input String to clean
     * @return string without specials
     */
    fun cleanString(input: String): String {
        return input.replace("[^A-Za-z0-9]".toRegex(), "_")
    }

    fun calculateNoOfColumns(context: Context, columnWidthDp: Float): Int { // For example columnWidthdp=180
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
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
    fun convertStringToStati(s: String): NovelStatus {
        return when (s) {
            "Publishing" -> NovelStatus.PUBLISHING
            "Completed" -> NovelStatus.COMPLETED
            "Paused" -> NovelStatus.PAUSED
            "Unknown" -> NovelStatus.UNKNOWN
            else -> NovelStatus.UNKNOWN
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
    fun initPreferences(mainActivity: AppCompatActivity) {
        Settings.ReaderTextColor = viewPreferences.getInt("ReaderTextColor", Color.BLACK)
        Settings.ReaderTextBackgroundColor = viewPreferences.getInt("ReaderBackgroundColor", Color.WHITE)
        var dir = mainActivity.getExternalFilesDir(null)!!.absolutePath
        dir = dir.substring(0, dir.indexOf("/Android"))
        shoDir = downloadPreferences.getString("dir", "$dir/Shosetsu/")!!
        Settings.downloadPaused = downloadPreferences.getBoolean("paused", false)
        Settings.ReaderTextSize = viewPreferences.getInt("ReaderTextSize", 14).toFloat()
        Settings.themeMode = advancedPreferences.getInt("themeMode", 0)
        Settings.paragraphSpacing = viewPreferences.getInt("paragraphSpacing", 1)
        Settings.indentSize = viewPreferences.getInt("indentSize", 1)
        Settings.ReaderMarkingType = viewPreferences.getInt("markingType", MarkingTypes.ONVIEW.i)
    }

    fun setReaderMarkingType(markingType: MarkingTypes) {
        Settings.ReaderMarkingType = markingType.i
        viewPreferences.edit().putInt("markingType", markingType.i).apply()
    }

    fun toggleTapToScroll(): Boolean {
        if (isTapToScroll) viewPreferences.edit().putBoolean("tapToScroll", false).apply() else viewPreferences.edit().putBoolean("tapToScroll", true).apply()
        return isTapToScroll
    }

    val isTapToScroll: Boolean
        get() = viewPreferences.getBoolean("tapToScroll", false)

    fun intToBoolean(a: Int): Boolean {
        return a == 1
    }

    fun changeIndentSize(newIndent: Int) {
        Settings.indentSize = newIndent
        viewPreferences.edit().putInt("indentSize", newIndent).apply()
    }

    fun changeParagraphSpacing(newSpacing: Int) {
        Settings.paragraphSpacing = newSpacing
        viewPreferences.edit().putInt("paragraphSpacing", newSpacing).apply()
    }

    fun changeMode(activity: Activity, newMode: Int) {
        if (newMode !in 0..2) throw IndexOutOfBoundsException("Non valid int passed")
        Settings.themeMode = newMode
        advancedPreferences.edit()
                .putInt("themeMode", newMode)
                .apply()
        activity.recreate()
        // setupTheme(activity);
    }

    fun setupTheme(activity: Activity) {
        when (Settings.themeMode) {
            0 -> activity.setTheme(R.style.Theme_MaterialComponents_Light_NoActionBar)
            1 -> activity.setTheme(R.style.Theme_MaterialComponents_NoActionBar)
            2 -> activity.setTheme(R.style.ThemeOverlay_MaterialComponents_Dark)
        }
    }

    /**
     * Toggles paused downloads
     *
     * @return if paused or not
     */
    fun togglePause(): Boolean {
        Settings.downloadPaused = !Settings.downloadPaused
        downloadPreferences.edit()
                .putBoolean("paused", Settings.downloadPaused)
                .apply()
        return Settings.downloadPaused
    }

    /**
     * Checks if online
     *
     * @return true if so, otherwise false
     */
    val isOnline: Boolean
        get() {
            val activeNetwork = Settings.connectivityManager!!.activeNetworkInfo
            return if (activeNetwork != null) activeNetwork.type == ConnectivityManager.TYPE_WIFI || activeNetwork.type == ConnectivityManager.TYPE_MOBILE else false
        }//TODO: Check this also, this doesn't seem to be a nice way to do things.

    /**
     * Is reader in night mode
     *
     * @return true if so, otherwise false
     */
    val isReaderNightMode: Boolean
        get() =//TODO: Check this also, this doesn't seem to be a nice way to do things.
            Settings.ReaderTextColor == Color.WHITE


    fun setNightNode() {
        setReaderColor(Color.WHITE, Color.BLACK)
    }

    fun setLightMode() {
        setReaderColor(Color.BLACK, Color.WHITE)
    }

    fun setSepiaMode(context: Context) {
        setReaderColor(Color.BLACK, ContextCompat.getColor(context, R.color.wheat))
    }

    /**
     * Sets the reader color
     *
     * @param text       Color of text
     * @param background Color of background
     */
    private fun setReaderColor(text: Int, background: Int) {
        Settings.ReaderTextColor = text
        Settings.ReaderTextBackgroundColor = background
        viewPreferences.edit()
                .putInt("ReaderTextColor", text)
                .putInt("ReaderBackgroundColor", background)
                .apply()
    }

    fun getReaderColor(context: Context): Int {
        return when (Settings.ReaderTextBackgroundColor) {
            Color.WHITE -> {
                1
            }
            Color.BLACK -> {
                0
            }
            ContextCompat.getColor(context, R.color.wheat) -> {
                2
            }
            else -> {
                1
            }
        }
    }

    /**
     * Swaps the reader colors
     */
    fun swapReaderColor() {
        if (isReaderNightMode) {
            setReaderColor(Color.BLACK, Color.WHITE)
        } else {
            setReaderColor(Color.WHITE, Color.BLACK)
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

    fun setTextSize(size: Int) {
        Settings.ReaderTextSize = size.toFloat()
        viewPreferences.edit()
                .putInt("ReaderTextSize", size)
                .apply()
    }

    /**
     * Pre resquite requires chapter to already have been added to library
     *
     * @param activity     activity
     * @param novelChapter novel chapter
     * @param novelID      id of novel
     * @param formatterID  formatter
     */
    fun openChapter(activity: Activity, novelChapter: NovelChapter, novelID: Int, formatterID: Int) {
        openChapter(activity, novelChapter, novelID, formatterID, null)
    }

    private fun openChapter(activity: Activity, novelChapter: NovelChapter, novelID: Int, formatterID: Int, chapters: Array<String>?) {
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

    fun openInBrowser(activity: Activity, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(intent)
    }

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