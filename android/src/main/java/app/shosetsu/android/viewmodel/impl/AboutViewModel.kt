package app.shosetsu.android.viewmodel.impl

import android.app.Application
import android.content.Intent
import android.net.Uri
import app.shosetsu.android.backend.workers.onetime.AppUpdateCheckWorker
import app.shosetsu.android.domain.usecases.open.OpenInWebviewUseCase
import app.shosetsu.android.viewmodel.abstracted.AAboutViewModel
import com.github.doomsdayrs.apps.shosetsu.R

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
 * 01 / 10 / 2020
 */
class AboutViewModel(
	private val openInWebviewUseCase: OpenInWebviewUseCase,
	private val application: Application,
	private val manager: AppUpdateCheckWorker.Manager,
) : AAboutViewModel() {

	override fun openGithub() {
		application.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(application.getString(R.string.github_url))
		).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
	}

	override fun openWebsite() {
		application.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(application.getString(R.string.website_url))
		).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
	}

	override fun appUpdateCheck() {
		if (!manager.isRunning())
			manager.start()
	}

	override fun openExtensions() {
		application.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(application.getString(R.string.extensions_url))
		).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
	}

	override fun openDiscord() {
		application.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(application.getString(R.string.discord_url))
		).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
	}

	override fun openLicenses() {
	}

	override fun openPatreon() {
		application.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(application.getString(R.string.patreon_url))
		).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
	}
}