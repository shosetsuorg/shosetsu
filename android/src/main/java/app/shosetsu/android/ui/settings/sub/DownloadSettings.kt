package app.shosetsu.android.ui.settings.sub

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.consts.ActivityRequestCodes.REQUEST_CODE_DIRECTORY
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.view.compose.setting.SliderSettingContent
import app.shosetsu.android.view.compose.setting.SwitchSettingContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.viewmodel.abstracted.settings.ADownloadSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.snackbar.Snackbar

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
 * Shosetsu
 * 13 / 07 / 2019
 */
class DownloadSettings : ShosetsuController() {
	override val viewTitleRes: Int = R.string.settings_download
	val viewModel: ADownloadSettingsViewModel by viewModel()

	override fun onViewCreated(view: View) {
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setViewTitle()
		setContent {
			MdcTheme {
				DownloadSettingsContent(
					viewModel,
					::performFileSearch
				)
			}
		}
	}

	private fun setDownloadDirectory(dir: String) {
		//s.downloadDirectory = dir
		// TODO ???????????
	}

	private fun performFileSearch() {
		context?.toast(
			"Please make sure this is on the main storage, " +
					"SD card storage is not functional yet", duration = Toast.LENGTH_LONG
		)
		val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
		i.addCategory(Intent.CATEGORY_DEFAULT)
		activity?.startActivityForResult(Intent.createChooser(i, "Choose directory"), 42)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == REQUEST_CODE_DIRECTORY && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				val path = data.data?.path
				Log.i("Selected Folder", "Uri: $path")
				if (path != null)
					setDownloadDirectory(path.substring(path.indexOf(":") + 1))
				else context?.toast("Path is null")
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		launchUI {
			if (!viewModel.downloadWorkerSettingsChanged) return@launchUI

			makeSnackBar(
				getString(
					R.string.controller_settings_restart_worker,
					getString(R.string.worker_title_download)
				),
				Snackbar.LENGTH_LONG
			)?.setAction(R.string.restart) {
				viewModel.restartDownloadWorker()
			}?.setOnDismissed { _, _ ->
				viewModel.downloadWorkerSettingsChanged = false
			}?.show()
		}

	}
}

@Composable
fun DownloadSettingsContent(
	viewModel: ADownloadSettingsViewModel,
	performFileSearch: () -> Unit
) {
	LazyColumn(
		contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 64.dp)
	) {
		item {
			SliderSettingContent(
				"Download thread pool size",
				"How many simultaneous downloads occur at once",
				1..6,
				{ "$it" },
				viewModel.settingsRepo,
				SettingKey.DownloadThreadPool,
				modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
			)
		}

		item {
			SliderSettingContent(
				"Download threads per Extension",
				"How many simultaneous downloads per extension that can occur at once",
				1..6,
				{ "$it" },
				viewModel.settingsRepo,
				SettingKey.DownloadExtThreads,
				modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
			)
		}

		// TODO Figure out how to change download directory
		item {
			SwitchSettingContent(
				stringResource(R.string.download_chapter_updates),
				stringResource(R.string.download_chapter_updates_desc),
				viewModel.settingsRepo,
				SettingKey.DownloadNewNovelChapters,
				modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
			)
		}

		item {
			SwitchSettingContent(
				"Allow downloading on metered connection",
				"",//TODO Description
				viewModel.settingsRepo,
				SettingKey.DownloadOnMeteredConnection,
				modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
			)
		}
		item {
			SwitchSettingContent(
				"Download on low battery",
				"",//TODO Description
				viewModel.settingsRepo,
				SettingKey.DownloadOnLowBattery,
				modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
			)
		}
		item {
			SwitchSettingContent(
				"Download on low storage",
				"",//TODO Description
				viewModel.settingsRepo,
				SettingKey.DownloadOnLowStorage,
				modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
			)
		}

		if (BuildConfig.VERSION_CODE >= VERSION_CODES.M)
			item {
				SwitchSettingContent(
					"Download only when idle",
					"",//TODO Description
					viewModel.settingsRepo,
					SettingKey.DownloadOnlyWhenIdle,
					modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
				)
			}

		item {
			SwitchSettingContent(
				"Bookmarked novel on download",
				"If a novel is not bookmarked when a chapter is downloaded, this will",
				viewModel.settingsRepo,
				SettingKey.BookmarkOnDownload,
				modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
			)
		}
		item {
			SwitchSettingContent(
				stringResource(R.string.settings_download_notify_extension_install_title),
				stringResource(R.string.settings_download_notify_extension_install_desc),
				viewModel.settingsRepo,
				SettingKey.NotifyExtensionDownload,
				modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
			)
		}

		item {
			SliderSettingContent(
				stringResource(R.string.settings_download_delete_on_read_title),
				stringResource(R.string.settings_download_delete_on_read_desc),
				-1..3,
				{
					when (it) {
						-1 -> "Disabled"
						0 -> "Current"
						1 -> "Previous"
						2 -> "2nd to last"
						3 -> "3rd to last"
						else -> "Invalid"
					}
				},
				viewModel.settingsRepo,
				SettingKey.DeleteReadChapter,
				modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
				maxHeaderSize = 80.dp
			)
		}

		item {
			SwitchSettingContent(
				"Unique chapter notification",
				"Create a notification for each chapters status when downloading",
				viewModel.settingsRepo,
				SettingKey.DownloadNotifyChapters,
				modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
			)
		}
	}

}