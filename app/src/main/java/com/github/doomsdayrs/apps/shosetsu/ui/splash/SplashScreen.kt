package com.github.doomsdayrs.apps.shosetsu.ui.splash

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterController
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.DBHelper
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.main.MainActivity
import com.github.doomsdayrs.apps.shosetsu.variables.ext.requestPerms
import java.io.File


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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class SplashScreen : AppCompatActivity(R.layout.splash_screen) {
    internal class BootSequence(private val splashScreen: SplashScreen) : AsyncTask<Void, String, Void>() {
        val unknown = ArrayList<File>()

        override fun onProgressUpdate(vararg values: String?) {
            splashScreen.textView.post {
                splashScreen.textView.text = values[0]
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            unknown.addAll(FormatterController.formatterInitTask(splashScreen) { onProgressUpdate(it) })
            return null
        }

        override fun onPostExecute(result: Void?) {
            val action = {
                onProgressUpdate("Setting up the application")
                val intent = Intent(splashScreen, MainActivity::class.java)
                intent.action = splashScreen.intent.action
                splashScreen.intent.extras?.let { intent.putExtras(it) }
                splashScreen.startActivity(intent)
                onProgressUpdate("Finished! Going to app now~")
                splashScreen.finish()
            }

            if (unknown.size > 0) {
                onProgressUpdate("Uh oh! We got some issues~")
                FormatterController.formatterInitPost(unknown, splashScreen, action)
            } else {
                action()
            }
        }
    }

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestPerms()
        super.onCreate(savedInstanceState)
        // Sets prefrences
        Utilities.viewPreferences = getSharedPreferences("view", 0)
        Utilities.downloadPreferences = getSharedPreferences("download", 0)
        Utilities.advancedPreferences = getSharedPreferences("advanced", 0)
        Utilities.trackingPreferences = getSharedPreferences("tracking", 0)
        Utilities.backupPreferences = getSharedPreferences("backup", 0)
        Utilities.initPreferences(this)

        // Sets up DB
        if (Database.sqLiteDatabase == null) Database.sqLiteDatabase = DBHelper(this).writableDatabase

        // Settings setup
        Utilities.connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        textView = findViewById(R.id.textView)
        BootSequence(this).execute()
    }
}