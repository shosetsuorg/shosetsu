package com.github.doomsdayrs.apps.shosetsu.backend.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences

import com.github.doomsdayrs.apps.shosetsu.backend.preference.PreferenceKeys as Keys

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