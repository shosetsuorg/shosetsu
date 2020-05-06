package com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.adapters
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.defaultListing
import com.github.doomsdayrs.apps.shosetsu.common.ext.setDefaultListing
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.ConfigureExtensions
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.viewHolders.ConfigExtView
import com.squareup.picasso.Picasso


/**
 * shosetsu
 * 21 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ConfigExtAdapter(private val configureExtensions: ConfigureExtensions)
	: RecyclerView.Adapter<ConfigExtView>() {
	init {
		setHasStableIds(true)
	}

	override fun getItemId(position: Int) = position.toLong()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfigExtView =
			ConfigExtView(LayoutInflater.from(parent.context).inflate(
					R.layout.alert_extensions_configure_card,
					parent,
					false
			))

	override fun getItemCount(): Int = configureExtensions.recyclerArray.size

	override fun onBindViewHolder(holder: ConfigExtView, position: Int) {
		with(configureExtensions.recyclerArray[position]) {
			val name: String = name
			val image: String = imageURL ?: ""
			val enabled = enabled
			val fom: Formatter? = configureExtensions.viewModel.loadFormatterIfEnabled(this)

			if (image.isNotEmpty())
				Picasso.get().load(image).into(holder.imageView)

			holder.title.text = name
			holder.switch.isChecked = enabled
			holder.switch.setOnCheckedChangeListener { _, isChecked ->
				if (isChecked) {
					configureExtensions.viewModel.disableExtension(this) {
						if (it.enabled) {
							holder.switch.setText(R.string.enabled)
						} else {
							configureExtensions.context?.toast("Failed to disable")
						}
					}
				} else {
					configureExtensions.viewModel.enableExtension(this) {
						if (it.enabled) {
							holder.switch.setText(R.string.disabled)
						} else {
							configureExtensions.context?.toast("Failed to enable")
						}
					}
				}
			}

			fom?.let {
				if (fom.listings.size > 1) {
					val a = ArrayList<String>()
					holder.constraintLayout.visibility = View.VISIBLE
					fom.listings.forEach { a.add(it.name) }
					holder.spinner.adapter = ArrayAdapter(
							holder.itemView.context,
							android.R.layout.simple_spinner_item,
							a
					)
					holder.spinner.setSelection(fom.defaultListing)
					var first = true
					holder.spinner.onItemSelectedListener = object
						: AdapterView.OnItemSelectedListener {
						override fun onNothingSelected(parent: AdapterView<*>?) {}

						override fun onItemSelected(
								parent: AdapterView<*>?,
								view: View?,
								position: Int,
								id: Long
						) {
							if (!first)
								if (!fom.setDefaultListing(position))
									view?.context?.toast(R.string.invalid_selection)
								else
									view?.context?.toast(
											"${view.context.getString(R.string.set_to)} " +
													"${parent?.getItemAtPosition(position)}")
							else first = !first
						}
					}
				}
			}
		}
	}


}