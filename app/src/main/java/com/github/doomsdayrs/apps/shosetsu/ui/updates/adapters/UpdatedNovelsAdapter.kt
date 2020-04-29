package com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters
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
 */
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.UpdateEntity
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdateController
import com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder.UpdatedNovelHolder
import com.github.doomsdayrs.apps.shosetsu.variables.ext.launchAsync
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IUpdatesViewModel
import com.squareup.picasso.Picasso

 /**
 * shosetsu
 * 03 / 09 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatedNovelsAdapter(
		updateController: UpdateController,
		val activity: Activity

) : RecyclerView.Adapter<UpdatedNovelHolder>() {
	val novelIDs: ArrayList<Int> = updateController.novelIDs
	val updates: ArrayList<UpdateEntity> = updateController.recyclerArray
	val updatesViewModel: IUpdatesViewModel = updateController.updatesViewModel


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
			UpdatedNovelHolder(LayoutInflater.from(parent.context).inflate(
					R.layout.updated_novel_card,
					parent,
					false
			), activity)

	override fun onBindViewHolder(holder: UpdatedNovelHolder, position: Int) {
		val returnedNovelID = novelIDs[position]

		val subUpdates: ArrayList<UpdateEntity> = ArrayList()
		val updatersAdapter = UpdatedChaptersAdapter(holder, updatesViewModel)

		updates.filter { it.novelID == returnedNovelID }.forEach { subUpdates.add(it) }

		with(holder) {
			var expanded = false
			novelID = returnedNovelID
			launchAsync {
				val urlImageTitle = updatesViewModel.getURLImageTitle(returnedNovelID)
				if (urlImageTitle.imageURL.isNotEmpty())
					Picasso.get().load(urlImageTitle.imageURL).into(imageView)
				novelName = urlImageTitle.title
			}

			updates = subUpdates
			chip.text = updates.size.toString()
			updatersAdapter.size = if (updates.size > 20) 5 else updates.size
			updatersAdapter.notifyDataSetChanged()

			button.setOnClickListener {
				if (expanded) {
					button.setImageResource(R.drawable.ic_baseline_expand_more_24)
					recyclerView.visibility = View.GONE
					expand.visibility = View.GONE
				} else {
					button.setImageResource(R.drawable.ic_baseline_expand_less_24)
					recyclerView.visibility = View.VISIBLE

					if (updatersAdapter.size < updates.size)
						expand.visibility = View.VISIBLE
				}
				expanded = !expanded
			}
			expand.setOnClickListener {
				updatersAdapter.size =
						if (updatersAdapter.size + 5 >= updates.size) {
							expand.visibility = View.GONE
							updates.size
						} else updatersAdapter.size + 5
				updatersAdapter.notifyDataSetChanged()
			}

			recyclerView.adapter = updatersAdapter
			recyclerView.layoutManager = LinearLayoutManager(
					activity,
					LinearLayoutManager.VERTICAL,
					false
			)
		}
	}

	override fun getItemCount() = novelIDs.size
}