package com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsController.Types
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.*
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.BackupSettings
import com.google.android.material.card.MaterialCardView

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
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class SettingsCardViewHolder(
		itemView: View,
		private val router: Router
) : RecyclerView.ViewHolder(itemView) {
	private val libraryCardTitle: TextView = itemView.findViewById(R.id.recycler_settings_title)
	private val cardView: MaterialCardView = itemView.findViewById(R.id.settings_card)

	fun setType(type: Types) {
		Log.d("SettingsCardVH", "Type: ${type.name}")
		cardView.setOnClickListener {
			router.pushController(
					when (type) {
						Types.VIEW -> ViewSettings()
						Types.INFO -> InfoSettings()
						Types.ADVANCED -> AdvancedSettings()
						Types.DOWNLOAD -> DownloadSettings()
						Types.BACKUP -> BackupSettings()
						Types.READER -> ReaderSettings()
					}.withFadeTransaction()
			)
		}
		libraryCardTitle.text = when (type) {
			Types.DOWNLOAD -> itemView.context.getString(R.string.download)
			Types.VIEW -> itemView.context.getString(R.string.view)
			Types.ADVANCED -> itemView.context.getString(R.string.advanced)
			Types.INFO -> itemView.context.getString(R.string.info)
			Types.BACKUP -> itemView.context.getString(R.string.backup)
			Types.READER -> itemView.context.getString(R.string.reader)
		}
	}

}