package com.github.doomsdayrs.apps.shosetsu.ui.migration.async

import android.os.AsyncTask
import android.util.Log
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers.getByID
import kotlinx.android.synthetic.main.migrate_source_view.*
import org.luaj.vm2.LuaTable

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
 * 22 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class MigrationViewLoad(private val migrationView: MigrationView) : AsyncTask<Void?, Void?, Void?>() {
    private var targetFormat: Formatter = getByID(migrationView.target)

    override fun doInBackground(vararg voids: Void?): Void? {
        Log.d("Searching with", targetFormat.name)
        for (x in migrationView.novels.indices) { // Retrieves search results

            val table = LuaTable()
            table["query"] = migrationView.novels[x].title
            val list = targetFormat.search(table) {}
            migrationView.novelResults[x].addAll(list)
        }
        return null
    }

    override fun onPreExecute() {
        //migrationView.swipeRefreshLayout.isRefreshing = true
    }

    override fun onPostExecute(aVoid: Void?) {
        //  migrationView.swipeRefreshLayout.isRefreshing = false
        migrationView.mapping_view.post { migrationView.mappingNovelsAdapter?.notifyDataSetChanged() }
    }

}