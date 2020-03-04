package org.doomsdayrs.apps.shosetsuwear

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class MainActivityWear : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_wear)

        // Enables Always-on
        setAmbientEnabled()
    }
}
