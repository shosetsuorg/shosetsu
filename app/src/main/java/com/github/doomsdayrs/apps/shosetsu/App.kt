package com.github.doomsdayrs.apps.shosetsu

import android.app.Application
import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.backend.preference.PreferencesHelper
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

open class App : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        import(androidXModule(this@App))

        bind<PreferencesHelper>() with singleton { PreferencesHelper(instance()) }
    }

    override fun onCreate() {
        Log.d("onCreate App", "Successfully created the App")
        super.onCreate()
    }
}