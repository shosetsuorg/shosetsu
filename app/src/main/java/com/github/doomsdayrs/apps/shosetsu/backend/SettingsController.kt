package com.github.doomsdayrs.apps.shosetsu.backend

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo

import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.variables.Settings

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * Shosetsu
 * 14 / June / 2019
 *
 * @author github.com/doomsdayrs
 * @author github.com/hXtreme
 */

object SettingsController {
    // Preference objects
    var download: SharedPreferences? = null
    var view: SharedPreferences? = null
    var advanced: SharedPreferences? = null
    var tracking: SharedPreferences? = null
    var backup: SharedPreferences? = null

    /**
     * Checks if online
     *
     * @return true if so, otherwise false
     */
    val isOnline: Boolean
        get() {
            val activeNetwork = Settings.connectivityManager.activeNetworkInfo
            return if (activeNetwork != null) activeNetwork.type == ConnectivityManager.TYPE_WIFI || activeNetwork.type == ConnectivityManager.TYPE_MOBILE else false
        }

    /**
     * Is reader in night mode
     *
     * @return true if so, otherwise false
     */
    //TODO: Check this also, this doesn't seem to be a nice way to do things.
    val isReaderNightMode: Boolean
        get() = Settings.ReaderTextColor == Color.WHITE

    //TODO Online Trackers
    //Methods below when tracking system setup

    val isTrackingEnabled: Boolean
        get() = tracking!!.getBoolean("enabled", false)

    /**
     * Initializes the settings
     */
    fun init() {
        Settings.ReaderTextColor = view!!.getInt("ReaderTextColor", Color.BLACK)
        Settings.ReaderTextBackgroundColor = view!!.getInt("ReaderBackgroundColor", Color.WHITE)
        Download_Manager.shoDir = download!!.getString("dir", "/storage/emulated/0/Shosetsu/")
        Settings.downloadPaused = download!!.getBoolean("paused", false)
        Settings.ReaderTextSize = view!!.getInt("ReaderTextSize", 14).toFloat()
        Settings.themeMode = advanced!!.getInt("themeMode", 0)
        Settings.paragraphSpacing = view!!.getInt("paragraphSpacing", 1)
        Settings.indentSize = view!!.getInt("indentSize", 1)
    }

    fun changeIndentSize(newIndent: Int) {
        Settings.indentSize = newIndent
        view!!.edit().putInt("indentSize", newIndent).apply()
    }

    fun changeParagraphSpacing(newSpacing: Int) {
        Settings.paragraphSpacing = newSpacing
        view!!.edit().putInt("paragraphSpacing", newSpacing).apply()
    }


    fun changeMode(activity: Activity, newMode: Int) {
        if (!(newMode >= 0 && newMode <= 2))
            throw IndexOutOfBoundsException("Non valid int passed")
        Settings.themeMode = newMode
        advanced!!.edit()
                .putInt("themeMode", newMode)
                .apply()

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
        download!!.edit()
                .putBoolean("paused", Settings.downloadPaused)
                .apply()
        return Settings.downloadPaused
    }

    fun setNightNode() {
        setReaderColor(Color.WHITE, Color.BLACK)
    }

    fun unsetNightMode() {
        setReaderColor(Color.BLACK, Color.WHITE)
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
        view!!.edit()
                .putInt("ReaderTextColor", text)
                .putInt("ReaderBackgroundColor", background)
                .apply()
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
     * Gets y position of a bookmark
     *
     * @param chapterURL chapter chapterURL
     * @return y position
     */
    fun getYBookmark(chapterURL: String): Int {
        return Database.DatabaseChapter.getY(chapterURL)
    }

    /**
     * Toggles bookmark
     *
     * @param chapterURL imageURL of chapter
     * @return true means added, false means removed
     */
    fun toggleBookmarkChapter(chapterURL: String): Boolean {
        if (Database.DatabaseChapter.isBookMarked(chapterURL)) {
            Database.DatabaseChapter.setBookMark(chapterURL, 0)
            return false
        } else {
            Database.DatabaseChapter.setBookMark(chapterURL, 1)
            return true
        }
    }


    fun setTextSize(size: Int) {
        Settings.ReaderTextSize = size.toFloat()
        view!!.edit()
                .putInt("ReaderTextSize", size)
                .apply()
    }

    fun addTracker() {}
}
