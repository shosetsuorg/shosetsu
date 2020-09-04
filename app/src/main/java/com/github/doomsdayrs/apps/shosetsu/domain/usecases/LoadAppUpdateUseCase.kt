package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.SHOSETSU_UPDATE_URL
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchUI
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.quickie
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import okhttp3.OkHttpClient
import org.xmlpull.v1.XmlPullParserException

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
		private val context: Context,
		private val okHttpClient: OkHttpClient,
		private val shareUseCase: ShareUseCase,
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


	private data class DebugAppUpdate(
			@JsonProperty("latestVersion")
			val version: Int,
			@JsonProperty("url")
			val url: String,
			@JsonProperty("releaseNotes")
			val notes: List<String>,
	)

	override fun invoke() {
		if (!BuildConfig.DEBUG)
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
		else {
			launchIO {
				okHttpClient.quickie(SHOSETSU_UPDATE_URL).takeIf { it.code == 200 }?.let { r ->
					try {
						val mapper = ObjectMapper().registerKotlinModule()
								.readValue<DebugAppUpdate>(r.body!!.string())
						val currentV = BuildConfig.VERSION_NAME.substringAfter("r").toInt()
						when {
							mapper.version < currentV -> {
								Log.i(logID(), "This a future release")
							}
							mapper.version > currentV -> {
								Log.i(logID(), "Update found")
								AlertDialog.Builder(context).apply {
									setTitle(R.string.update_app_now_question)
									setMessage("${mapper.version}\n" + mapper.notes.joinToString("\n"))
									setPositiveButton(R.string.update) { it, _ ->
										shareUseCase(mapper.url, "Open discord and get the release")
										it.dismiss()
									}
									setOnDismissListener {
										it.dismiss()
									}
								}.let {
									launchUI { it.show() }
								}
							}
							mapper.version == currentV -> {
								Log.i(logID(), "This the current release")
							}
						}
					} catch (e: XmlPullParserException) {
					}
					r.close()
				}
			}
		}
	}
}