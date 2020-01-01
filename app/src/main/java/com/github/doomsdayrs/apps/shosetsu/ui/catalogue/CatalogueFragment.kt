package com.github.doomsdayrs.apps.shosetsu.ui.catalogue

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
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
class CatalogueFragment : Fragment() {
    var catalogueNovelCards = ArrayList<CatalogueNovelCard>()
    lateinit var formatter: Formatter
    lateinit var catalogueAdapter: CatalogueAdapter

    var currentMaxPage = 1
    var isInSearch = false
    private var dontRefresh = false
    var isQuery = false
    var empty: TextView? = null


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("list", catalogueNovelCards)
        outState.putInt("formatter", formatter.formatterID)
    }

    override fun onResume() {
        super.onResume()
        Log.d("Resume", "HERE")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Pause", "HERE")
        dontRefresh = true
    }

    override fun onDestroy() {
        super.onDestroy()
        dontRefresh = false
    }

    /**
     * Creates view
     *
     * @param inflater           inflates layouts and shiz
     * @param container          container of this fragment
     * @param savedInstanceState save file
     * @return View
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "CatalogueFragment")
        if (savedInstanceState != null) {
            catalogueNovelCards = (savedInstanceState.getSerializable("list") as ArrayList<CatalogueNovelCard>)
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"))!!
        }
        return inflater.inflate(R.layout.fragment_catalogue, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utilities.setActivityTitle(activity, formatter.name)
        swipeRefreshLayout!!.setOnRefreshListener(CatalogueRefresh(this))
        if (savedInstanceState == null && !dontRefresh) {
            Log.d("Process", "Loading up latest")
            setLibraryCards(catalogueNovelCards)
            if (catalogueNovelCards.size > 0) {
                catalogueNovelCards = ArrayList()
                catalogueAdapter.notifyDataSetChanged()
            }
            if (!formatter.hasCloudFlare) CataloguePageLoader(this).execute() else webView()
        } else setLibraryCards(catalogueNovelCards)
    }

    private fun webView() {
        val intent = Intent(activity, WebViewApp::class.java)
        intent.putExtra("url", formatter.getLatestURL(0))
        intent.putExtra("action", 1)
        startActivityForResult(intent, 42)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 42) { //TODO, Pass cookies from webview to okhttp
            val client = OkHttpClient.Builder()
                    .cookieJar(WebviewCookieHandler())
                    .build()
            formatter.client = client
            CataloguePageLoader(this).execute()
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
        val layoutManager: RecyclerView.LayoutManager
        layoutManager = GridLayoutManager(context, Utilities.calculateNoOfColumns(context!!, 200f), RecyclerView.VERTICAL, false)
        catalogueAdapter = CatalogueAdapter(recycleCards, this, formatter)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.addOnScrollListener(CatalogueHitBottom(this))
        recyclerView!!.adapter = catalogueAdapter
    }

    /**
     * Constructor
     */
    init {
        setHasOptionsMenu(true)
    }
}