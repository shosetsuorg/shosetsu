package app.shosetsu.android.ui.settings.sub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.SettingKey.*
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.view.compose.ShosetsuCompose
import app.shosetsu.android.view.compose.setting.ButtonSettingContent
import app.shosetsu.android.view.compose.setting.DropdownSettingContent
import app.shosetsu.android.view.compose.setting.SwitchSettingContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.viewmodel.abstracted.settings.AAdvancedSettingsViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG


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
class AdvancedSettings : ShosetsuController() {
	val viewModel: AAdvancedSettingsViewModel by viewModel()
	override val viewTitleRes: Int = R.string.settings_advanced

	/**
	 * Execute a purge from the view model, prompt to retry if failed
	 */
	private fun purgeNovelCache() {
		viewModel.purgeUselessData().observe(
			catch = {
				logE("Failed to purge")
				makeSnackBar(
					R.string.controller_settings_advanced_snackbar_purge_failure,
					LENGTH_LONG
				)
					?.setAction(R.string.retry) { purgeNovelCache() }
					?.show()
			}
		) {

			makeSnackBar(R.string.controller_settings_advanced_snackbar_purge_success)
				?.show()
		}
	}


	private fun clearWebCookies() {
		logI("User wants to clear cookies")
		logV("Clearing cookies")
		CookieManager.getInstance().removeAllCookies {
			logV("Cookies cleared")
			makeSnackBar(R.string.settings_advanced_clear_cookies_complete)?.show()
		}
	}


	private fun themeSelected(position: Int) {
		launchUI {
			activity?.onBackPressed()
			makeSnackBar(
				R.string.controller_settings_advanced_snackbar_ui_change,
				Snackbar.LENGTH_INDEFINITE
			)?.setAction(R.string.apply) {
				launchIO {
					viewModel.settingsRepo.setInt(AppTheme, position)
				}
			}?.show()
		}
	}

	private fun killCycleWorkers() {
		viewModel.killCycleWorkers()
		makeSnackBar(
			R.string.settings_advanced_snackbar_cycle_kill_success,
			LENGTH_LONG
		)?.apply {
			setAction(R.string.restart) {
				viewModel.startCycleWorkers()
				makeSnackBar(R.string.settings_advanced_cycle_start_success)?.show()
			}
		}?.show()
	}


	override fun onViewCreated(view: View) {}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setViewTitle()
		setContent {
			ShosetsuCompose {
				AdvancedSettingsContent(
					viewModel,
					onThemeSelected = ::themeSelected,
					onPurgeNovelCache = ::purgeNovelCache,
					onPurgeChapterCache = {},
					onKillCycleWorkers = ::killCycleWorkers,
					onClearCookies = ::clearWebCookies,
					onForceRepoSync = {
						viewModel.forceRepoSync()
					}
				)
			}
		}
	}
}


@Composable
fun AdvancedSettingsContent(
	viewModel: AAdvancedSettingsViewModel,
	onThemeSelected: (Int) -> Unit,
	onPurgeNovelCache: () -> Unit,
	onPurgeChapterCache: () -> Unit,
	onKillCycleWorkers: () -> Unit,
	onForceRepoSync: () -> Unit,
	onClearCookies: () -> Unit
) {
	LazyColumn(
		contentPadding = PaddingValues(
			top = 16.dp,
			start = 16.dp,
			end = 16.dp,
			bottom = 64.dp
		)
	) {
		item {
			val choice by viewModel.settingsRepo.getIntFlow(AppTheme)
				.collectAsState(AppTheme.default)

			DropdownSettingContent(
				title = stringResource(R.string.theme),
				description = stringResource(R.string.settings_advanced_theme_desc),
				choices = stringArrayResource(R.array.application_themes),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				selection = choice,
				onSelection = onThemeSelected
			)
		}

		item {
			ButtonSettingContent(
				title = stringResource(R.string.remove_novel_cache),
				description = stringResource(R.string.settings_advanced_purge_novel_cache),
				buttonText = stringResource(R.string.settings_advanced_purge_button),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				onClick = onPurgeNovelCache
			)
		}

		item {
			SwitchSettingContent(
				title = stringResource(R.string.settings_advanced_verify_checksum_title),
				description = stringResource(R.string.settings_advanced_verify_checksum_desc),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				repo = viewModel.settingsRepo,
				key = VerifyCheckSum
			)
		}

		item {
			SwitchSettingContent(
				title = stringResource(R.string.settings_advanced_require_double_back_title),
				description = stringResource(R.string.settings_advanced_require_double_back_desc),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				repo = viewModel.settingsRepo,
				key = RequireDoubleBackToExit
			)
		}

		item {
			ButtonSettingContent(
				title = stringResource(R.string.settings_advanced_kill_cycle_workers_title),
				description = stringResource(R.string.settings_advanced_kill_cycle_workers_desc),
				buttonText = stringResource(R.string.settings_advanced_kill_cycle_workers_button),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				onClick = onKillCycleWorkers
			)
		}

		item {
			ButtonSettingContent(
				title = stringResource(R.string.settings_advanced_force_repo_update_title),
				description = stringResource(R.string.settings_advanced_force_repo_update_desc),
				buttonText = stringResource(R.string.force),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				onClick = onForceRepoSync
			)
		}

		item {
			ButtonSettingContent(
				title = stringResource(R.string.settings_advanced_clear_cookies_title),
				description = stringResource(R.string.settings_advanced_clear_cookies_desc),
				buttonText = stringResource(R.string.settings_advanced_clear_cookies_button),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				onClick = onClearCookies
			)
		}

		item {
			SwitchSettingContent(
				title = stringResource(R.string.settings_advanced_true_chapter_delete_title),
				description = stringResource(R.string.settings_advanced_true_chapter_delete_desc),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				repo = viewModel.settingsRepo,
				key = ExposeTrueChapterDelete
			)
		}

		item {
			SwitchSettingContent(
				title = stringResource(R.string.settings_advanced_log_title),
				description = stringResource(R.string.settings_advanced_log_desc),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				repo = viewModel.settingsRepo,
				key = LogToFile
			)
		}

		item {
			SwitchSettingContent(
				title = stringResource(R.string.settings_advanced_auto_bookmark_title),
				description = stringResource(R.string.settings_advanced_auto_bookmark_desc),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				repo = viewModel.settingsRepo,
				key = AutoBookmarkFromQR
			)
		}
	}
}
