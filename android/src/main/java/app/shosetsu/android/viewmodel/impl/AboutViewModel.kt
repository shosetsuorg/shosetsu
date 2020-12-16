package app.shosetsu.android.viewmodel.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import app.shosetsu.android.backend.workers.onetime.AppUpdateWorker
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
	private val context: Context,
	private val manager: AppUpdateWorker.Manager,
) : AAboutViewModel() {

	override fun openGithub() {
		context.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(context.getString(R.string.github_url))
		).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
	}

	override fun openWebsite() {
		context.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(context.getString(R.string.website_url))
		).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
	}

	override fun appUpdateCheck() {
		if (!manager.isRunning())
			manager.start()
	}

	override fun openExtensions() {
		context.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(context.getString(R.string.extensions_url))
		).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
	}

	override fun openDiscord() {
		context.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(context.getString(R.string.discord_url))
		).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
	}

	override fun openLicenses() {
	}

	override fun openPatreon() {
		context.startActivity(Intent(
			Intent.ACTION_VIEW,
			Uri.parse(context.getString(R.string.patreon_url))
		).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
	}
}