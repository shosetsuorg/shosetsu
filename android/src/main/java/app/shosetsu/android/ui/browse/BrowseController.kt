package app.shosetsu.android.ui.browse

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

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_EXTENSION
import app.shosetsu.android.common.consts.REPOSITORY_HELP_URL
import app.shosetsu.android.common.ext.displayOfflineSnackBar
import app.shosetsu.android.common.ext.makeSnackBar
import app.shosetsu.android.common.ext.shosetsuPush
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.domain.model.local.BrowseExtensionEntity
import app.shosetsu.android.domain.model.local.ExtensionInstallOptionEntity
import app.shosetsu.android.ui.catalogue.CatalogController
import app.shosetsu.android.ui.extensionsConfigure.ConfigureExtension
import app.shosetsu.android.view.compose.ErrorAction
import app.shosetsu.android.view.compose.ErrorContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.controller.base.syncFABWithCompose
import app.shosetsu.android.viewmodel.abstracted.ABrowseViewModel
import app.shosetsu.lib.Version
import coil.compose.rememberAsyncImagePainter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ComposeViewBinding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class BrowseController : ShosetsuController(),
	ExtendedFABController {
	override val viewTitleRes: Int = R.string.browse

	override fun onViewCreated(view: View) {}

	private var bsg: BottomSheetDialog? = null

	init {
		setHasOptionsMenu(true)
	}

	/***/
	val viewModel: ABrowseViewModel by viewModel()

	private var fab: ExtendedFloatingActionButton? = null

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_browse, menu)
	}

	override fun onPrepareOptionsMenu(menu: Menu) {
		(menu.findItem(R.id.search).actionView as SearchView).apply {
			setOnQueryTextListener(BrowseSearchQuery { router.shosetsuPush(it) })
			isSubmitButtonEnabled = true
		}
	}

	private fun installExtension(
		extension: BrowseExtensionEntity,
		option: ExtensionInstallOptionEntity
	) {
		if (!extension.isInstalled) {
			if (viewModel.isOnline()) {
				viewModel.installExtension(extension, option)
			} else {
				displayOfflineSnackBar(R.string.controller_browse_snackbar_offline_no_install_extension)
			}
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
				val entites by viewModel.liveData.collectAsState(listOf())
				var isRefreshing by remember { mutableStateOf(false) }
				BrowseContent(
					entites,
					refresh = {
						isRefreshing = true
						onRefresh()
						isRefreshing = false
					},
					installExtension = ::installExtension,
					update = viewModel::updateExtension,
					openCatalogue = ::openCatalogue,
					openSettings = ::openSettings,
					cancelInstall = viewModel::cancelInstall,
					isRefreshing = isRefreshing,
					fab
				)
			}
		}
	}

	private fun openSettings(entity: BrowseExtensionEntity) {
		viewModel.resetSearch()
		router.shosetsuPush(ConfigureExtension(bundleOf(BUNDLE_EXTENSION to entity.id)))
	}

	private fun openCatalogue(entity: BrowseExtensionEntity) {
		// First check if the user is online or not
		if (viewModel.isOnline()) {
			// If the extension is installed, push to it, otherwise prompt the user to install
			if (entity.isInstalled) {
				viewModel.resetSearch()
				router.shosetsuPush(
					CatalogController(
						bundleOf(
							BUNDLE_EXTENSION to entity.id
						)
					)
				)
			} else makeSnackBar(R.string.controller_browse_snackbar_not_installed)?.setAction(
				R.string.install
			) {
				// TODO install
			}?.show()
		} else displayOfflineSnackBar(R.string.controller_browse_snackbar_offline_no_extension)

	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		R.id.help -> {
			openHelpMenu()
			true
		}
		R.id.search -> true
		R.id.browse_import -> {
			makeSnackBar(R.string.regret)?.show()
			true
		}
		else -> false
	}

	private fun openHelpMenu() {
		startActivity(Intent(ACTION_VIEW, Uri.parse(REPOSITORY_HELP_URL)))
	}

	private fun onRefresh() {
		if (viewModel.isOnline())
			viewModel.refresh()
		else displayOfflineSnackBar(R.string.controller_browse_snackbar_offline_no_update_extension)
	}

	override fun manipulateFAB(fab: ExtendedFloatingActionButton) {
		this.fab = fab
		fab.setOnClickListener {
			//bottomMenuRetriever.invoke()?.show()
			if (bsg == null)
				bsg = BottomSheetDialog(this.view!!.context)
			if (bsg?.isShowing == false) {
				bsg?.apply {
					val binding = ComposeViewBinding.inflate(
						this@BrowseController.activity!!.layoutInflater,
						null,
						false
					)

					this.window?.decorView?.let {
						ViewTreeLifecycleOwner.set(it, this@BrowseController)
						ViewTreeSavedStateRegistryOwner.set(it, activity as MainActivity)
					}

					binding.root.apply {
						setViewCompositionStrategy(
							ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@BrowseController)
						)
						setContent {
							MdcTheme(view!!.context) {
								BrowseControllerFilterMenu(viewModel)
							}
						}
					}

					setContentView(binding.root)

				}?.show()
			}
		}
		fab.setText(R.string.filter)
		fab.setIconResource(R.drawable.filter)
	}
}

