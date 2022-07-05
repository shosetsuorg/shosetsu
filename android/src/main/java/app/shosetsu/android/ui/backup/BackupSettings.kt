package app.shosetsu.android.ui.backup

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LifecycleOwner
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.consts.BACKUP_FILE_EXTENSION
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.makeSnackBar
import app.shosetsu.android.common.ext.toast
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.compose.ShosetsuCompose
import app.shosetsu.android.view.compose.setting.ButtonSettingContent
import app.shosetsu.android.view.compose.setting.SwitchSettingContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.viewmodel.abstracted.settings.ABackupSettingsViewModel
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
 * Shosetsu
 * 13 / 07 / 2019
 */
class BackupSettings : ShosetsuController() {
	override val viewTitleRes: Int = R.string.controller_backup_title

	val viewModel: ABackupSettingsViewModel by viewModel()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedViewState: Bundle?
	): View = ComposeView(requireContext()).apply {
		setViewTitle()
		setContent {
			ShosetsuCompose {
				BackupSettingsContent(
					viewModel,
					backupNow = {
						// Stops novel updates while backup is taking place
						// Starts backing up data
						viewModel.startBackup()
					},
					restore = {
						viewModel.restore(it)
					},
					export = {
						viewModel.holdBackupToExport(it)
						performExportSelection()
					},
					performFileSelection = {
						performFileSelection()
					}
				)
			}
		}
	}

	/*
	 * Options for restore
	 * > Internal
	 * >> Open pop up list to select file
	 * > Via external
	 * >> Open file explorer and select file
	 */
	lateinit var selectBackupToRestoreLauncher: ActivityResultLauncher<Array<String>>
	lateinit var selectLocationToExportLauncher: ActivityResultLauncher<String>

	override fun onLifecycleCreate(owner: LifecycleOwner, registry: ActivityResultRegistry) {
		selectBackupToRestoreLauncher = registry.register(
			"backup_settings_load_backup_rq#",
			owner,
			ActivityResultContracts.OpenDocument()
		) { uri: Uri? ->
			if (uri == null) {
				logE("Cancelled")
				return@register
			}

			// TODO Possibly add popup verification to make sure that an invalid file ext is oki

			context?.toast("Restoring now...")
			viewModel.restore(uri)
		}

		selectLocationToExportLauncher = registry.register(
			"backup_settings_point_export_rq#",
			owner,
			ActivityResultContracts.CreateDocument()
		) { uri: Uri? ->
			if (uri == null) {
				logE("Cancelled")
				viewModel.clearExport()
				return@register
			}

			context?.toast("Exporting now")
			viewModel.exportBackup(uri)
		}
	}


	private fun performExportSelection() {
		val backupFileName = viewModel.getBackupToExport()

		if (backupFileName == null) {
			makeSnackBar(R.string.controller_backup_error_unselected)
				?.setAction(R.string.retry) {
					performExportSelection()
				}?.show()
			return
		}

		selectLocationToExportLauncher.launch(backupFileName)
	}

	private fun performFileSelection() {
		selectBackupToRestoreLauncher.launch(arrayOf("application/octet-stream"))
	}

}

@Composable
fun BackupSelectionDialog(
	viewModel: ABackupSettingsViewModel,
	dismiss: () -> Unit,
	optionSelected: (String) -> Unit,
) {
	val options by viewModel.loadInternalOptions().collectAsState(listOf())
	Dialog(
		onDismissRequest = {
			dismiss()
		}
	) {
		Card(
			modifier = Modifier.fillMaxHeight(.6f)
		) {
			Column(
				modifier = Modifier.padding(8.dp),
			) {
				Text(
					stringResource(R.string.settings_backup_alert_select_location_title),
					style = MaterialTheme.typography.h6,
					modifier = Modifier.padding(
						bottom = 16.dp,
						top = 8.dp,
						start = 24.dp,
						end = 24.dp
					)
				)

				LazyColumn(
					modifier = Modifier
						.padding(bottom = 8.dp, start = 24.dp, end = 24.dp)
						.fillMaxWidth()
				) {
					items(options) { option ->
						TextButton(onClick = {
							optionSelected(option)
							dismiss()
						}) {
							Text(
								option.removePrefix("shosetsu-backup-")
									.removeSuffix(".$BACKUP_FILE_EXTENSION")
							)
						}
					}
				}

				Row(
					horizontalArrangement = Arrangement.End,
					modifier = Modifier.fillMaxWidth()
				) {
					TextButton(
						onClick = dismiss,
					) {
						Text(stringResource(android.R.string.cancel))
					}
				}
			}
		}
	}
}

