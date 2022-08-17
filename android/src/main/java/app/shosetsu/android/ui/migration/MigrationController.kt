package app.shosetsu.android.ui.migration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.shosetsu.android.R
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.compose.ImageLoadingError
import app.shosetsu.android.view.compose.ShosetsuCompose
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.uimodels.model.MigrationExtensionUI
import app.shosetsu.android.view.uimodels.model.MigrationNovelUI
import app.shosetsu.android.viewmodel.abstracted.AMigrationViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.material.placeholder

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
 * ====================================================================
 */


/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 * yes, a THIRD ONE
 */
class MigrationController : ShosetsuController() {
	companion object {
		const val TARGETS_BUNDLE_KEY: String = "targets"
	}

	private val viewModel: AMigrationViewModel by viewModel()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		viewModel.setNovels(arguments!!.getIntArray(TARGETS_BUNDLE_KEY)!!)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedViewState: Bundle?
	): View = ComposeView(requireContext()).apply {
		setContent {
			ShosetsuCompose {
				MigrationContent(viewModel)
			}
		}
	}
}

@Composable
fun MigrationContent(viewModel: AMigrationViewModel) {
	val novelList by viewModel.novels.collectAsState(emptyList())
	val extensionsToSelect by viewModel.extensions.collectAsState(initial = emptyList())
	val currentQuery by viewModel.currentQuery.collectAsState(null)

	Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
		// Novels that the user selected to transfer
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight(.25f)
		) {
			// TODO Loading via loading flow
			// MigrationNovelsLoadingContent()
			MigrationNovelsContent(list = novelList) {
				viewModel.setWorkingOn(it.id)
			}
		}


		Text(text = "With name")

		if (currentQuery != null) {
			TextField(value = currentQuery!!, onValueChange = viewModel::setQuery)
		}

		Text(text = "In")

		// Select the extension
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight(.25f)
		) {
			// TODO Loading via loading flow
			// MigrationExtensionsLoadingContent()
			MigrationExtensionsContent(
				list = extensionsToSelect,
				onClick = viewModel::setSelectedExtension
			)
		}

		// Holds an arrow indicating it will be transferred to
		Text(text = "To")

		Icon(
			painter = painterResource(id = R.drawable.expand_more),
			contentDescription = "The above will transfer to the below"
		)


		// Select novel from its results
		Box(modifier = Modifier.fillMaxWidth()) {
			Text(text = "This is under construction, Try again in another release :D")
		}
	}
}

@Composable
fun MigrationExtensionsLoadingContent() {
	LinearProgressIndicator()
}

@Composable
fun MigrationExtensionsContent(
	list: List<MigrationExtensionUI>,
	onClick: (MigrationExtensionUI) -> Unit
) {
	LazyRow(
		horizontalArrangement = Arrangement.Center,
		modifier = Modifier.fillMaxWidth()
	) {
		items(items = list, key = { it.id }) { extensionUI ->
			MigrationExtensionItemContent(extensionUI, onClick = onClick)
		}
	}
}

@ExperimentalMaterialApi
@Preview
@Composable
fun PreviewMigrationExtensionItemContent() {
	val item by remember {
		mutableStateOf(
			MigrationExtensionUI(
				0,
				"This is a novel",
				"",
				false
			)
		)
	}
	ShosetsuCompose {
		Box(modifier = Modifier.height(200.dp)) {
			MigrationExtensionItemContent(item = item) {
				println("Test")
			}
		}
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MigrationExtensionItemContent(
	item: MigrationExtensionUI,
	onClick: (MigrationExtensionUI) -> Unit
) {
	Card(
		modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
		shape = RoundedCornerShape(16.dp),
		border =
		if (item.isSelected) {
			BorderStroke(2.dp, MaterialTheme.colors.primary)
		} else {
			null
		},

		onClick = { onClick(item) },
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
		) {
			if (item.imageURL.isNotEmpty()) {
				SubcomposeAsyncImage(
					ImageRequest.Builder(LocalContext.current)
						.data(item.imageURL)
						.crossfade(true)
						.build(),
					contentDescription = null,
					modifier = Modifier.size(64.dp),
					error = {
						ImageLoadingError()
					},
					loading = {
						Box(Modifier.placeholder(true))
					}
				)
			} else {
				ImageLoadingError(Modifier.size(64.dp))
			}
			Text(
				text = item.name,
				modifier = Modifier.padding(end = 16.dp)
			)
		}
	}
}

@Composable
fun MigrationNovelsLoadingContent() {
	LinearProgressIndicator()
}

@Composable
fun MigrationNovelsContent(list: List<MigrationNovelUI>, onClick: (MigrationNovelUI) -> Unit) {
	LazyRow(
		horizontalArrangement = Arrangement.Center,
		modifier = Modifier.fillMaxWidth()
	) {
		items(items = list, key = { it.id }) { novelUI ->
			MigrationNovelItemContent(item = novelUI, onClick = onClick)
		}
	}
}

@ExperimentalMaterialApi
@Composable
@Preview
fun PreviewMigrationNovelItemRowContent() {
	val item by remember {
		mutableStateOf(
			MigrationNovelUI(
				0,
				"This is a novel",
				"",
				false
			)
		)
	}
	ShosetsuCompose {
		Row(
			modifier = Modifier
				.height(200.dp)
				.width(600.dp)
		) {
			MigrationNovelItemContent(item = item) {
				println("Test")
			}
			MigrationNovelItemContent(item = item) {
				println("Test")
			}
		}
	}
}

@ExperimentalMaterialApi
@Composable
@Preview
fun PreviewMigrationNovelItemContent() {
	val item by remember {
		mutableStateOf(
			MigrationNovelUI(
				0,
				"This is a novel",
				"",
				false
			)
		)
	}
	ShosetsuCompose {
		Box(modifier = Modifier.height(200.dp)) {
			MigrationNovelItemContent(item = item) {
				println("Test")
			}
		}
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MigrationNovelItemContent(item: MigrationNovelUI, onClick: (MigrationNovelUI) -> Unit) {
	Card(
		onClick = { onClick(item) },
		border =
		if (item.isSelected) {
			BorderStroke(2.dp, MaterialTheme.colors.primary)
		} else {
			null
		},
		modifier = Modifier.aspectRatio(.70f)
	) {
		val blackTrans = colorResource(id = R.color.black_trans)
		Box {
			val modifier = Modifier.fillMaxSize()
				.drawWithContent {
					drawContent()
					drawRect(
						Brush.verticalGradient(
							colors = listOf(
								Color.Transparent,
								blackTrans
							),
						)
					)
				}
			if (item.imageURL.isNotEmpty()) {
				SubcomposeAsyncImage(
					ImageRequest.Builder(LocalContext.current)
						.data(item.imageURL)
						.crossfade(true)
						.build(),
					contentDescription = null,
					modifier = modifier,
					error = {
						ImageLoadingError()
					},
					loading = {
						Box(Modifier.placeholder(true))
					}
				)
			} else {
				ImageLoadingError(modifier)
			}

			Text(
				text = item.title,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(8.dp),
				fontWeight = FontWeight.Bold,
				textAlign = TextAlign.Center
			)
		}
	}
}