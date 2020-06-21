package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.SHOSETSU_UPDATE_URL
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update

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
 * 20 / 06 / 2020
 */
class LoadAppUpdateUseCase(
		private val context: Context
) : (() -> Unit) {
	private fun getString(@StringRes id: Int) =
			context.getString(id)

	private fun utils() {
		AppUpdaterUtils(context)
				.setUpdateFrom(UpdateFrom.XML).setUpdateXML(SHOSETSU_UPDATE_URL)
				.withListener(object : AppUpdaterUtils.UpdateListener {
					override fun onSuccess(update: Update, isUpdateAvailable: Boolean) {
						Log.i("Latest Version", isUpdateAvailable.toString())
						Log.i("Latest Version", update.latestVersion)
						Log.i("Latest Version", update.latestVersionCode.toString())
					}

					override fun onFailed(error: AppUpdaterError) {
						Log.d("AppUpdater Error", "Something went wrong")
					}
				})
				.start()
	}

	override fun invoke() {
		AppUpdater(context)
				.setUpdateFrom(UpdateFrom.XML)
				.setUpdateXML(SHOSETSU_UPDATE_URL)
				.setDisplay(Display.DIALOG)
				.setTitleOnUpdateAvailable(getString(R.string.app_update_available))
				.setContentOnUpdateAvailable(getString(R.string.check_out_latest_app))
				.setTitleOnUpdateNotAvailable(getString(R.string.app_update_unavaliable))
				.setContentOnUpdateNotAvailable(getString(R.string.check_updates_later))
				.setButtonUpdate(getString(R.string.update_app_now_question))
				//.setButtonUpdateClickListener(...)
				.setButtonDismiss(getString(R.string.update_dismiss))
				//.setButtonDismissClickListener(...)
				.setButtonDoNotShowAgain(getString(R.string.update_not_interested))
				//.setButtonDoNotShowAgainClickListener(...)
				.setIcon(R.drawable.ic_system_update_alt_24dp)
				.setCancelable(true)
				.showEvery(5)
				.start()
	}
}