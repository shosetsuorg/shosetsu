package com.github.doomsdayrs.apps.shosetsu.ui.migration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters.NewTargetAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters.NewTransferalsAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import kotlinx.android.synthetic.main.new_migrartion_view.*

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
 * 03 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class NewMigrationView : AppCompatActivity() {
    // Options for which to search with
    val catalogueOptions = DefaultScrapers.asCatalogue

    private val transferalsAdapter = NewTransferalsAdapter(this)
    private val targetAdapter = NewTargetAdapter(this)


    // Current index
    var index = 0

    // Novel to transfer
    var novelsToTransfer: ArrayList<Int> = ArrayList()

    // Novel to transfer too
    var novelResults = ArrayList<ArrayList<Novel>>()

    // Is Single target or multiple
    var isMultiTargeted = false

    // SelectedCatalogue
    var targetCatalogue = 0

    // Which novel is selected to where
    var selectedCatalogues: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utilities.setupTheme(this)
        setContentView(R.layout.new_migrartion_view)
        novelsToTransfer = intent.getIntegerArrayListExtra("selection")!!
        setupTransferals()
        setupTargets()
    }

    fun setupTransferals() {
        novels_to_transfer?.adapter = transferalsAdapter
        novels_to_transfer?.setSlideOnFling(true)
        novels_to_transfer?.addOnItemChangedListener { _, i ->
            index = i
            targetView?.post { targetAdapter.notifyDataSetChanged() }
        }
    }

    fun setupTargets() {
        targetView.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        targetView.adapter = targetAdapter
    }

}