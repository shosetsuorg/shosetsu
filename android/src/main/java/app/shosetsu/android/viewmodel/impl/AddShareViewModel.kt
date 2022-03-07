package app.shosetsu.android.viewmodel.impl

import androidx.work.WorkInfo
import app.shosetsu.android.backend.workers.onetime.ExtensionInstallWorker
import app.shosetsu.android.backend.workers.onetime.RepositoryUpdateWorker
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.usecases.RequestInstallExtensionUseCase
import app.shosetsu.android.domain.usecases.StartRepositoryUpdateManagerUseCase
import app.shosetsu.android.domain.usecases.get.GetURLUseCase
import app.shosetsu.android.viewmodel.abstracted.AAddShareViewModel
import app.shosetsu.common.GenericSQLiteException
import app.shosetsu.common.domain.model.local.InstalledExtensionEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.repositories.base.IExtensionRepoRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.domain.repositories.base.INovelsRepository
import app.shosetsu.lib.share.ExtensionLink
import app.shosetsu.lib.share.NovelLink
import app.shosetsu.lib.share.RepositoryLink
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

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
 * Shosetsu
 *
 * @since 07 / 03 / 2022
 * @author Doomsdayrs
 */
class AddShareViewModel(
	private val extRepo: IExtensionsRepository,
	private val repoRepo: IExtensionRepoRepository,
	private val novelRepo: INovelsRepository,
	private val getContentURL: GetURLUseCase,
	private val installExtension: RequestInstallExtensionUseCase,
	private val startRepositoryUpdateWorker: StartRepositoryUpdateManagerUseCase,
	private val installManager: ExtensionInstallWorker.Manager,
	private val updateManager: RepositoryUpdateWorker.Manager

) : AAddShareViewModel() {
	override val isAdding: MutableStateFlow<Boolean> = MutableStateFlow(false)
	override val isComplete: MutableStateFlow<Boolean> = MutableStateFlow(false)

	override val isNovelAlreadyPresent: MutableStateFlow<Boolean> = MutableStateFlow(false)
	override val isStyleAlreadyPresent: MutableStateFlow<Boolean> = MutableStateFlow(false)
	override val isExtAlreadyPresent: MutableStateFlow<Boolean> = MutableStateFlow(false)
	override val isRepoAlreadyPresent: MutableStateFlow<Boolean> = MutableStateFlow(false)

	override val isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(true)
	override val isQRCodeValid: MutableStateFlow<Boolean> = MutableStateFlow(false)

	override val extLink: MutableStateFlow<ExtensionLink?> = MutableStateFlow(null)
	override val novelLink: MutableStateFlow<NovelLink?> = MutableStateFlow(null)
	override val repoLink: MutableStateFlow<RepositoryLink?> = MutableStateFlow(null)

	private var data: MutableStateFlow<String?> = MutableStateFlow(null)

	override val exception: MutableStateFlow<Exception?> = MutableStateFlow(null)

	override val hasData: Flow<Boolean> =
		data.map { it != null }

	init {
		launchIO {
			data.collectLatest { url ->
				if (url == null) return@collectLatest

				isProcessing.tryEmit(true)

				suspend fun invalidate() {
					isProcessing.emit(false)
					isQRCodeValid.emit(false)
				}

				val http = url.toHttpUrlOrNull()

				if (http != null) {
					val linkType = http.pathSegments.firstOrNull()

					if (linkType != null) {
						when (linkType) {
							"novel" -> {
								val names = http.queryParameterNames

								if (!names.containsAll(
										listOf(
											"name",
											"url",
											"imageURL",
											"extID",
											"extURL",
											"extName",
											"repoName",
											"repoURL"
										)
									)
								) {
									invalidate()
									return@collectLatest
								}


								val repo = RepositoryLink(
									http.queryParameter("repoName")!!,
									http.queryParameter("repoURL")!!.toHttpUrl().toUri().normalize()
										.toString()
								)

								val ext = ExtensionLink(
									http.queryParameter("extID")!!.toInt(),
									http.queryParameter("extName")!!,
									http.queryParameter("extURL")!!.toHttpUrl().toUri().normalize()
										.toString(),
									repo
								)
								val novel = NovelLink(
									http.queryParameter("name")!!,
									http.queryParameter("imageURL")!!.toHttpUrl().toUri()
										.normalize().toString(),
									http.queryParameter("url")!!.toHttpUrl().toUri().normalize()
										.toString(),
									ext
								)

								val repoEntity: RepositoryEntity? = try {
									repoRepo.loadRepositories().find {
										val entityUrl = it.url.toHttpUrl().toUri().normalize()
										val repoLinkUrl = repo.url.toHttpUrl().toUri()

										logV(entityUrl.toString())
										logV(entityUrl.toString())

										entityUrl == repoLinkUrl
									}
								} catch (e: GenericSQLiteException) {
									null
								}

								val extEntity: InstalledExtensionEntity? = try {
									extRepo.getInstalledExtension(ext.id)
								} catch (e: GenericSQLiteException) {
									null
								}

								val novelEntity: NovelEntity? =
									novelRepo.loadNovels().find {
										val contentUrl =
											getContentURL(it)?.toHttpUrl()?.toUri()?.normalize()
										val novelLinkUrl = novel.url.toHttpUrl().toUri().normalize()

										logV(contentUrl.toString())
										logV(novelLinkUrl.toString())

										contentUrl == novelLinkUrl
									}?.takeIf { it.bookmarked }

								repoLink.emit(repo)
								extLink.emit(ext)
								novelLink.emit(novel)

								if (repoEntity != null)
									isRepoAlreadyPresent.emit(true)

								if (extEntity != null)
									isExtAlreadyPresent.emit(true)

								if (novelEntity != null)
									isNovelAlreadyPresent.emit(true)

								isProcessing.emit(false)
								isQRCodeValid.emit(true)
							}
							"repository" -> {
								invalidate()
							}
							"extension" -> {
								invalidate()
							}
							"style" -> {
								invalidate()
							}
						}
					} else {
						invalidate()
					}
				} else {
					invalidate()
				}
			}
		}
	}

	override fun takeData(url: String) {
		logV(url)
		data.tryEmit(url)
	}

	override fun setInvalidQRCode() {
		isQRCodeValid.tryEmit(false)
		isProcessing.tryEmit(false)
	}

	override fun add() {
		launchIO {
			isAdding.emit(true)

			// Add repository if not present
			if (!isRepoAlreadyPresent.value) {
				val link = repoLink.value!!
				try {
					repoRepo.addRepository(link.url, link.name)
				} catch (e: GenericSQLiteException) {
					exception.emit(e)
					return@launchIO
				}
				startRepositoryUpdateWorker()

				delay(100)

				while (updateManager.getWorkerState().let {
						it == WorkInfo.State.ENQUEUED || it == WorkInfo.State.RUNNING || it == WorkInfo.State.BLOCKED
					}) {
					logI("Waiting for repo to finish updating")
					delay(200)
				}
			}

			// Add ext if not present
			if (!isExtAlreadyPresent.value) {
				val link = extLink.value!!

				val repo = try {
					repoRepo.loadRepositories().first { it.url == link.repo.url }
				} catch (e: GenericSQLiteException) {
					exception.emit(e)
					return@launchIO
				} catch (e: NoSuchElementException) {
					exception.emit(e)
					return@launchIO
				}



				installExtension(link.id, repo.id)

				delay(100)

				while (installManager.getWorkerState().let {
						it == WorkInfo.State.ENQUEUED || it == WorkInfo.State.RUNNING || it == WorkInfo.State.BLOCKED
					}) {
					logI("Waiting for extension to install")
					delay(200)
				}
			}

			// Add style if not present
			if (!isStyleAlreadyPresent.value) {
				// TODO Style
			}

			// Add novel if not present
			if (!isNovelAlreadyPresent.value) {
				val link = novelLink.value!!

				val novel = novelRepo.loadNovels().find {
					getContentURL(it)?.toHttpUrl()?.toUri()?.normalize() ==
							link.url.toHttpUrl().toUri().normalize()
				}

				try {
					if (novel == null) {
						novelRepo.insert(
							NovelEntity(
								url = link.url,
								imageURL = link.imageURL,
								title = link.name,
								extensionID = link.extensionQRCode.id,
								bookmarked = true
							)
						)

					} else {
						novelRepo.update(novel.copy(bookmarked = true))
					}
				} catch (e: GenericSQLiteException) {
					exception.emit(e)
					return@launchIO
				}
			}

			isAdding.emit(false)
			isComplete.emit(true)
		}
	}

	override fun destroy() {
		launchIO {
			isAdding.emit(false)
			isComplete.emit(false)

			isNovelAlreadyPresent.emit(false)
			isStyleAlreadyPresent.emit(false)
			isExtAlreadyPresent.emit(false)
			isRepoAlreadyPresent.emit(false)

			isProcessing.emit(true)
			isQRCodeValid.emit(false)

			extLink.emit(null)
			novelLink.emit(null)
			repoLink.emit(null)
			data.emit(null)

			exception.emit(null)
		}
	}

	override fun retry() {
		isAdding.tryEmit(false)
		isProcessing.tryEmit(true)
		isQRCodeValid.tryEmit(false)
	}

}