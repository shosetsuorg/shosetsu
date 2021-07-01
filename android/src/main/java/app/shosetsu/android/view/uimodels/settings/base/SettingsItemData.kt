package app.shosetsu.android.view.uimodels.settings.base

import android.graphics.Typeface
import android.os.Build
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.BindViewHolder
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.SettingsItemBinding

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
 * shosetsu
 * 25 / 06 / 2020
 */
abstract class SettingsItemData(
	val id: Int
) : BaseRecyclerItem<SettingsItemData.ViewHolder>() {
	/** Min version required for this setting to be visible */
	var minVersionCode: Int = Build.VERSION_CODES.Q

	@StringRes
	var titleRes: Int = -1
	var titleText: String = ""
	var isBoldTitle: Boolean = false

	@StringRes
	var descRes: Int = -1
	var descText: String = ""

	override var identifier: Long
		get() = id.toLong()
		set(value) {}

	@CallSuper
	open fun bindBinding(holder: SettingsItemBinding, payloads: List<Any>) = with(holder) {
		itemView.isSelected = isSelected

		if (titleRes != -1)
			settingsItemTitle.setText(titleRes)
		else
			settingsItemTitle.text = titleText

		if (isBoldTitle)
			settingsItemTitle.setTypeface(null, Typeface.BOLD)

		if (descRes != -1) {
			settingsItemDesc.isVisible = true
			settingsItemDesc.setText(descRes)
		} else if (descText.isNotEmpty()) {
			settingsItemDesc.isVisible = true
			settingsItemDesc.text = descText
		}
	}

	@CallSuper
	open fun unbindBinding(holder: SettingsItemBinding) = with(holder) {
		settingsItemTitle.text = null
		settingsItemDesc.text = null
	}

	class ViewHolder(itemView: View) :
		BindViewHolder<SettingsItemData, SettingsItemBinding>(itemView) {
		override val binding: SettingsItemBinding = SettingsItemBinding.bind(view)

		override fun SettingsItemBinding.bindView(item: SettingsItemData, payloads: List<Any>) {
			item.bindBinding(this, payloads)
		}

		override fun SettingsItemBinding.unbindView(item: SettingsItemData) {
			item.unbindBinding(this)
		}
	}

	override val layoutRes: Int = R.layout.settings_item
	override val type: Int = javaClass.hashCode()
	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
}