@Preview
@Composable
fun PreviewBrowseContent() {
	BrowseContent(
		entities =
		List(10) {
			BrowseExtensionEntity(
				it,
				"Fake a b c",
				"",
				"en",
				installOptions = null,
				isInstalled = true,
				installedVersion = Version(1, 1, 1),
				installedRepo = 1,
				isUpdateAvailable = false,
				updateVersion = Version(1, 2, 1),
				isInstalling = false
			)
		},
		{},
		{ a, b -> },
		{},
		{},
		{},
		{},
		false,
		fab = null
	)
}

@Composable
fun BrowseContent(
	entities: List<BrowseExtensionEntity>,
	refresh: () -> Unit,
	installExtension: (BrowseExtensionEntity, ExtensionInstallOptionEntity) -> Unit,
	update: (BrowseExtensionEntity) -> Unit,
	openCatalogue: (BrowseExtensionEntity) -> Unit,
	openSettings: (BrowseExtensionEntity) -> Unit,
	cancelInstall: (BrowseExtensionEntity) -> Unit,
	isRefreshing: Boolean,
	fab: ExtendedFloatingActionButton?
) {
	SwipeRefresh(
		state = SwipeRefreshState(isRefreshing), refresh, modifier = Modifier.fillMaxSize()
	) {
		if (entities.isNotEmpty()) {
			val state = rememberLazyListState()
			if (fab != null)
				syncFABWithCompose(state, fab)
			LazyColumn(
				modifier = Modifier.fillMaxSize(),
				contentPadding = PaddingValues(bottom = 198.dp),
				state = state
			) {
				items(entities, key = { it.id }) { entity ->
					BrowseExtensionContent(
						entity,
						install = {
							installExtension(entity, it)
						},
						update = {
							update(entity)
						},
						openCatalogue = {
							openCatalogue(entity)
						},
						openSettings = {
							openSettings(entity)
						},
						cancelInstall = {
							cancelInstall(entity)
						}
					)
				}
			}
		} else {
			ErrorContent(
				R.string.empty_browse_message,
				ErrorAction(R.string.empty_browse_refresh_action) {
					refresh()
				}
			)
		}
	}
}

@Preview
@Composable
fun PreviewBrowseExtensionContent() {
	BrowseExtensionContent(
		BrowseExtensionEntity(
			1,
			"Fake a  aaaaaaaaaaaaaaaaa",
			"",
			"en",
			installOptions = listOf(
				ExtensionInstallOptionEntity(1, "Wowa", Version(1, 1, 1))
			),
			isInstalled = true,
			installedVersion = Version(1, 1, 1),
			installedRepo = 1,
			isUpdateAvailable = true,
			updateVersion = Version(1, 2, 1),
			isInstalling = false
		),
		{},
		{},
		{},
		{},
		{}
	)
}

