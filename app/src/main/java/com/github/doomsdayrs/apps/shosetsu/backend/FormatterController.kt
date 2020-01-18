package com.github.doomsdayrs.apps.shosetsu.backend

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.github.doomsdayrs.api.shosetsu.services.core.dep.LuaFormatter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.LuaSupport
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import org.json.JSONObject
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
object FormatterController {
    const val directory = "/scripts/"
    const val scriptFolder = "src/"
    lateinit var sourceJSON: JSONObject

    /**
     * Initializes formatterController
     */
    fun initialize(activity: Activity) {
        FormatterInit(activity).execute()
    }

    private fun md5(s: String): String? {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()
            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun getContent(file: File): String {
        val br = BufferedReader(FileReader(file))
        val text = StringBuilder()
        var line: String? = br.readLine()
        while (line != null) {
            text.append(line)
            text.append("\n")
            line = br.readLine()
        }
        br.close()
        return text.toString()
    }

    private fun getMetaData(file: File): JSONObject? {
        try {
            BufferedReader(FileReader(file)).use { br ->
                val line: String? = br.readLine()
                br.close()
                return if (line != null) JSONObject(line.toString().replace("-- ", "")) else null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun getScriptFromSystem(path: String): LuaFormatter {
        val script: LuaValue = JsePlatform.standardGlobals()
        script.checkglobals().STDOUT = System.out
        val support = LuaSupport()
        script.checkglobals().set("LuaSupport", CoerceJavaToLua.coerce(support))
        script["dofile"].call(LuaValue.valueOf(path))
        return LuaFormatter(script)
    }


    class FormatterInit(val activity: Activity) : AsyncTask<Void, Void, Void>() {
        val incompatible = ArrayList<File>()

        override fun doInBackground(vararg params: Void?): Void? {
            val sourceFile = File(activity.filesDir.absolutePath + "/formatters.json")
            if (sourceFile.isFile && sourceFile.exists()) {
                sourceJSON = JSONObject(getContent(sourceFile))
            } else {
                Log.i("FormatterInit", "Downloading formatterJSON")
                sourceFile.createNewFile()
                var json = "{}"
                if (Utilities.isOnline) {
                    val doc = WebViewScrapper.docFromURL("https://raw.githubusercontent.com/Doomsdayrs/shosetsu-extensions/master/src/main/resources/formatters.json", false)
                    if (doc != null) {
                        json = doc.body().text()
                        val out = FileOutputStream(sourceFile)
                        val writer = OutputStreamWriter(out)
                        writer.write(json)
                        writer.close()
                        out.flush()
                        out.close()
                    }
                } else {
                    Log.e("FormatterInit", "IsOffline, Cannot load data, Using stud")
                }
                sourceJSON = JSONObject(json)
            }

            val path = Utilities.shoDir + directory + scriptFolder
            // Check if script MD5 matches DB
            val directory = File(path)
            if (directory.isDirectory && directory.exists()) {
                val sources = directory.listFiles()!!
                for (source in sources) {
                    val meta = getMetaData(source)
                    if (meta != null) {
                        // Checks MD5 sum
                        var sum = Database.DatabaseFormatters.getMD5Sum(meta.getInt("id"))
                        if (sum.isEmpty()) {
                            sum = sourceJSON.getJSONObject(source.name.substring(0, source.name.length - 4)).getString("md5")
                        }
                        val fileSum = md5(getContent(source))

                        Log.i("FormatterInit", "Sum required:\t$sum")
                        Log.i("FormatterInit", "Sum found:\t$fileSum")
                        if (sum == fileSum)
                            DefaultScrapers.formatters.add(getScriptFromSystem(source.absolutePath))
                        else
                            incompatible.add(source)

                    } else {
                        incompatible.add(source)
                    }
                }
                for (incom in incompatible) {
                    //TODO replace this with proper error message
                    Log.e("FormatterInit", "Deleting Unverified file: " + incom.absolutePath)
                    DefaultScrapers.formatters.add(getScriptFromSystem(incom.absolutePath))
                    //incom.delete()
                }
            } else {
                directory.mkdirs()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
        }
    }

}