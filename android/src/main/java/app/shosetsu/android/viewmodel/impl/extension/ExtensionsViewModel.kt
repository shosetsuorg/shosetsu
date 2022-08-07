package app.shosetsu.android.viewmodel.impl.extension

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

import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.SettingKey.BrowseFilteredLanguages
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.model.local.ExtensionInstallOptionEntity
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.CancelExtensionInstallUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.RequestInstallExtensionUseCase
import app.shosetsu.android.domain.usecases.StartRepositoryUpdateManagerUseCase
import app.shosetsu.android.domain.usecases.load.LoadBrowseExtensionsUseCase
import app.shosetsu.android.view.uimodels.model.BrowseExtensionUI
import app.shosetsu.android.viewmodel.abstracted.ABrowseViewModel
import app.shosetsu.android.viewmodel.base.ExposedSettingsRepoViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExtensionsViewModel(
	private val getBrowseExtensions: LoadBrowseExtensionsUseCase,
	private val startRepositoryUpdateManager: StartRepositoryUpdateManagerUseCase,
	private val installExtensionUI: RequestInstallExtensionUseCase,
	private val cancelExtensionInstall: CancelExtensionInstallUseCase,
	private var isOnlineUseCase: IsOnlineUseCase,
	override val settingsRepo: ISettingsRepository
) : ABrowseViewModel(), ExposedSettingsRepoViewModel {


	override fun refresh() {
		startRepositoryUpdateManager()
	}

	override fun installExtension(
		extension: BrowseExtensionUI,
		option: ExtensionInstallOptionEntity
	) {
		launchIO {
			installExtensionUI(extension, option)
		}
	}

	override fun updateExtension(ext: BrowseExtensionUI) {
		launchIO {
			installExtensionUI(ext)
		}
	}

	override fun cancelInstall(ext: BrowseExtensionUI) {
		launchIO {
			cancelExtensionInstall(ext)
		}
	}

	private val extensionFlow by lazy {
		getBrowseExtensions()
	}

	private val languageListFlow by lazy {
		extensionFlow.map { list ->
			list.map { LanguageFilter(it.lang, it.displayLang) }.distinct()
				.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.displayLang })
		}
	}

	private val searchTermFlow: MutableStateFlow<String> by lazy { MutableStateFlow("") }

	override val filteredLanguagesLive: Flow<FilteredLanguages> by lazy {
		languageListFlow.combine(settingsRepo.getStringSetFlow(BrowseFilteredLanguages)) { languageResult, filteredLanguages ->

			val map = HashMap<String, Boolean>().apply {
				languageResult.forEach { language ->
					this[language.lang] = filteredLanguages.none { language.lang == it }
				}
			}
			FilteredLanguages(languageResult, map)
		}.onIO()
	}

	private val onlyInstalledFlow by lazy {
		settingsRepo.getBooleanFlow(SettingKey.BrowseOnlyInstalled)
	}


	override val onlyInstalledLive: Flow<Boolean> by lazy {
		onlyInstalledFlow.onIO()
	}

	override fun setLanguageFiltered(language: String, state: Boolean) {
		logI("Language $language updated to state $state")
		launchIO {
			try {
				settingsRepo.getStringSet(BrowseFilteredLanguages).let { set ->
					val mutableSet = set.toMutableSet()

					if (state) {
						mutableSet.removeAll { it == language }
					} else {
						mutableSet.add(language)
					}

					try {
						settingsRepo.setStringSet(BrowseFilteredLanguages, mutableSet)
						logV("Done")
					} catch (e: Exception) {
						logE("Failed to update $BrowseFilteredLanguages", e)
					}

				}
			} catch (e: Exception) {
				logE("Failed to retrieve $BrowseFilteredLanguages", e)

			}
		}
	}

	override fun showOnlyInstalled(state: Boolean) {
		logI("Show only installed new state: $state")
		launchIO {
			try {
				settingsRepo.setBoolean(SettingKey.BrowseOnlyInstalled, state)
				logV("Done")
			} catch (e: Exception) {
				logE("Failed to update ${SettingKey.BrowseOnlyInstalled}", e)
			}
		}
	}

	override fun setSearch(name: String) {
		searchTermFlow.value = name
	}

	override fun resetSearch() {
		searchTermFlow.value = ""
	}

	override val searchTermLive: Flow<String> by lazy {
		searchTermFlow.onIO()
	}

	override val liveData: Flow<List<BrowseExtensionUI>> by lazy {
		extensionFlow.transformLatest { list ->
			emitAll(
				settingsRepo.getStringSetFlow(BrowseFilteredLanguages)
					.combine(
						settingsRepo.getBooleanFlow(SettingKey.BrowseOnlyInstalled)
							.combine(searchTermFlow) { onlyInstalled, searchTerm ->
								onlyInstalled to searchTerm
							}) { languagesToFilter, (onlyInstalled, searchTerm) ->
						list
							.asSequence()
							.let { sequence ->
								if (searchTerm.isNotBlank())
									sequence.filter { it.name.contains(searchTerm) }
								else sequence
							}
							.filter { if (onlyInstalled) it.isInstalled else true }
							.filterNot { languagesToFilter.contains(it.lang) }
							.sortedBy { it.name }
							.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.displayLang })
							.sortedBy { !it.isInstalled }
							.sortedBy { !it.isUpdateAvailable }
							.toList()
					})
		}.onIO()
	}

	override fun isOnline(): Boolean = isOnlineUseCase()

}