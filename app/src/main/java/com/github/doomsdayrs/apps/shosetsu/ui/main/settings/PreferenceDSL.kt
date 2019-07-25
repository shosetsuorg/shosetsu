package com.github.doomsdayrs.apps.shosetsu.ui.main.settings

import androidx.preference.Preference
import androidx.preference.PreferenceGroup

// A Domain Specific Language for PreferenceScreen (aka. Settings)
@DslMarker
@Target(AnnotationTarget.TYPE)
annotation class DSL

inline fun PreferenceGroup.preference(block: (@DSL Preference).() -> Unit): Preference {
    return initThenAdd(Preference(context), block)
}

inline fun <P : Preference> PreferenceGroup.initThenAdd(p: P, block: P.() -> Unit): P {
    return p.apply { block(); addPreference(this); }
}

inline fun Preference.onClick(crossinline block: () -> Unit) {
    setOnPreferenceClickListener { block(); true }
}

var Preference.titleRes: Int
    get() = 0 // set only
    set(value) { setTitle(value) }
