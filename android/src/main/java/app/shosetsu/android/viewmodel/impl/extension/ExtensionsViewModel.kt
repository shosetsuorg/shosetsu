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

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.*
import app.shosetsu.android.domain.usecases.load.LoadExtensionsUIUseCase
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.viewmodel.abstracted.ABrowseViewModel
import app.shosetsu.android.viewmodel.base.ExposedSettingsRepoViewModel
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.consts.settings.SettingKey.BrowseFilteredLanguages
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsViewModel(
	private val getExtensionsUI: LoadExtensionsUIUseCase,
	private val startRepositoryUpdateManager: StartRepositoryUpdateManagerUseCase,
	private val installExtensionUI: RequestInstallExtensionUseCase,
	private val uninstallExtensionUI: UninstallExtensionUIUseCase,
	private val cancelExtensionInstall: CancelExtensionInstallUseCase,
	private var isOnlineUseCase: IsOnlineUseCase,
	private val reportException: ReportExceptionUseCase,
	override val settingsRepo: ISettingsRepository
) : ABrowseViewModel(), ExposedSettingsRepoViewModel {

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportException(error)
	}

	override fun refreshRepository() {
		startRepositoryUpdateManager()
	}

	override fun installExtension(extensionUI: ExtensionUI) {
		launchIO {
			installExtensionUI(extensionUI)
		}
	}

	override fun uninstallExtension(extensionUI: ExtensionUI) {
		launchIO {
			uninstallExtensionUI(extensionUI)
		}
	}

	override fun cancelInstall(extensionUI: ExtensionUI) {
		launchIO {
			cancelExtensionInstall(extensionUI)
		}
	}

	@ExperimentalCoroutinesApi
	private val extensionFlow by lazy {
		getExtensionsUI()
	}

	@ExperimentalCoroutinesApi
	val languageListFlow by lazy {
		extensionFlow.map { result ->
			result.transformToSuccess { list ->
				list.map { it.lang }.distinct()
			}
		}
	}

	private val searchTermFlow: MutableStateFlow<String> by lazy { MutableStateFlow("") }

	override val filteredLanguagesLive: LiveData<HResult<FilteredLanguages>> by lazy {
		languageListFlow.combine(settingsRepo.getStringSetFlow(BrowseFilteredLanguages)) { languageResult, filteredLanguages ->
			val languageList = languageResult.unwrap() ?: listOf()

			val map = HashMap<String, Boolean>().apply {
				languageList.forEach { language ->
					this[language] = !filteredLanguages.contains(language)
				}
			}
			successResult(FilteredLanguages(languageList, map))
		}.asIOLiveData()
	}

	private val onlyInstalledFlow by lazy {
		settingsRepo.getBooleanFlow(SettingKey.BrowseOnlyInstalled)
	}


	override val onlyInstalledLive: LiveData<Boolean> by lazy {
		onlyInstalledFlow.asIOLiveData()
	}

	override fun setLanguageFiltered(language: String, state: Boolean) {
		logI("Language $language updated to state $state")
		launchIO {
			settingsRepo.getStringSet(BrowseFilteredLanguages).handle(
				onError = {
					logE("Failed to retrieve $BrowseFilteredLanguages", it.exception)
				}
			) { set ->
				val mutableSet = set.toMutableSet()

				if (state) {
					mutableSet.removeAll { it == language }
				} else {
					mutableSet.add(language)
				}

				settingsRepo.setStringSet(BrowseFilteredLanguages, mutableSet).handle(
					onError = {
						logE("Failed to update $BrowseFilteredLanguages", it.exception)
					}
				) {
					logV("Done")
				}
			}
		}
	}

	override fun showOnlyInstalled(state: Boolean) {
		logI("Show only installed new state: $state")
		launchIO {
			settingsRepo.setBoolean(SettingKey.BrowseOnlyInstalled, state).handle(
				onError = {
					logE("Failed to update ${SettingKey.BrowseOnlyInstalled}", it.exception)
				}
			) {
				logV("Done")
			}
		}
	}

	override fun setSearch(name: String) {
		searchTermFlow.tryEmit(name)
	}

	override fun resetSearch() {
		searchTermFlow.tryEmit("")
	}

	override val searchTermLive: LiveData<String> by lazy {
		searchTermFlow.asIOLiveData()
	}

	override val liveData: LiveData<HResult<List<ExtensionUI>>> by lazy {
		extensionFlow.transformLatest { result ->
			emitAll(
				settingsRepo.getStringSetFlow(BrowseFilteredLanguages)
					.combine(
						settingsRepo.getBooleanFlow(SettingKey.BrowseOnlyInstalled)
							.combine(searchTermFlow) { onlyInstalled, searchTerm ->
								onlyInstalled to searchTerm
							}) { languagesToFilter, (onlyInstalled, searchTerm) ->
						result.transformToSuccess { list ->
							list
								.asSequence()
								.let { sequence ->
									if (searchTerm.isNotBlank())
										sequence.filter { it.name.contains(searchTerm) }
									else sequence
								}
								.filter { if (onlyInstalled) it.installed else true }
								.filterNot { languagesToFilter.contains(it.lang) }
								.sortedBy { it.name }
								.sortedBy { it.lang }
								.sortedBy { !it.installed }
								.sortedBy { it.updateState() != ExtensionUI.State.UPDATE }
								.toList()
						}
					})
		}.asIOLiveData()
	}

	override fun isOnline(): Boolean = isOnlineUseCase()

}