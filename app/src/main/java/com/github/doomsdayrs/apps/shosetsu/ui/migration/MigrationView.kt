package com.github.doomsdayrs.apps.shosetsu.ui.migration

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters.MigratingMapAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters.MigratingNovelAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters.MigrationViewCatalogueAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.migration.async.MigrationViewLoad
import com.github.doomsdayrs.apps.shosetsu.ui.migration.async.Transfer
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers.Companion.asCatalogue
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueCard
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard
import kotlinx.android.synthetic.main.migrate_source_view.*

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
 * shosetsu
 * 19 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class MigrationView : AppCompatActivity() {

    val novelResults = ArrayList<ArrayList<Novel>>()
    private var catalogues: ArrayList<CatalogueCard> = ArrayList()

    var novels: ArrayList<NovelCard> = ArrayList()
    private val confirmedMappings = ArrayList<Array<String>>()

    private var t: Transfer? = null

    var target = -1
    var selection = 0
    var secondSelection = -1
    var mappingNovelsAdapter: MigratingMapAdapter? = null

    private var selectedNovelsAdapters: MigratingNovelAdapter? = null

    private var load: MigrationViewLoad? = null


    override fun onDestroy() {
        if (t != null) {
            t!!.isNotCanceled = false
            t!!.cancel(true)
        }
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        novels = Utilities.deserializeString(intent.getStringExtra("selected")!!) as ArrayList<NovelCard>

        setContentView(R.layout.migrate_source_view)

        // Fills in dummy data
        run { for (x in novels!!.indices) novelResults.add(ArrayList()) }

        // Sets selected novels
        setUpSelectedNovels()

        // Sets the novels to map
        setUpMappingNovels()

        // Sets cancel button
        val cancel = findViewById<Button>(R.id.cancel)
        cancel.setOnClickListener {
            secondSelection = -1
            refresh()
        }
        cancel.setOnLongClickListener {
            load!!.cancel(true)
            finish()
            true
        }

        // Sets confirm button
        run {
            val confirm = findViewById<Button>(R.id.confirm)
            confirm.setOnClickListener {
                if (secondSelection != -1) {
                    //Adds mapping targets

                    val map = arrayOfNulls<String>(2)
                    map[0] = novels[selection].novelURL
                    map[1] = novelResults[selection][secondSelection].link
                    confirmedMappings.add(map as Array<String>)

                    novelResults.removeAt(selection)
                    novels.removeAt(selection)
                    when {
                        selection != novels.size -> {
                            Log.d("Increment", "Increase")
                        }
                        selection - 1 != -1 -> {
                            Log.d("Increment", "Decrease")
                            selection--
                        }
                        else -> {
                            t = Transfer(confirmedMappings, target, this)
                            t!!.execute()
                        }
                    }
                    secondSelection = -1
                    refresh()
                } else Toast.makeText(applicationContext, "You need to select something!", Toast.LENGTH_SHORT).show()
            }
            confirm.setOnLongClickListener {
                load!!.cancel(true)
                true
            }
        }
        if (catalogues.isEmpty()) catalogues = asCatalogue

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        val adapter = MigrationViewCatalogueAdapter(catalogues, this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        fillData();
    }

    fun fillData() {
        if (load == null) load = MigrationViewLoad(this)
        if (load!!.isCancelled) {
            load = MigrationViewLoad(this)
        }
        load!!.execute()
    }

    private fun setUpSelectedNovels() {
        selectedNovelsAdapters = MigratingNovelAdapter(this)
        selection_view!!.layoutManager = LinearLayoutManager(applicationContext)
        selection_view!!.adapter = selectedNovelsAdapters
    }

    fun refresh() {
        selection_view!!.post { selectedNovelsAdapters!!.notifyDataSetChanged() }
        mapping_view!!.post { mappingNovelsAdapter!!.notifyDataSetChanged() }
    }

    private fun setUpMappingNovels() {
        mappingNovelsAdapter = MigratingMapAdapter(this)
        mapping_view!!.layoutManager = LinearLayoutManager(applicationContext)
        mapping_view!!.adapter = mappingNovelsAdapter
    }
}