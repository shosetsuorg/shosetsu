package com.github.doomsdayrs.apps.shosetsu.ui.catalogue

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.WebviewCookieHandler
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters.CatalogueAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async.CataloguePageLoader
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueHitBottom
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueRefresh
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueSearchQuery
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard
import kotlinx.android.synthetic.main.fragment_catalogue.*
import okhttp3.OkHttpClient
import java.util.*

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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO fix issue with not loading
class CatalogueFragment : Fragment(R.layout.fragment_catalogue) {
    private var cataloguePageLoader: CataloguePageLoader? = null
    var catalogueNovelCards = ArrayList<CatalogueNovelCard>()
    lateinit var formatter: Formatter
    lateinit var catalogueAdapter: CatalogueAdapter

    var currentMaxPage = 1
    var isInSearch = false
    private var dontRefresh = false
    var isQuery = false

    init {
        setHasOptionsMenu(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("list", catalogueNovelCards)
        if (this::formatter.isInitialized)
            outState.putInt("formatter", formatter.formatterID)
        else outState.putInt("formatter", -1)
    }

    override fun onPause() {
        super.onPause()
        Log.d("Pause", "HERE")
        dontRefresh = true
        cataloguePageLoader?.cancel(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        dontRefresh = false
        cataloguePageLoader?.cancel(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            catalogueNovelCards = (savedInstanceState.getSerializable("list") as ArrayList<CatalogueNovelCard>)
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"))
        }
        Utilities.setActivityTitle(activity, formatter.name)
        swipeRefreshLayout!!.setOnRefreshListener(CatalogueRefresh(this))
        if (savedInstanceState == null && !dontRefresh) {
            Log.d("Process", "Loading up latest")
            setLibraryCards(catalogueNovelCards)
            if (catalogueNovelCards.size > 0) {
                catalogueNovelCards = ArrayList()
                catalogueAdapter.notifyDataSetChanged()
            }
            if (!formatter.hasCloudFlare) {
                executePageLoader()
            } else {
                val intent = Intent(activity, WebViewApp::class.java)
                intent.putExtra("url", formatter.getLatestURL(0))
                intent.putExtra("action", 1)
                startActivityForResult(intent, 42)
            }
        } else setLibraryCards(catalogueNovelCards)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 42) {
            //TODO, Pass cookies from webview to okhttp
            val client = OkHttpClient.Builder()
                    .cookieJar(WebviewCookieHandler())
                    .build()
            executePageLoader()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.toolbar_library, menu)
        val searchView = menu.findItem(R.id.library_search).actionView as SearchView
        searchView.setOnQueryTextListener(CatalogueSearchQuery(this))
        searchView.setOnCloseListener {
            isQuery = false
            isInSearch = false
            setLibraryCards(catalogueNovelCards)
            true
        }
    }

    fun setLibraryCards(recycleCards: ArrayList<CatalogueNovelCard>) {
        recyclerView!!.setHasFixedSize(false)

        if (Settings.novelCardType == 0) {
            catalogueAdapter = CatalogueAdapter(recycleCards, this, formatter, R.layout.recycler_novel_card)
            recyclerView!!.layoutManager = GridLayoutManager(context, Utilities.calculateNoOfColumns(context!!, 200f), RecyclerView.VERTICAL, false)
        }
        if (Settings.novelCardType == 0) {
            catalogueAdapter = CatalogueAdapter(recycleCards, this, formatter, R.layout.recycler_novel_card_compressed)
            recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        recyclerView!!.adapter = catalogueAdapter
        recyclerView!!.addOnScrollListener(CatalogueHitBottom(this))

    }

    fun executePageLoader() {
        if (cataloguePageLoader?.isCancelled == false)
            cataloguePageLoader = CataloguePageLoader(this)

        if (cataloguePageLoader == null)
            cataloguePageLoader = CataloguePageLoader(this)

        cataloguePageLoader!!.execute(currentMaxPage)
    }

}