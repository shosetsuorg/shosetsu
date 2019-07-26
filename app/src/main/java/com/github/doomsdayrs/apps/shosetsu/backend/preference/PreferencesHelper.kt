package com.github.doomsdayrs.apps.shosetsu.backend.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences

import com.github.doomsdayrs.apps.shosetsu.backend.preference.PreferenceKeys as Keys

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
 * 9 / June / 2019
 *
 * @author github.com/hXtreme
 */

// Add helpful methods to Preference
fun <T> Preference<T>.getOrDefault(): T = get() ?: defaultValue()!!

fun Preference<Boolean>.invert(): Boolean = getOrDefault().let { set(!it); !it }

class PreferencesHelper(val context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val rxPrefs = RxSharedPreferences.create(prefs)

    fun clear() = prefs.edit().clear().apply()

    // Global preferences
    fun theme() = prefs.getInt(Keys.theme, 0)

    // Reader Preferences
    fun reader_dark_mode() = rxPrefs.getBoolean(Keys.reader_night_mode, false)

    fun reader_para_space() = rxPrefs.getInteger(Keys.reader_para_spacing, 0)

    fun reader_para_indent() = rxPrefs.getInteger(Keys.reader_para_indent, 17)
}
