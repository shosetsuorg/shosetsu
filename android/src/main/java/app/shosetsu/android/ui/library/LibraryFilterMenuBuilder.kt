package app.shosetsu.android.ui.library

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.view.widget.TriStateButton.State.CHECKED
import app.shosetsu.android.view.widget.TriStateButton.State.UNCHECKED
import app.shosetsu.android.viewmodel.abstracted.ILibraryViewModel
import app.shosetsu.common.enums.NovelSortType.*
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerLibraryBottomMenu0Binding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerLibraryBottomMenu1Binding
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
class LibraryFilterMenuBuilder constructor(
	private val layoutInflater: LayoutInflater,
	private val viewModel: ILibraryViewModel
) {
	fun build(): View =
		ControllerNovelInfoBottomMenuBinding.inflate(
			layoutInflater
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
					val view = ControllerLibraryBottomMenu0Binding.inflate(
						layoutInflater,
						container,
						false
					).also {

					}.root
					container.addView(view)
					return view
				}
				1 -> {
					val view = ControllerLibraryBottomMenu1Binding.inflate(
						layoutInflater,
						container,
						false
					).also {
						val reversed = viewModel.isSortReversed()

						when (viewModel.getSortType()) {
							BY_TITLE -> it.byTitle::state
							BY_UNREAD_COUNT -> it.byUnreadCount::state
							BY_ID -> it.byId::state
						}.set(if (!reversed) CHECKED else UNCHECKED)

						it.triStateGroup.addOnStateChangeListener { id, state ->
							viewModel.setSortType(
								when (id) {
									R.id.by_title -> BY_TITLE
									R.id.by_unread_count -> BY_UNREAD_COUNT
									R.id.by_id -> BY_ID
									else -> BY_TITLE
								}
							)
							viewModel.setIsSortReversed(state != CHECKED)
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