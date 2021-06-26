package app.shosetsu.android.ui.migration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.ext.context
import app.shosetsu.android.common.ext.picasso
import app.shosetsu.android.view.controller.ViewedController
import app.shosetsu.common.dto.HResult
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.MigrationViewBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.MigrationViewBinding.inflate

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
class MigrationController(bundle: Bundle) : ViewedController<MigrationViewBinding>(bundle) {
	companion object {
		const val TARGETS_BUNDLE_KEY: String = "targets"
	}

	override fun bindView(inflater: LayoutInflater): MigrationViewBinding =
		inflate(inflater)

	class Transferee(
		val original: Int,
		var targetExtensionID: Int = -1,
		var listings: Array<Novel.Listing> = arrayOf(),
		var selectedURL: String = "",
	)

	private var transferees: Array<Transferee>

	init {
		val arrayList = ArrayList<Transferee>()
		bundle.getIntArray(TARGETS_BUNDLE_KEY)?.forEach {
			arrayList.add(Transferee(original = it))
		}
		transferees = arrayList.toTypedArray()
	}

	override fun onViewCreated(view: View) {
		binding.catalogueSelection.layoutManager = LinearLayoutManager(context)
		binding.novelsToTransfer.adapter = TransfereeAdapter(this)
		binding.novelsToTransfer.addOnItemChangedListener { _, item ->
			setupViewWithTransferee(item)
		}
		setupViewWithTransferee(0)
	}

	/**
	 * @param position [Int] position
	 */
	fun setupViewWithTransferee(position: Int) {
		val target = transferees[position]
		if (target.targetExtensionID == -1) {
			binding.catalogueSelectionView.visibility = VISIBLE
			binding.targetSearching.visibility = INVISIBLE
			binding.catalogueSelection.adapter = CatalogueSelectionAdapter(this, position)
		} else {
			binding.catalogueSelectionView.visibility = GONE
			binding.targetSearching.visibility = VISIBLE
			// TODO
		}
	}

	class TransfereeAdapter(private val migrationController: MigrationController) :
		RecyclerView.Adapter<TransfereeAdapter.TransfereeViewHolder>() {
		class TransfereeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
			val imageView: ImageView = itemView.findViewById(R.id.imageView)
			val title: AppCompatTextView = itemView.findViewById(R.id.title)
		}

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransfereeViewHolder {
			return TransfereeViewHolder(
				LayoutInflater.from(parent.context).inflate(
					R.layout.recycler_novel_card,
					parent,
					false
				)
			)
		}

		override fun getItemCount(): Int {
			return migrationController.transferees.size
		}

		override fun onBindViewHolder(holder: TransfereeViewHolder, position: Int) {
			val tran = migrationController.transferees[position]
			//holder.title.text = tran.novelCard.title
			//if (tran.novelCard.imageURL.isNotEmpty())
			//	Picasso.get().load("TODO").into(holder.imageView)
		}
	}

	class CatalogueSelectionAdapter(
		private val migrationController: MigrationController,
		private val transfereePosition: Int
	) : RecyclerView.Adapter<CatalogueSelectionAdapter.CatalogueHolder>() {
		class CatalogueHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
			val imageView: ImageView = itemView.findViewById(R.id.imageView)
			val title: AppCompatTextView = itemView.findViewById(R.id.title)
			var id: Int = -1
		}

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogueHolder {
			return CatalogueHolder(
				LayoutInflater.from(parent.context)
					.inflate(R.layout.catalogue_item_card, parent, false)
			)
		}

		override fun getItemCount(): Int {
			return -1
		}

		override fun onBindViewHolder(holder: CatalogueHolder, position: Int) {
			val form: IExtension? = null
			holder.title.text = form?.name
			if (form?.imageURL?.isNotEmpty()!!)
				picasso(form.imageURL, holder.imageView)

			holder.id = form.formatterID
			holder.itemView.setOnClickListener {
				migrationController.transferees[transfereePosition].targetExtensionID = holder.id
				migrationController.setupViewWithTransferee(transfereePosition)
			}
		}
	}

	override fun handleErrorResult(e: HResult.Error) {
		TODO("Not yet implemented")
	}

}