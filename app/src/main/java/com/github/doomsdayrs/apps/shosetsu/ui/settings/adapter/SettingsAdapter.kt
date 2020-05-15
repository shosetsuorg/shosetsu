package com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.SettingsCard
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsController.Types
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.*
import com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.BackupSettings
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsCardViewHolder
import java.util.*

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
class SettingsAdapter(
		private val settingsCards: ArrayList<SettingsCard>,
		private val router: Router
) : RecyclerView.Adapter<SettingsCardViewHolder>() {
	override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SettingsCardViewHolder =
			SettingsCardViewHolder(
					LayoutInflater.from(viewGroup.context).inflate(
							R.layout.recycler_settings_card,
							viewGroup,
							false
					)
			)

	override fun onBindViewHolder(settingsCardViewHolder: SettingsCardViewHolder, i: Int) {
		settingsCards[i].id.let { type ->
			with(settingsCardViewHolder) {
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
				libraryCardTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
						when (type) {
							Types.READER -> R.drawable.ic_book_24dp
							Types.DOWNLOAD -> R.drawable.ic_file_download
							Types.BACKUP -> R.drawable.ic_stat_name
							Types.VIEW -> R.drawable.ic_view_module
							Types.ADVANCED -> R.drawable.ic_settings
							Types.INFO -> R.drawable.ic_info_outline_24dp
						},
						0,
						0,
						0
				)
			}
		}

	}

	override fun getItemCount(): Int = settingsCards.size

}