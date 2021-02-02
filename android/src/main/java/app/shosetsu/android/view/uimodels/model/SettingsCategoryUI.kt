package app.shosetsu.android.view.uimodels.model

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.BindViewHolder
import app.shosetsu.common.enums.SettingCategory
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.RecyclerSettingsCardBinding

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
 */
data class SettingsCategoryUI(
	val category: SettingCategory,
	@StringRes
	val title: Int,
	@DrawableRes
	val image: Int
) : BaseRecyclerItem<SettingsCategoryUI.ViewHolder>() {
	override val layoutRes: Int = R.layout.recycler_settings_card
	override val type: Int = R.layout.recycler_settings_card
	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	class ViewHolder(view: View) :
		BindViewHolder<SettingsCategoryUI, RecyclerSettingsCardBinding>(view) {
		override val binding: RecyclerSettingsCardBinding = RecyclerSettingsCardBinding.bind(view)

		override fun RecyclerSettingsCardBinding.bindView(
			item: SettingsCategoryUI,
			payloads: List<Any>
		) {
			title.setText(item.title)
			title.setCompoundDrawablesRelativeWithIntrinsicBounds(
				item.image,
				0,
				0,
				0
			)
		}

		override fun RecyclerSettingsCardBinding.unbindView(item: SettingsCategoryUI) {
			title.text = null
			title.setCompoundDrawablesRelativeWithIntrinsicBounds(
				R.drawable.broken_image,
				0,
				0,
				0
			)
		}
	}
}