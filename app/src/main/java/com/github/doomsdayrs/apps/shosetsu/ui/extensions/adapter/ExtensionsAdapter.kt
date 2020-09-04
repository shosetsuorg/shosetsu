package com.github.doomsdayrs.apps.shosetsu.ui.extensions.adapter
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

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import com.github.doomsdayrs.apps.shosetsu.common.utils.FormatterUtils.Companion.compareVersions
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ExtensionUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.IExtensionsViewModel
import com.mikepenz.fastadapter.FastAdapter

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsAdapter(private val viewModel: IExtensionsViewModel)
	: FastAdapter<ExtensionUI>() {

	init {
		setHasStableIds(true)
	}


	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		getItem(position)?.let { extensionUI ->
			var installed = false
			var update = false
			if (extensionUI.installed && extensionUI.isExtEnabled) {
				installed = true
				if (compareVersions(extensionUI.installedVersion ?: "",
								extensionUI.repositoryVersion
						))
					update = true
			}
			(holder as ExtensionUI.ViewHolder).button.setOnClickListener {
				try {
					if (!installed || update)
						viewModel.installExtension(extensionUI)
					else
						viewModel.uninstallExtension(extensionUI)
				} catch (e: Exception) {
					it.context.toast("Holy shit what happened")
					Log.e(logID(), "Unhandled exception", e)
				}
			}
		}
	}
}