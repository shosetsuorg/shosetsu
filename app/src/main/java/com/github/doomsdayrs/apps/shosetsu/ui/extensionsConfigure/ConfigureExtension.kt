package com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.observe
import app.shosetsu.lib.Filter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_FORMATTER
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.view.base.FastAdapterRecyclerController
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IExtensionSingleConfigureViewModel
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.xw.repo.BubbleSeekBar

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
 * 21 / 01 / 2020
 *
 * Opens up detailed view of an extension, allows modifications
 */
class ConfigureExtension(
		val bundle: Bundle
) : FastAdapterRecyclerController<ConfigureExtension.SettingWrapper>(bundle) {
	val viewModel: IExtensionSingleConfigureViewModel by viewModel()

	override val layoutRes: Int = R.layout.configure_extension_view
	override val resourceID: Int = R.id.settings

	override fun onViewCreated(view: View) {
		viewModel.setExtensionID(bundle.getInt(BUNDLE_FORMATTER))
	}

	override fun setupFastAdapter() {
		super.setupFastAdapter()
	}

	private fun observe() {
		viewModel.liveData.observe(this) {
			it.settings.forEach {}
		}
	}

	/**
	 * Wraps [Filter] and maps it to
	 * [com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base.SettingsItemData]
	 */
	data class SettingWrapper(
			private val filter: Filter<*>
	) : AbstractItem<SettingWrapper.ViewHolder>() {

		override var identifier: Long
			get() = filter.name.toLong()
			set(value) {}

		override val layoutRes: Int = R.layout.settings_item

		override val type: Int = filter.javaClass.hashCode()

		override fun getViewHolder(v: View): ViewHolder {
			TODO("Not yet implemented")
		}


		class ViewHolder(itemView: View) : FastAdapter.ViewHolder<SettingWrapper>(itemView) {
			val itemTitle: TextView = itemView.findViewById(R.id.settings_item_title)
			val itemDescription: TextView = itemView.findViewById(R.id.settings_item_desc)
			val button: Button = itemView.findViewById(R.id.button)
			val spinner: Spinner = itemView.findViewById(R.id.spinner)
			val textView: TextView = itemView.findViewById(R.id.text)
			val switchView: Switch = itemView.findViewById(R.id.switchView)
			val numberPicker: NumberPicker = itemView.findViewById(R.id.numberPicker)
			val colorBox: View = itemView.findViewById(R.id.colorBox)
			val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
			val seekbar: BubbleSeekBar = itemView.findViewById(R.id.bubbleSeekBar)

			override fun bindView(item: SettingWrapper, payloads: List<Any>) {
				TODO("Not yet implemented")
			}

			override fun unbindView(item: SettingWrapper) {
				TODO("Not yet implemented")
			}
		}
	}


}