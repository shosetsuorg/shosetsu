package com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.Update
import com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder.UpdatedNovelHolder

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
 * 03 / 09 / 2019
 *
 * @author github.com/doomsdayrs
 */
internal class UpdatedNovelsAdapter(private val novelIDs: ArrayList<Int>, val updates: ArrayList<Update>, val activity: Activity) : RecyclerView.Adapter<UpdatedNovelHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdatedNovelHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.updated_novel_card, parent, false)
        return UpdatedNovelHolder(view,activity)
    }

    override fun onBindViewHolder(holder: UpdatedNovelHolder, position: Int) {
        val novelID = novelIDs[position]

        val subUpdates: ArrayList<Update> = ArrayList()
        for (update in updates)
            if (update.novelID == novelID)
                subUpdates.add(update)

        holder.novelID = novelID
        holder.updates = subUpdates
    }

    override fun getItemCount(): Int {
        return novelIDs.size
    }
}