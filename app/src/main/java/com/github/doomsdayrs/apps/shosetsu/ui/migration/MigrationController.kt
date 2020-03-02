package com.github.doomsdayrs.apps.shosetsu.ui.migration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.ViewedController
import com.github.doomsdayrs.apps.shosetsu.variables.obj.DefaultScrapers
import com.squareup.picasso.Picasso
import com.yarolegovich.discretescrollview.DiscreteScrollView
import java.io.Serializable

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
 * yes, a THIRD ONE
 */
class MigrationController(bundle: Bundle) : ViewedController(bundle) {
    companion object {
        const val TARGETS_BUNDLE_KEY = "targets"
    }

    class Transferee(val original: Int, var targetFormatterID: Int = -1, var listings: Array<Novel.Listing> = arrayOf(), var selectedURL: String = "") : Serializable

    override val layoutRes: Int = R.layout.migration_view

    private var transferees: Array<Transferee>

    init {
        val arrayList = ArrayList<Transferee>()
        bundle.getIntArray(TARGETS_BUNDLE_KEY)?.forEach {
            arrayList.add(Transferee(it))
        }
        transferees = arrayList.toTypedArray()
    }


    @Attach(R.id.novels_to_transfer)
    var novelsFromRecyclerView: DiscreteScrollView? = null


    //

    @Attach(R.id.targetSearching)
    var targetSearching: View? = null

    @Attach(R.id.searchView)
    var searchView: SearchView? = null

    @Attach(R.id.swipeRefreshLayout)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @Attach(R.id.targetView)
    var targetView: RecyclerView? = null


    //

    @Attach(R.id.catalogue_selection_view)
    var catalogueSelectionView: View? = null

    @Attach(R.id.catalogue_selection)
    var catalogueSelection: RecyclerView? = null


    override fun onSaveInstanceState(outState: Bundle) {
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    }

    override fun onViewCreated(view: View) {
        novelsFromRecyclerView?.addOnItemChangedListener { viewHolder, item ->
            setupViewWithTransferee(item)
        }
        setupViewWithTransferee(0)
    }

    fun setupViewWithTransferee(position: Int) {
        val target = transferees[position]
        if (target.targetFormatterID == -1) {
            catalogueSelectionView?.visibility = VISIBLE
            targetSearching?.visibility = INVISIBLE

        } else {
            catalogueSelectionView?.visibility = INVISIBLE
            targetSearching?.visibility = VISIBLE

        }
    }

    class CatalogueSelectionAdapter(val transferee: Transferee) : RecyclerView.Adapter<CatalogueSelectionAdapter.CatalogueHolder>() {
        class CatalogueHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val title: TextView = itemView.findViewById(R.id.textView)
            var id: Int = -1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogueHolder {
            return CatalogueHolder(LayoutInflater.from(parent.context).inflate(R.layout.catalogue_item_card, parent, false))
        }

        override fun getItemCount(): Int {
            return DefaultScrapers.formatters.size
        }

        override fun onBindViewHolder(holder: CatalogueHolder, position: Int) {
            val form = DefaultScrapers.formatters[position]
            holder.title.text = form.name
            if (form.imageURL.isNotEmpty())
                Picasso.get()
                        .load(form.imageURL)
                        .into(holder.imageView)

        }
    }

}