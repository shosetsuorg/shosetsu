package app.shosetsu.android.viewmodel.abstracted

import app.shosetsu.android.viewmodel.base.ShosetsuViewModel

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
abstract class AAboutViewModel : ShosetsuViewModel() {
	/** Call action to create popup window to open shosetsu github */
	abstract fun openGithub()

	/** Call action to create popup window to open shosetsu website */
	abstract fun openWebsite()

	/** Run an app update check */
	abstract fun appUpdateCheck()

	/** Call action to create popup window to open extensions github */
	abstract fun openExtensions()

	/** Call action to create popup window to open shosetsu discord */
	abstract fun openDiscord()

	/** Call action to create popup window to open licenses */
	abstract fun openLicenses()

	/** Call action to create popup window to open Doomsdayrs patreon */
	abstract fun openPatreon()
}