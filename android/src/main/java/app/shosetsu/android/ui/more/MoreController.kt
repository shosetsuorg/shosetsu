package app.shosetsu.android.ui.more

import android.view.LayoutInflater
import android.view.View
import app.shosetsu.android.common.ext.shosetsuPush
import app.shosetsu.android.ui.about.AboutController
import app.shosetsu.android.ui.downloads.DownloadsController
import app.shosetsu.android.ui.repository.RepositoryController
import app.shosetsu.android.ui.settings.SettingsController
import app.shosetsu.android.view.controller.ViewedController
import app.shosetsu.android.view.controller.base.CollapsedToolBarController
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerMoreBinding

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
 */

/**
 * shosetsu
 * 12 / 09 / 2020
 *
 * Option for download queue
 */
class MoreController
	: ViewedController<ControllerMoreBinding>(), CollapsedToolBarController {

	override fun onViewCreated(view: View) {
		binding.download.setOnClickListener {
			router.shosetsuPush(DownloadsController())
		}
		binding.repositories.setOnClickListener {
			router.shosetsuPush(RepositoryController())
		}
		binding.settings.setOnClickListener {
			router.shosetsuPush(SettingsController())
		}
		binding.info.setOnClickListener {
			router.shosetsuPush(AboutController())
		}
	}

	override fun bindView(inflater: LayoutInflater): ControllerMoreBinding =
		ControllerMoreBinding.inflate(inflater)

	override fun handleErrorResult(e: HResult.Error) {
		TODO("Not yet implemented")
	}

}