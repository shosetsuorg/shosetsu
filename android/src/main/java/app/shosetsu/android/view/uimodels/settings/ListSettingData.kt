package app.shosetsu.android.view.uimodels.settings

import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.databinding.SettingsItemBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

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

/**
 * Shosetsu
 *
 * A list of [SettingsItemData]
 *
 * @since 21 / 06 / 2021
 * @author Doomsdayrs
 */
class ListSettingData(id: Int) : SettingsItemData(id) {

	val itemAdapter: ItemAdapter<SettingsItemData> by lazy { ItemAdapter() }

	@DrawableRes
	var openRes: Int = -1

	@DrawableRes
	var closeRes: Int = -1

	private fun toggle(holder: SettingsItemBinding) {
		holder.recyclerView.isVisible = !holder.recyclerView.isVisible
		if (holder.recyclerView.isVisible) {
			if (closeRes != -1)
				holder.imageButton.setImageResource(closeRes)
		} else
			if (openRes != -1)
				holder.imageButton.setImageResource(openRes)
	}

	override fun bindBinding(holder: SettingsItemBinding, payloads: List<Any>) {
		super.bindBinding(holder, payloads)
		holder.rightField.isVisible = true
		holder.bottomField.isVisible = true

		holder.root.setOnClickListener {
			toggle(holder)
		}

		holder.imageButton.isVisible = true
		if (closeRes != -1)
			holder.imageButton.setImageResource(closeRes)

		holder.imageButton.setOnClickListener {
			toggle(holder)
		}

		val fastAdapter: FastAdapter<SettingsItemData> = FastAdapter.with(itemAdapter)

		holder.recyclerView.isVisible = true

		holder.recyclerView.adapter = fastAdapter
		holder.divider.isVisible = true
	}

	override fun unbindBinding(holder: SettingsItemBinding) {
		super.unbindBinding(holder)
		holder.rightField.isVisible = false
		holder.bottomField.isVisible = false

		holder.imageButton.isVisible = false
		holder.imageButton.setOnClickListener(null)
		holder.imageButton.setImageDrawable(null)

		holder.recyclerView.isVisible = false
		holder.recyclerView.adapter = null

		holder.divider.isVisible = false
	}
}