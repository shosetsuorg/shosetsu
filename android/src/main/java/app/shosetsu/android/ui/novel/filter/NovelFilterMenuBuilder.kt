package app.shosetsu.android.ui.novel.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import app.shosetsu.android.view.widget.TriStateButton.State.CHECKED
import app.shosetsu.android.view.widget.TriStateButton.State.UNCHECKED
import app.shosetsu.android.viewmodel.abstracted.INovelViewModel
import app.shosetsu.common.enums.ChapterSortType.SOURCE
import app.shosetsu.common.enums.ChapterSortType.UPLOAD
import app.shosetsu.common.enums.ReadingStatus.READ
import app.shosetsu.common.enums.ReadingStatus.UNREAD
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBottomMenu0Binding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBottomMenu1Binding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBottomMenuBinding

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
 *
 * Creates the bottom menu for Novel Controller
 */
class NovelFilterMenuBuilder constructor(
	private val inflater: LayoutInflater,
	private val viewModel: INovelViewModel
) {
	fun build(): View =
		ControllerNovelInfoBottomMenuBinding.inflate(
			inflater
		).also { binding ->
			binding.viewPager.apply {
				this.adapter = MenuAdapter(binding.root.context)
			}
		}.root

	inner class MenuAdapter(
		private val context: Context
	) : PagerAdapter() {
		override fun getCount(): Int = 2
		override fun getPageTitle(position: Int): CharSequence? = when (position) {
			0 -> context.getString(R.string.filter)
			1 -> context.getString(R.string.sort)
			else -> null
		}

		override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

		override fun instantiateItem(container: ViewGroup, position: Int): Any {
			when (position) {
				0 -> {
					val view = ControllerNovelInfoBottomMenu0Binding.inflate(
						inflater,
						container,
						false
					).also {
						it.bookmarked.isChecked = viewModel.showOnlyBookmarkedChapters()

						it.bookmarked.setOnCheckedChangeListener { _, _ ->
							viewModel.toggleOnlyBookmarked()
						}

						it.downloaded.isChecked = viewModel.showOnlyDownloadedChapters()

						it.downloaded.setOnCheckedChangeListener { _, _ ->
							viewModel.toggleOnlyDownloaded()
						}

						when (viewModel.getSortReadingStatusOf()) {
							UNREAD -> it.unread.isChecked = true
							READ -> it.read.isChecked = true
							else -> it.all.isChecked = true
						}

						it.radioGroup.setOnCheckedChangeListener { group, checkedId ->
							when (checkedId) {
								R.id.all -> viewModel.showOnlyStatus(null)

								R.id.read -> viewModel.showOnlyStatus(READ)

								R.id.unread -> viewModel.showOnlyStatus(UNREAD)

							}
						}

					}.root
					container.addView(view)
					return view
				}
				1 -> {
					val view = ControllerNovelInfoBottomMenu1Binding.inflate(
						inflater,
						container,
						false
					).also {
						val reversed = viewModel.isReversedSortOrder()
						when (viewModel.getSortType()) {
							SOURCE -> it.bySource::state
							UPLOAD -> it.byDate::state
						}.set(if (!reversed) CHECKED else UNCHECKED)

						it.triStateGroup.addOnStateChangeListener { id, state ->
							viewModel.setChapterSortType(
								when (id) {
									R.id.by_date -> UPLOAD
									R.id.by_source -> SOURCE
									else -> UPLOAD
								}
							)
							viewModel.setReverse(state != CHECKED)
						}
					}.root
					container.addView(view)
					return view
				}
			}
			return super.instantiateItem(container, position)
		}

		override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
			(obj as? View)?.let {
				container.removeView(it)
			}
		}
	}
}