@Composable
fun BackupSettingsContent(
	viewModel: ABackupSettingsViewModel,
	backupNow: () -> Unit,
	performFileSelection: () -> Unit,
	restore: (String) -> Unit,
	export: (String) -> Unit
) {
	LazyColumn(
		contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 64.dp)
	) {

		item {
			SwitchSettingContent(
				stringResource(R.string.backup_chapters_option),
				stringResource(R.string.backup_chapters_option_description),
				viewModel.settingsRepo,
				SettingKey.ShouldBackupChapters,
				modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.backup_settings_option),
				stringResource(R.string.backup_settings_option_desc),
				viewModel.settingsRepo,
				SettingKey.ShouldBackupSettings,
				modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.backup_only_modified_title),
				stringResource(R.string.backup_only_modified_desc),
				viewModel.settingsRepo,
				SettingKey.BackupOnlyModifiedChapters,
				modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.backup_restore_print_chapters_title),
				stringResource(R.string.backup_restore_print_chapters_desc),
				viewModel.settingsRepo,
				SettingKey.RestorePrintChapters,
				modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.backup_restore_low_storage),
				stringResource(R.string.backup_restore_low_storage_desc),
				viewModel.settingsRepo,
				SettingKey.BackupOnLowStorage,
				modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.backup_restore_low_battery),
				stringResource(R.string.backup_restore_low_battery_desc),
				viewModel.settingsRepo,
				SettingKey.BackupOnLowBattery,
				modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth()
			)
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			item {
				SwitchSettingContent(
					stringResource(R.string.backup_restore_only_idle),
					stringResource(R.string.backup_restore_only_idle_desc),
					viewModel.settingsRepo,
					SettingKey.BackupOnlyWhenIdle,
					modifier = Modifier
						.padding(bottom = 8.dp)
						.fillMaxWidth()
				)
			}

		item {
			ButtonSettingContent(
				stringResource(R.string.backup_now),
				"",
				stringResource(R.string.backup_now),
				onClick = backupNow, modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth()
			)
		}

		item {
			var isDialogShowing: Boolean by remember { mutableStateOf(false) }
			var isRestoreDialogShowing: Boolean by remember { mutableStateOf(false) }

			ButtonSettingContent(
				stringResource(R.string.restore_now),
				"",
				stringResource(R.string.restore_now),
				modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth()
			) {
				isDialogShowing = true
			}
			if (isRestoreDialogShowing)
				BackupSelectionDialog(viewModel, { isRestoreDialogShowing = false }) {
					restore(it)
				}

			if (isDialogShowing)
				AlertDialog(
					onDismissRequest = {
						isDialogShowing = false
					},
					buttons = {
						Row {
							TextButton(onClick = {
								// Open file selector
								performFileSelection()
								isDialogShowing = false
							}) {
								Text(stringResource(R.string.settings_backup_alert_location_external))
							}

							TextButton(onClick = {
								isDialogShowing = false
								isRestoreDialogShowing = true
							}) {
								Text(stringResource(R.string.settings_backup_alert_location_internal))
							}
						}
					},
					title = {
						Text(stringResource(R.string.settings_backup_alert_select_location_title))
					}
				)
		}
		item {
			var isExportShowing: Boolean by remember { mutableStateOf(false) }

			if (isExportShowing) {
				BackupSelectionDialog(viewModel, { isExportShowing = false }) {
					export(it)
				}
			}

			ButtonSettingContent(
				stringResource(R.string.settings_backup_export),
				"",
				stringResource(R.string.settings_backup_export),
				onClick = {
					isExportShowing = true
				},
				modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth()
			)
		}

	}
}