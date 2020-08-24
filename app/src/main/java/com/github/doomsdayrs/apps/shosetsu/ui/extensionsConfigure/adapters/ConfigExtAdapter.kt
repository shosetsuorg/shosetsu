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
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ExtensionConfigUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IExtensionsConfigureViewModel
import com.mikepenz.fastadapter.FastAdapter


/**
 * shosetsu
 * 21 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ConfigExtAdapter(private val viewModel: IExtensionsConfigureViewModel)
	: FastAdapter<ExtensionConfigUI>() {
	init {
		setHasStableIds(true)
	}

	override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
		super.onBindViewHolder(viewHolder, position)

		val holder = viewHolder as ExtensionConfigUI.ViewHolder
		val data = getItem(position)!!
		with(data) {
			val fom: Formatter? = null
			/*
			holder.switch.setOnCheckedChangeListener { _, isChecked ->
				launchIO {
					configureExtensions.viewModel.updateExtensionConfig(data, !isChecked)
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
			*/
		}
	}
}