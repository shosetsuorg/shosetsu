package app.shosetsu.android.ui.settings.sub

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.compose.setting.HeaderSettingContent
import app.shosetsu.android.view.compose.setting.SliderSettingContent
import app.shosetsu.android.view.compose.setting.SwitchSettingContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.viewmodel.abstracted.settings.AUpdateSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme

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
class UpdateSettings : ShosetsuController() {
	override val viewTitleRes: Int = com.github.doomsdayrs.apps.shosetsu.R.string.settings_update
	val viewModel: AUpdateSettingsViewModel by viewModel()

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
				UpdateSettingsContent(
					viewModel,
				)
			}
		}
	}
}


@Composable
fun UpdateSettingsContent(viewModel: AUpdateSettingsViewModel) {
	LazyColumn(
		contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 64.dp, top = 16.dp)
	) {
		item {
			HeaderSettingContent(
				stringResource(R.string.settings_update_header_novel),
				modifier = Modifier.padding(bottom = 8.dp)
			)
		}

		item {
			SliderSettingContent(
				stringResource(R.string.settings_update_novel_frequency_title),
				stringResource(R.string.settings_update_novel_frequency_desc),
				1..168,
				{
					"$it Hour(s)"
				},
				viewModel.settingsRepo,
				SettingKey.NovelUpdateCycle,
				modifier = Modifier.padding(bottom = 8.dp),
				haveSteps = false
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_novel_on_update_title),
				stringResource(R.string.settings_update_novel_on_update_desc),
				viewModel.settingsRepo,
				SettingKey.DownloadNewNovelChapters,
				modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_novel_only_ongoing_title),
				stringResource(R.string.settings_update_novel_only_ongoing_desc),
				viewModel.settingsRepo,
				SettingKey.OnlyUpdateOngoingNovels,
				modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
			)
		}
		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_novel_on_metered_title),
				stringResource(R.string.settings_update_novel_on_metered_desc),
				viewModel.settingsRepo,
				SettingKey.NovelUpdateOnMeteredConnection,
				modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
			)
		}
		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_novel_on_low_bat_title),
				stringResource(R.string.settings_update_novel_on_low_bat_desc),
				viewModel.settingsRepo,
				SettingKey.NovelUpdateOnLowBattery,
				modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
			)
		}
		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_novel_on_low_sto_title),
				stringResource(R.string.settings_update_novel_on_low_sto_desc),
				viewModel.settingsRepo,
				SettingKey.NovelUpdateOnLowStorage,
				modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
			)
		}
		if (BuildConfig.VERSION_CODE > Build.VERSION_CODES.M)
			item {
				SwitchSettingContent(
					stringResource(R.string.settings_update_novel_only_idle_title),
					stringResource(R.string.settings_update_novel_only_idle_desc),
					viewModel.settingsRepo,
					SettingKey.NovelUpdateOnlyWhenIdle,
					modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
				)
			}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_novel_notification_style_title),
				stringResource(R.string.settings_update_novel_notification_style_desc),
				viewModel.settingsRepo,
				SettingKey.UpdateNotificationStyle,
				modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_novel_show_progress_title),
				stringResource(R.string.settings_update_novel_show_progress_desc),
				viewModel.settingsRepo,
				SettingKey.NovelUpdateShowProgress,
				modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_novel_classic_notification_title),
				stringResource(R.string.settings_update_novel_classic_notification_desc),
				viewModel.settingsRepo,
				SettingKey.NovelUpdateClassicFinish,
				modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
			)
		}
		item {
			HeaderSettingContent(
				stringResource(R.string.settings_update_header_repositories),
				modifier = Modifier.padding(bottom = 8.dp)
			)
		}

		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_repo_on_metered_title),
				stringResource(R.string.settings_update_repo_on_metered_desc),
				viewModel.settingsRepo,
				SettingKey.RepoUpdateOnMeteredConnection,
				modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
			)
		}
		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_repo_on_low_bat_title),
				stringResource(R.string.settings_update_repo_on_low_bat_desc),
				viewModel.settingsRepo,
				SettingKey.RepoUpdateOnLowBattery,
				modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
			)
		}
		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_repo_on_low_sto_title),
				stringResource(R.string.settings_update_repo_on_low_sto_desc),
				viewModel.settingsRepo,
				SettingKey.RepoUpdateOnLowStorage,
				modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
			)
		}
		item {
			SwitchSettingContent(
				stringResource(R.string.settings_update_repo_disable_on_fail_title),
				stringResource(R.string.settings_update_repo_disable_on_fail_desc),
				viewModel.settingsRepo,
				SettingKey.RepoUpdateDisableOnFail, modifier = Modifier.fillMaxWidth()
			)
		}
	}
}