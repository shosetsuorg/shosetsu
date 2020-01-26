package com.github.doomsdayrs.apps.shosetsu.backend

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.dep.LuaFormatter
import com.github.doomsdayrs.api.shosetsu.services.core.luaSupport.ShosetsuLib
import com.github.doomsdayrs.api.shosetsu.services.core.objects.LibraryLoaderSync
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.ExtensionsFragment
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.adapter.ExtensionsAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.susScript.SusScriptDialog
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import org.json.JSONArray
import org.json.JSONObject
import org.luaj.vm2.LuaValue
import java.io.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption
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
    const val scriptDirectory = "/scripts/"
    const val libraryDirectory = "/libraries/"
    const val sourceFolder = "/src/"
    lateinit var sourceJSON: JSONObject

    /**
     * Initializes formatterController
     */
    fun initialize(activity: Activity) {
        FormatterInit(activity).execute()
    }

    fun md5(s: String): String? {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()
            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices)
                hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getContent(file: File): String {
        val builder = StringBuilder()
        val br = BufferedReader(FileReader(file))
        var line = br.readLine()
        while (line != null) {
            builder.append(line).append("\n")
            line = br.readLine()
        }
        return builder.toString()
    }


    fun splitVersion(version: String): Array<String> {
        return version.split(".").toTypedArray()
    }

    /**
     * @return 1 if ver2 is newer, 0 if they are the same, -1 if ver1 is newer
     */
    fun compareVersions(ver1: String, ver2: String): Int {
        if (ver1 == ver2)
            return 0
        val version1 = splitVersion(ver1)
        val version2 = splitVersion(ver2)

        for (i in 0..2) {
            val a = version1[i].compareTo(version2[i])
            if (a != 0) {
                return a
            }
        }
        return 0
    }

    fun getMetaData(file: File): JSONObject? {
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

    fun downloadScript(name: String, holder: ExtensionsAdapter.ExtensionHolder, activity: Activity) {
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse("https://raw.githubusercontent.com/Doomsdayrs/shosetsu-extensions/master/src/main/resources/src/$name.lua"))

        request.setDescription("Installing $name")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, "$name.lua")

        val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = manager.enqueue(request)
        val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val intentID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadID == intentID && context != null) {
                    Toast.makeText(context, "Installed: $name", Toast.LENGTH_SHORT).show()
                    var file = context.getExternalFilesDir(null)!!.absolutePath
                    file = file.substring(0, file.indexOf("/Android"))
                    val downloadedFile = File("$file/${Environment.DIRECTORY_DOCUMENTS}/$name.lua")
                    val targetFile = File(activity.filesDir.absolutePath + sourceFolder + scriptDirectory + "/$name.lua")
                    Log.i("Extension download", downloadedFile.absolutePath)
                    Log.i("Extension download", targetFile.absolutePath)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Files.move(downloadedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    } else {
                        downloadedFile.renameTo(targetFile)
                    }
                    holder.button.text = holder.itemView.context.getString(R.string.uninstall)
                    //holder.button.setImageResource(R.drawable.ic_delete_black_24dp)
                    holder.installed = true
                    val form = LuaFormatter(targetFile)

                    if (holder.update) {
                        holder.update = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            DefaultScrapers.formatters.removeIf { (it as LuaFormatter).formatterID == form.formatterID }
                        } else {
                            DefaultScrapers.formatters.remove(DefaultScrapers.getByID(form.formatterID))
                        }
                    }

                    DefaultScrapers.formatters.add(form)
                    DefaultScrapers.formatters.sortedWith(compareBy { it.name })
                    activity.findViewById<RecyclerView>(R.id.recyclerView)?.adapter?.notifyDataSetChanged()
                }
            }
        }
        activity.registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    fun deleteScript(name: String, id: Int, holder: ExtensionsAdapter.ExtensionHolder, activity: Activity) {
        //  holder.button.setImageResource(R.drawable.ic_file_download)
        holder.button.text = holder.itemView.context.getString(R.string.download)
        var i = 0
        while (i < DefaultScrapers.formatters.size && holder.installed) {
            if (DefaultScrapers.formatters[i].formatterID == id) {
                DefaultScrapers.formatters.removeAt(i)
                holder.installed = false
                val targetFile = File(Utilities.shoDir + scriptDirectory + sourceFolder + "/$name.lua")
                targetFile.delete()
                Toast.makeText(activity.applicationContext, "Script deleted", Toast.LENGTH_SHORT).show()
                activity.findViewById<RecyclerView>(R.id.recyclerView)?.adapter?.notifyDataSetChanged()
            }
            i++
        }
        Database.DatabaseFormatters.removeFormatterFromList(id)
    }

    fun trustScript(file: File) {
        val name = file.name.substring(0, file.name.length - 4)
        val meta = LuaFormatter(file).getMetaData()!!
        val md5 = md5(getContent(file))
        val id = meta.getInt("id")
        val repo = meta.getString("repo")
        Database.DatabaseFormatters.addToFormatterList(name, id, md5, repo.isNotEmpty(), repo)
    }

    fun writeFile(string: String, file: File) {
        val out = FileOutputStream(file)
        val writer = OutputStreamWriter(out)
        writer.write(string)
        writer.close()
        out.flush()
        out.close()
    }

    private fun downloadLibrary(name: String, file: File) {
        val doc = WebViewScrapper.docFromURL("https://raw.githubusercontent.com/Doomsdayrs/shosetsu-extensions/master/src/main/resources/lib/$name.lua", false)
        if (doc != null) {
            writeFile(doc.body().text(), file)
        }
    }

    class FormatterInit(val activity: Activity) : AsyncTask<Void, Void, Void>() {
        private val unknownFormatters = ArrayList<File>()

        override fun doInBackground(vararg params: Void?): Void? {

            // Source files
            run {
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
                            writeFile(json, sourceFile)
                        }
                    } else {
                        Log.e("FormatterInit", "IsOffline, Cannot load data, Using stud")
                    }
                    sourceJSON = JSONObject(json)
                }
            }


            // Auto Download all source material
            run {
                val libraries: JSONArray = sourceJSON.getJSONArray("libraries")
                for (index in 0 until libraries.length()) {
                    val libraryJSON: JSONObject = libraries.getJSONObject(index)
                    val libraryFile = File(activity.filesDir.absolutePath + sourceFolder + libraryDirectory + "${libraryJSON.getString("name")}.lua")
                    if (libraryFile.exists()) {
                        val meta = getMetaData(libraryFile)!!
                        if (compareVersions(meta.getString("version"), libraryJSON.getString("version")) == -1) {
                            Log.i("FormatterInit", "Installing library:\t" + libraryJSON.getString("name"))
                            downloadLibrary(libraryJSON.getString("name"), libraryFile)
                        }
                    } else {
                        downloadLibrary(libraryJSON.getString("name"), libraryFile)
                    }
                }
            }

            ShosetsuLib.libraryLoaderSync = object : LibraryLoaderSync {
                override fun getScript(name: String): LuaValue? {
                    Log.i("LibraryLoaderSync","Loading:\t$name")
                    val libraryFile = File(activity.filesDir.absolutePath + sourceFolder + libraryDirectory + "$name.lua")
                    return LuaValue.valueOf(libraryFile.absolutePath)
                }
            }

            // Load the private scripts
            run {
                val path = activity.filesDir.absolutePath + sourceFolder + scriptDirectory
                val directory = File(path)
                if (directory.isDirectory && directory.exists()) {
                    val sources = directory.listFiles()
                    val jsonArray = Settings.disabledFormatters
                    if (sources != null) {
                        for (source in sources) {
                            if (!Utilities.isFormatterDisabled(jsonArray, source.name.substring(0, source.name.length - 4))) {
                                val l = LuaFormatter(source)
                                if (DefaultScrapers.getByID(l.formatterID) == DefaultScrapers.unknown)
                                    DefaultScrapers.formatters.add(l)
                            }
                        }
                    } else {
                        Log.e("FormatterInit", "Sources file returned null")
                    }
                } else {
                    directory.mkdirs()
                }
            }


            run {
                val path = Utilities.shoDir + scriptDirectory
                // Check if script MD5 matches DB
                val directory = File(path)
                if (directory.isDirectory && directory.exists()) {
                    val sources = directory.listFiles()
                    val jsonArray = Settings.disabledFormatters
                    if (sources != null) {
                        for (source in sources) {
                            confirm(source, object : CheckSumAction {
                                override fun fail() {
                                    Log.i("FormatterInit", "${source.name}:\tSum does not match, Adding")
                                    unknownFormatters.add(source)
                                }

                                override fun pass() {
                                    if (!Utilities.isFormatterDisabled(jsonArray, source.name.substring(0, source.name.length - 4))) {
                                        val l = LuaFormatter(source)
                                        if (DefaultScrapers.getByID(l.formatterID) == DefaultScrapers.unknown)
                                            DefaultScrapers.formatters.add(l)
                                    }
                                }

                                override fun noMeta() {
                                    Log.i("FormatterInit", "${source.name}:\tNo meta found, Adding")
                                    unknownFormatters.add(source)
                                }

                            })
                        }
                    } else {
                        Log.e("FormatterInit", "External Sources file returned null")
                    }
                } else {
                    directory.mkdirs()
                }
            }
            for (unknownFormatter in unknownFormatters) {
                Log.e("FormatterInit", "Unknown Script:\t${unknownFormatter.name}")
            }
            DefaultScrapers.formatters.sortedWith(compareBy { it.name })
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (unknownFormatters.size > 0) {
                SusScriptDialog(activity, unknownFormatters).execute()
            }
        }

    }

    interface CheckSumAction {
        fun fail()
        fun pass()
        fun noMeta()
    }

    /**
     * Dynamic MD5 checking
     */
    fun confirm(file: File, checkSumAction: CheckSumAction): Boolean {
        val meta = getMetaData(file)
        return if (meta != null) {
            // Checks MD5 sum
            var sum = Database.DatabaseFormatters.getMD5Sum(meta.getInt("id"))
            if (sum.isEmpty()) {
                sum = sourceJSON.getJSONObject(file.name.substring(0, file.name.length - 4)).getString("md5")
            }
            val content = getContent(file)
            val fileSum = md5(content)

            Log.i("FormatterInit", "${file.name}:\tSum required:{$sum}\tSum found:\t{$fileSum}")

            if (sum == fileSum) {
                checkSumAction.pass()
                true
            } else {
                checkSumAction.fail()
                false
            }
        } else {
            checkSumAction.noMeta()
            false
        }
    }


    /**
     * Loads a new JSON file to be used
     */
    class RefreshJSON(val activity: Activity, val extensionsFragment: ExtensionsFragment) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            val sourceFile = File(activity.filesDir.absolutePath + "/formatters.json")
            if (Utilities.isOnline) {
                val doc = WebViewScrapper.docFromURL("https://raw.githubusercontent.com/Doomsdayrs/shosetsu-extensions/master/src/main/resources/formatters.json", false)
                if (doc != null) {
                    val json = doc.body().text()
                    val out = FileOutputStream(sourceFile)
                    val writer = OutputStreamWriter(out)
                    writer.write(json)
                    writer.close()
                    out.flush()
                    out.close()
                    sourceJSON = JSONObject(json)
                }
            } else {
                Log.e("FormatterInit", "IsOffline, Cannot load data, Using stud")
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            Toast.makeText(activity, activity.getString(R.string.updated_extensions_list), Toast.LENGTH_SHORT).show()
            extensionsFragment.setData()
            extensionsFragment.adapter.notifyDataSetChanged()
        }
    }


}