package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder

import android.view.View
import android.view.View.OnLongClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueController
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async.NovelBackgroundAddC
import com.github.doomsdayrs.apps.shosetsu.ui.main.MainActivity
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment

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
 * 06 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class NovelListingCViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, OnLongClickListener {
    val imageView: ImageView = itemView.findViewById(R.id.image)
    val title: TextView = itemView.findViewById(R.id.title)
    lateinit var catalogueFragment: CatalogueController
    lateinit var formatter: Formatter

    var url: String? = null
    var novelID = 0
    override fun onClick(v: View) {
        val novelFragment = NovelFragment()
        novelFragment.novelURL = url!!
        novelFragment.formatter = formatter
        novelFragment.novelID = Database.DatabaseIdentification.getNovelIDFromNovelURL(url!!)
        if (catalogueFragment!!.activity != null) (catalogueFragment!!.activity as MainActivity?)!!.transitionView(novelFragment)
    }

    override fun onLongClick(view: View): Boolean {
        NovelBackgroundAddC(this).execute(view)
        return true
    }

    init {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }
}