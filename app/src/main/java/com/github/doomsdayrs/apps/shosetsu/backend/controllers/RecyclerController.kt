package com.github.doomsdayrs.apps.shosetsu.backend.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context

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
 * 29 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 * <p>
 *     A simple controller that is just a recyclerView.
 *     Default will fill with a LinearLayoutManager
 * </p>
 */
abstract class RecyclerController(bundle: Bundle) : ViewedController(bundle) {
    constructor() : this(bundleOf())

    @LayoutRes
    override val layoutRes: Int = R.layout.recycler_controller

    @IdRes
    open val resourceID: Int = R.id.recyclerView

    var recyclerView: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = onCreateView1(inflater, container)
        recyclerView = view.findViewById(resourceID)!!
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        onViewCreated(view)
        return view
    }

    abstract override fun onViewCreated(view: View)
}