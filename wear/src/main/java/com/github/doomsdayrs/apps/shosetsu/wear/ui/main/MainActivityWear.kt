package com.github.doomsdayrs.apps.shosetsu.wear.ui.main

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import org.doomsdayrs.apps.shosetsu.R

class MainActivityWear : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_wear)
        // Enables Always-on
        setAmbientEnabled()
    }
}
