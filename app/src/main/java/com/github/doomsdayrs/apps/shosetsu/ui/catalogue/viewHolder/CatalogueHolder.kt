package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.lib.Formatter
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.isOnline
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogController

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
 */ /**
 * shosetsu
 * 18 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class CatalogueHolder(itemView: View, private val router: Router) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
	val imageView: ImageView = itemView.findViewById(R.id.imageView)
	val title: TextView = itemView.findViewById(R.id.title)
	lateinit var formatter: Formatter

	init {
		itemView.setOnClickListener(this)
	}


	override fun onClick(v: View) {
		Log.d("FormatterSelection", formatter.name)
		if (isOnline) {
			val bundle = Bundle()
			bundle.putInt("formatter", formatter.formatterID)
			val catalogueFragment = CatalogController(bundle)
			router.pushController(catalogueFragment.withFadeTransaction())
			//TODO Router push to catalogue
		} else v.context.toast(R.string.you_not_online)
	}

}