package app.shosetsu.android.ui.novel.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.viewpager.widget.PagerAdapter
import app.shosetsu.android.ui.novel.filter.NovelFilterMenu.Pages.FILTER
import app.shosetsu.android.ui.novel.filter.NovelFilterMenu.Pages.SORT
import app.shosetsu.android.viewmodel.abstracted.INovelViewModel
import com.github.doomsdayrs.apps.shosetsu.databinding.NovelChaptersFilterMenu0Binding
import com.github.doomsdayrs.apps.shosetsu.databinding.NovelChaptersFilterMenu1Binding
import com.github.doomsdayrs.apps.shosetsu.databinding.NovelChaptersFilterMenuBinding

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
 * 22 / 11 / 2020
 */
class NovelFilterMenu(val context: Context, val root: ViewGroup) {
	var viewModel: INovelViewModel? = null
	internal val layoutInflater = context.getSystemService<LayoutInflater>()!!

	private enum class Pages { FILTER, SORT }


	init {
		NovelChaptersFilterMenuBinding.inflate(layoutInflater).apply {
			root.apply {

			}
		}
	}

	inner class MenuAdapter : PagerAdapter() {
		private val pages = HashMap<Pages, View>()

		override fun getCount(): Int = 2

		override fun isViewFromObject(view: View, obj: Any): Boolean {
			if (obj !is Pages && pages.contains(obj)) return pages[obj] == view
			return false
		}

		override fun instantiateItem(container: ViewGroup, position: Int): Any {
			when (position) {
				0 -> {
					pages[FILTER] = NovelChaptersFilterMenu0Binding.inflate(
							layoutInflater,
							container,
							false
					).also {
						it.bookmarked.setOnCheckedChangeListener { buttonView, isChecked ->

						}

						it.downloaded.setOnCheckedChangeListener { buttonView, isChecked ->

						}

						it.read.setOnCheckedChangeListener { buttonView, isChecked ->

						}

						it.unread.setOnCheckedChangeListener { buttonView, isChecked ->

						}
					}.root
					return FILTER
				}
				1 -> {
					pages[SORT] = NovelChaptersFilterMenu1Binding.inflate(
							layoutInflater,
							container,
							false
					).also {
					}.root
					return SORT
				}
			}
			return super.instantiateItem(container, position)
		}

		override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
			container.removeViewAt(position)
			when (position) {
				0 -> pages.remove(FILTER)
				1 -> pages.remove(SORT)
			}
		}
	}
}