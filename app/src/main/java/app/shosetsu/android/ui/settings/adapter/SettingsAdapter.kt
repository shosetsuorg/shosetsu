package app.shosetsu.android.ui.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.SettingsCard
import app.shosetsu.android.common.ext.getString
import app.shosetsu.android.common.ext.withFadeTransaction
import app.shosetsu.android.ui.settings.SettingsController.Types.*
import app.shosetsu.android.ui.settings.sub.*
import app.shosetsu.android.ui.settings.sub.backup.BackupSettings
import app.shosetsu.android.ui.settings.viewHolder.SettingsCardViewHolder
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.apps.shosetsu.R
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
		private val router: Router,
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
								VIEW -> ViewSettings()
								INFO -> InfoSettings()
								ADVANCED -> AdvancedSettings()
								DOWNLOAD -> DownloadSettings()
								BACKUP -> BackupSettings()
								READER -> ReaderSettings()
								UPDATE -> UpdateSettings()
							}.withFadeTransaction()
					)
				}
				libraryCardTitle.text = when (type) {
					DOWNLOAD -> itemView.getString(R.string.download)
					VIEW -> itemView.getString(R.string.view)
					ADVANCED -> itemView.getString(R.string.advanced)
					INFO -> itemView.getString(R.string.info)
					BACKUP -> itemView.getString(R.string.backup)
					READER -> itemView.getString(R.string.reader)
					UPDATE -> itemView.getString(R.string.update)
				}

				libraryCardTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
						when (type) {
							READER -> R.drawable.ic_book_24dp
							DOWNLOAD -> R.drawable.ic_file_download
							BACKUP -> R.drawable.ic_system_update_alt_24dp
							VIEW -> R.drawable.ic_view_module
							ADVANCED -> R.drawable.ic_settings
							INFO -> R.drawable.ic_info_outline_24dp
							UPDATE -> R.drawable.ic_update_24dp
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