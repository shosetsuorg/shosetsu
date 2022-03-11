package app.shosetsu.android.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.ext.collectLatestLA
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.shosetsuPush
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.compose.ErrorContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.CollapsedToolBarController
import app.shosetsu.android.view.widget.EmptyDataView
import app.shosetsu.android.viewmodel.abstracted.AAddShareViewModel
import app.shosetsu.lib.share.ExtensionLink
import app.shosetsu.lib.share.NovelLink
import app.shosetsu.lib.share.RepositoryLink
import app.shosetsu.lib.share.StyleLink
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import io.github.g00fy2.quickie.content.QRContent
import org.acra.ACRA
import javax.security.auth.DestroyFailedException

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
 * This class represents a combination of a QR code scanner + handler.
 *
 * First, it will read the QR code.
 * Second, it will display to the user what the QR code provides.
 * Third, it allows the user to choose to add the content or not.
 *
 * @since 07 / 03 / 2022
 * @author Doomsdayrs
 */
class AddShareController : ShosetsuController(), CollapsedToolBarController {

	override val viewTitleRes: Int = R.string.qr_code_scan
	lateinit var scanQrCode: ActivityResultLauncher<Nothing?>

	private val viewModel: AAddShareViewModel by viewModel()
	override fun onViewCreated(view: View) {
		viewModel.hasData.collectLatestLA(this, catch = {}) {
			if (!it) scanQrCode.launch(null)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setViewTitle()
		setContent {
			MdcTheme {
				val isProcessing by viewModel.isProcessing.collectAsState(true)
				val isQRCodeValid by viewModel.isQRCodeValid.collectAsState(true)
				val isAdding by viewModel.isAdding.collectAsState(false)
				val isComplete by viewModel.isComplete.collectAsState(false)
				val isNovelOpenable by viewModel.isNovelOpenable.collectAsState(false)

				val isNovelAlreadyPresent by viewModel.isNovelAlreadyPresent.collectAsState(false)
				val isStyleAlreadyPresent by viewModel.isStyleAlreadyPresent.collectAsState(false)
				val isExtAlreadyPresent by viewModel.isExtAlreadyPresent.collectAsState(false)
				val isRepoAlreadyPresent by viewModel.isRepoAlreadyPresent.collectAsState(false)

				val novelLink by viewModel.novelLink.collectAsState(null)
				val extLink by viewModel.extLink.collectAsState(null)
				val repoLink by viewModel.repoLink.collectAsState(null)

				AddShareContent(
					isProcessing = isProcessing,
					isQRCodeValid = isQRCodeValid,
					isAdding = isAdding,
					add = {
						viewModel.add()
					},
					reject = {
						activity?.onBackPressed()
					},
					retry = {
						viewModel.retry()
						scanQrCode.launch(null)
					},
					novelLink = novelLink,
					extensionLink = extLink,
					repositoryLink = repoLink,
					isNovelAlreadyPresent = isNovelAlreadyPresent,
					isStyleAlreadyPresent = isStyleAlreadyPresent,
					isExtAlreadyPresent = isExtAlreadyPresent,
					isRepoAlreadyPresent = isRepoAlreadyPresent,
					isComplete = isComplete,
					openNovel = {
						val entity = viewModel.getNovel()

						activity?.onBackPressed()

						if (entity != null)
							router.shosetsuPush(
								NovelController(
									bundleOf(BundleKeys.BUNDLE_NOVEL_ID to entity.id)
								)
							)
					},
					isNovelOpenable = isNovelOpenable
				)
			}
		}
	}

	private fun handleResult(result: QRResult) {
		when (result) {
			is QRResult.QRSuccess -> {
				when (val content = result.content) {
					is QRContent.Url -> {
						viewModel.takeData(content.url)
					}
					is QRContent.Plain -> {
						viewModel.takeData(content.rawValue)
					}
					else -> {
						viewModel.setInvalidQRCode()
					}
				}
			}
			QRResult.QRUserCanceled -> {
				viewModel.setInvalidQRCode()
			}
			QRResult.QRMissingPermission -> {
				viewModel.setInvalidQRCode()
			}
			is QRResult.QRError -> {
				logE("Failed to scan qr code", result.exception)
				viewModel.setInvalidQRCode()
			}
		}
	}

	override fun onLifecycleCreate(owner: LifecycleOwner, registry: ActivityResultRegistry) {
		super.onLifecycleCreate(owner, registry)
		scanQrCode = registry.register("controller_more_qr_code", ScanQRCode(), ::handleResult)
	}

	override fun onDestroy() {
		try {
			viewModel.destroy()
		} catch (e: DestroyFailedException) {
			logE("Failed to destroy viewmodel", e)
			ACRA.errorReporter.handleSilentException(e)
		}
	}
}

@Preview
@Composable
fun PreviewAboutContent() {
	val repoLink =
		RepositoryLink("Test Repo", "https://raw.githubusercontent.com/shosetsuorg/extensions/dev/")
	val extLink = ExtensionLink(
		0,
		"Test Ext",
		"https://raw.githubusercontent.com/shosetsuorg/extensions/dev/icons/AsianHobbyist.png",
		repoLink
	)
	val novelLink = NovelLink(
		"How to Get My Husband on My Side",
		"https://noveltrench.com/wp-content/uploads/2021/03/How-To-Get-My-Husband-On-My-Side-193x278.jpg",
		"https://noveltrench.com/novel/how-to-get-my-husband-on-my-side/",
		extLink
	)


	MdcTheme {

		AddShareContent(
			isProcessing = false,
			novelLink = novelLink,
			extensionLink = extLink,
			repositoryLink = repoLink,
			add = {
			},
			reject = {
			},
			isAdding = false,
			isQRCodeValid = true,
			retry = {
			},
			isNovelAlreadyPresent = false,
			isStyleAlreadyPresent = true,
			isExtAlreadyPresent = true,
			isRepoAlreadyPresent = true,
			openNovel = {}
		)
	}
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AddShareContent(
	isProcessing: Boolean,
	isQRCodeValid: Boolean = false,
	isAdding: Boolean = false,
	novelLink: NovelLink? = null,
	styleLink: StyleLink? = null,
	extensionLink: ExtensionLink? = null,
	repositoryLink: RepositoryLink? = null,
	add: () -> Unit,
	reject: () -> Unit,
	openNovel: () -> Unit,
	retry: () -> Unit,
	isNovelAlreadyPresent: Boolean = false,
	isStyleAlreadyPresent: Boolean = false,
	isExtAlreadyPresent: Boolean = false,
	isRepoAlreadyPresent: Boolean = false,
	isComplete: Boolean = false,
	isNovelOpenable: Boolean = false,
) {
	if (isComplete) {
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Column(horizontalAlignment = Alignment.CenterHorizontally) {
				Text(stringResource(R.string.completed), style = MaterialTheme.typography.body1)
				TextButton(onClick = reject) {
					Text(stringResource(android.R.string.ok))
				}

				if (isNovelOpenable)
					TextButton(onClick = openNovel) {
						Text(stringResource(R.string.controller_add_open_novel))
					}
			}
		}
	} else if (isProcessing) {
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			CircularProgressIndicator()
		}
	} else if (!isQRCodeValid) {
		ErrorContent(
			R.string.controller_add_invalid,
			EmptyDataView.Action(
				R.string.retry,
			) {
				retry()
			}
		)
	} else if (isNovelAlreadyPresent && novelLink != null) {
		ErrorContent(
			stringResource(R.string.controller_add_present_novel, novelLink.name),
			EmptyDataView.Action(
				android.R.string.ok,
			) {
				reject()
			},
			EmptyDataView.Action(
				R.string.controller_add_open_novel,
			) {
				openNovel()
			},
		)
	} else if (isStyleAlreadyPresent && styleLink != null) {
		ErrorContent(
			stringResource(R.string.controller_add_present_style, styleLink.name),
			EmptyDataView.Action(
				android.R.string.ok,
			) {
				reject()
			}
		)
	} else if (isExtAlreadyPresent && extensionLink != null && novelLink == null && styleLink == null) {
		ErrorContent(
			stringResource(R.string.controller_add_present_ext, extensionLink.name),
			EmptyDataView.Action(
				android.R.string.ok,
			) {
				reject()
			}
		)
	} else if (isRepoAlreadyPresent && repositoryLink != null && novelLink == null && styleLink == null && extensionLink == null) {
		ErrorContent(
			stringResource(R.string.controller_add_present_repo, repositoryLink.name),
			EmptyDataView.Action(
				android.R.string.ok,
			) {
				reject()
			}
		)
	} else {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(bottom = 56.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				stringResource(R.string.controller_add_following),
				modifier = Modifier.padding(bottom = 16.dp, top = 16.dp),
				style = MaterialTheme.typography.h5
			)

			LazyColumn(
				horizontalAlignment = Alignment.CenterHorizontally,
				contentPadding = PaddingValues(16.dp),
				modifier = Modifier.fillMaxHeight(.75f)
			) {
				if (novelLink != null) {
					item {
						Column {
							Text(
								stringResource(R.string.controller_add_novel),
								style = MaterialTheme.typography.h6,
								modifier = Modifier.padding(bottom = 8.dp)
							)

							Card(
								modifier = Modifier
									.fillMaxWidth()
									.padding(bottom = 16.dp)
							) {
								Column(
									modifier = Modifier.padding(16.dp)
								) {

									Row(
										verticalAlignment = Alignment.CenterVertically
									) {

										AsyncImage(
											model = ImageRequest.Builder(LocalContext.current)
												.data(novelLink.imageURL)
												.error(R.drawable.broken_image)
												.placeholder(R.drawable.animated_refresh)
												.build(),
											contentDescription = null,
											modifier = Modifier
												.heightIn(max = 128.dp)
												.aspectRatio(.75f)
										)
										Column(
											modifier = Modifier.padding(start = 8.dp)
										) {
											Text(
												novelLink.name,
												style = MaterialTheme.typography.body1
											)
											Text(
												novelLink.url,
												style = MaterialTheme.typography.caption
											)
										}
									}
								}
							}
						}
					}
				}
				//if (styleLink != null) {
				//	// TODO Style
				//}
				if (extensionLink != null && !isExtAlreadyPresent) {
					item {
						Column {
							Text(
								stringResource(R.string.controller_add_extension),
								style = MaterialTheme.typography.h6,
								modifier = Modifier.padding(bottom = 8.dp)
							)
							Card(
								modifier = Modifier
									.padding(bottom = 16.dp)
									.fillMaxWidth()
							) {
								Column(
									modifier = Modifier.padding(16.dp),
								) {

									Row(
										verticalAlignment = Alignment.CenterVertically
									) {
										AsyncImage(
											ImageRequest.Builder(LocalContext.current)
												.data(extensionLink.imageURL)
												.placeholder(R.drawable.animated_refresh)
												.error(R.drawable.broken_image)
												.build(),
											"",
											modifier = Modifier.size(64.dp)
										)
										Text(
											extensionLink.name,
											style = MaterialTheme.typography.body1
										)
									}
								}
							}
						}
					}
				}
				if (repositoryLink != null && !isRepoAlreadyPresent) {
					item {
						Column {
							Text(
								stringResource(
									R.string.controller_add_repository,
								),
								style = MaterialTheme.typography.h6,
								modifier = Modifier.padding(bottom = 8.dp)
							)
							Card(
								modifier = Modifier.fillMaxWidth()
							) {
								Column(
									modifier = Modifier.padding(16.dp)
								) {

									Text(
										repositoryLink.name,
										style = MaterialTheme.typography.body1
									)
									Text(
										repositoryLink.url,
										style = MaterialTheme.typography.caption,

										)
								}
							}
						}
					}
				}
			}

			if (isAdding)
				CircularProgressIndicator()

			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			) {
				Row(
					horizontalArrangement = Arrangement.SpaceEvenly,
					modifier = Modifier.fillMaxWidth()
				) {
					TextButton(reject, contentPadding = PaddingValues(16.dp)) {
						Text(stringResource(android.R.string.cancel))
					}

					if (!isAdding)
						TextButton(add, contentPadding = PaddingValues(16.dp)) {
							Text(stringResource(android.R.string.ok))
						}
				}
			}
		}
	}
}
