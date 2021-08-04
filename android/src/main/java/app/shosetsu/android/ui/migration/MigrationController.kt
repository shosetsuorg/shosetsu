package app.shosetsu.android.ui.migration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.AMigrationViewModel
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme

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
class MigrationController(bundle: Bundle) : ShosetsuController(bundle) {
	companion object {
		const val TARGETS_BUNDLE_KEY: String = "targets"
	}

	private val viewModel: AMigrationViewModel by viewModel()

	override fun onViewCreated(view: View) {
		viewModel.setNovels(args.getIntArray(TARGETS_BUNDLE_KEY)!!)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setContent {
			MdcTheme {
				MigrationContent(viewModel)
			}
		}
	}
}


@Composable
fun MigrationContent(viewModel: AMigrationViewModel) {
	val list by viewModel.novels.observeAsState(loading)

	Column(modifier = Modifier.fillMaxSize()) {
		// Novels that the user selected to transfer
		Box(modifier = Modifier.fillMaxWidth()) {
			list.handle(
				onLoading = { MigrationNovelsLoadingContent() }
			) {

			}
		}

		// Holds an arrow indicating it will be transferred to
		Box(modifier = Modifier.fillMaxWidth()) {
			Icon(
				painter = painterResource(id = R.drawable.expand_more),
				contentDescription = "The above will transfer to the below"
			)
		}

		// Select the extension
		Box(modifier = Modifier.fillMaxWidth()) {

		}

		// Select novel from its results
		Box(modifier = Modifier.fillMaxWidth()) {

		}
	}
}

@Composable
fun MigrationNovelsLoadingContent() {
	LinearProgressIndicator()
}

@Composable
fun MigrationNovelsContent(list: List<NovelUI>) {
	LazyRow {
		items(items = list, key = { it.id }) { novelUI ->
			Card {
				Column {
					Text(text = novelUI.imageURL)
				}
			}
		}
	}
}