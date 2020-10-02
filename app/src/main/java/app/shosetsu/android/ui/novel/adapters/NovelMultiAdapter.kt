package app.shosetsu.android.ui.novel.adapters

import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.launchUI
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.INovelViewModel
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

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
 * shosetsu
 * 25 / 09 / 2020
 */
class NovelMultiAdapter(private val viewModel: INovelViewModel) : FastAdapter<AbstractItem<*>>() {
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		launchIO {
			when (val item = getItem(position)) {
				is NovelUI -> launchUI { (holder as NovelUI.ViewHolder).bind(item) }
				is ChapterUI -> launchUI { (holder as ChapterUI.ViewHolder).bind(item) }
			}
		}
	}

	private fun NovelUI.ViewHolder.bind(novelUI: NovelUI) {
	}

	private fun ChapterUI.ViewHolder.bind(chapterUI: ChapterUI) {
	}
}