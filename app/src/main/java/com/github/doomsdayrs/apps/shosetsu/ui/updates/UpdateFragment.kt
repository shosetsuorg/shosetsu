package com.github.doomsdayrs.apps.shosetsu.ui.updates

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.Update
import com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters.UpdatedNovelsAdapter
import java.util.*

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
 * 20 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdateFragment : Fragment(R.layout.updates_list) {
    var date: Long = -1
    private val novels = ArrayList<Int>()
    private var updates = ArrayList<Update>()
    private var recyclerView: RecyclerView? = null

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("date", date)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (date == -1L) if (savedInstanceState != null) date = savedInstanceState.getLong("date")
        try {
            updates = Database.DatabaseUpdates.getTimeBetween(date, date + 86399999)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        for (update in updates) if (!novels.contains(update.novelID)) novels.add(update.novelID)
        recyclerView = view.findViewById(R.id.recycler_update)
        chapterSetUp()
        Log.d("Updates on this day: ", "" + updates.size)
    }

    private fun chapterSetUp() {
        if (recyclerView != null && activity != null) {
            val updatedNovelsAdapter = UpdatedNovelsAdapter(novels, updates, activity!!)
            //UpdatedChaptersAdapter updatersAdapter = new UpdatedChaptersAdapter(updates, getActivity());
            recyclerView!!.layoutManager = LinearLayoutManager(context)
            recyclerView!!.adapter = updatedNovelsAdapter
            recyclerView!!.post { updatedNovelsAdapter.notifyDataSetChanged() }
        }
    }
}