@OptIn(
	ExperimentalMaterialApi::class,
	androidx.compose.foundation.ExperimentalFoundationApi::class,
	androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi::class,
	androidx.compose.ui.unit.ExperimentalUnitApi::class
)
@Composable
fun BrowseExtensionContent(
	item: BrowseExtensionEntity,
	install: (ExtensionInstallOptionEntity) -> Unit,
	update: () -> Unit,
	openCatalogue: () -> Unit,
	openSettings: () -> Unit,
	cancelInstall: () -> Unit
) {
	Card(
		onClick = openCatalogue,
		modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
		shape = RoundedCornerShape(16.dp)
	) {
		Column {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(end = 8.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
				) {
					Image(
						painter = if (item.imageURL.isNotEmpty()) {
							rememberAsyncImagePainter(item.imageURL)
						} else {
							painterResource(R.drawable.broken_image)
						},
						stringResource(R.string.controller_browse_ext_icon_desc),
						modifier = Modifier.size(64.dp)
					)
					Column(
						modifier = Modifier.padding(start = 8.dp)
					) {
						Text(item.name)
						Row {
							Text(item.lang, fontSize = TextUnit(14f, TextUnitType.Sp))

							if (item.isInstalled && item.installedVersion != null)
								Text(
									item.installedVersion.toString(),
									modifier = Modifier.padding(start = 8.dp),
									fontSize = TextUnit(14f, TextUnitType.Sp)
								)

							if (item.isUpdateAvailable && item.updateVersion != null) {
								if (item.updateVersion != Version(-9, -9, -9))
									Text(
										stringResource(
											R.string.update_to,
											item.updateVersion.toString()
										),
										modifier = Modifier.padding(start = 8.dp),
										fontSize = TextUnit(14f, TextUnitType.Sp),
										color = colorResource(R.color.colorAccent)
									)
							}
						}
					}
				}
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.End
				) {
					if (!item.isInstalled && !item.isInstalling && !item.installOptions.isNullOrEmpty()) {
						var isDropdownVisible by remember { mutableStateOf(false) }
						IconButton(
							onClick = {
								// We can skip to dropdown if there is only 1 install option
								if (item.installOptions.size != 1)
									isDropdownVisible = true
								else install(item.installOptions[0])
							}
						) {
							Icon(painterResource(R.drawable.download), null)
						}
						DropdownMenu(
							expanded = isDropdownVisible,
							onDismissRequest = { isDropdownVisible = false },
						) {
							item.installOptions.forEach { s ->
								DropdownMenuItem(
									onClick = {
										install(s)
										isDropdownVisible = false
									}
								) {
									Row {
										Text(
											text = AnnotatedString(s.repoName)
										)
										Text(
											text = AnnotatedString(s.version.toString()),
											modifier = Modifier.padding(start = 8.dp)
										)
									}
								}
							}
						}
					}

					if (item.isUpdateAvailable) {
						IconButton(
							onClick = update
						) {
							Icon(
								painterResource(R.drawable.download),
								stringResource(R.string.update),
								modifier = Modifier.rotate(180f),
								tint = colorResource(R.color.colorAccent)
							)
						}
					}

					if (item.isInstalled) {
						IconButton(
							onClick = openSettings
						) {
							Icon(
								painterResource(R.drawable.settings),
								stringResource(R.string.settings)
							)
						}
					}

					if (item.isInstalling) {
						IconButton(
							onClick = {},
							modifier = Modifier.combinedClickable(
								onClick = {},
								onLongClick = cancelInstall,
							)
						) {
							val image =
								AnimatedImageVector.animatedVectorResource(R.drawable.animated_refresh)

							Icon(
								rememberAnimatedVectorPainter(image, false),
								stringResource(R.string.installing)
							)
						}
					}
				}

			}

			if (item.isUpdateAvailable && item.updateVersion != null) {
				if (item.updateVersion == Version(-9, -9, -9)) {
					Box(
						modifier = Modifier
							.background(colorResource(R.color.colorAccent))
							.fillMaxWidth()
					) {
						Text(
							stringResource(R.string.obsolete_extension),
							color = colorResource(R.color.design_default_color_on_primary),
							modifier = Modifier
								.padding(8.dp)
								.align(Alignment.Center)
						)
					}
				}
			}
		}
	}
}