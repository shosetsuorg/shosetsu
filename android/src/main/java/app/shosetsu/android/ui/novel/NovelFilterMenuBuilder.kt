package app.shosetsu.android.ui.novel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager.widget.PagerAdapter
import app.shosetsu.android.view.widget.TriStateButton.State.CHECKED
import app.shosetsu.android.view.widget.TriStateButton.State.UNCHECKED
import app.shosetsu.android.viewmodel.abstracted.INovelViewModel
import app.shosetsu.common.dto.handle
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
class NovelFilterMenuBuilder(
	private val novelControllerLifeCycle: LifecycleOwner,
	private val inflater: LayoutInflater,
	private val viewModel: INovelViewModel
) {
	fun build(): View =
		ControllerNovelInfoBottomMenuBinding.inflate(
			inflater
		).also { binding ->
			binding.viewPager.apply {
				val menAda = MenuAdapter(binding.root.context)
				this.adapter = menAda
				var isInitalSetup = true
				viewModel.novelSettingFlow.observe(novelControllerLifeCycle) { result ->
					result.handle { settings ->
						if (isInitalSetup) {
							isInitalSetup = false
							menAda.controllerNovelInfoBottomMenu0Binding?.apply {
								bookmarked.isChecked = settings.showOnlyBookmarked
								downloaded.isChecked = settings.showOnlyDownloaded
								when (settings.showOnlyReadingStatusOf) {
									UNREAD -> unreadRadioButton.isChecked = true
									READ -> readRadioButton.isChecked = true
									else -> allRadioButton.isChecked = true
								}
							}
							menAda.controllerNovelInfoBottomMenu1Binding?.apply {
								val reversed = settings.reverseOrder

								when (settings.sortType) {
									SOURCE -> bySource::state
									UPLOAD -> byDate::state
								}.set(if (!reversed) CHECKED else UNCHECKED)
							}
						}
						menAda.controllerNovelInfoBottomMenu0Binding?.apply {
							bookmarked.setOnCheckedChangeListener { _, state ->
								viewModel.updateNovelSetting(
									settings.copy(
										showOnlyBookmarked = state
									)
								)
							}

							downloaded.setOnCheckedChangeListener { _, state ->
								viewModel.updateNovelSetting(
									settings.copy(
										showOnlyDownloaded = state
									)
								)
							}

							radioGroup.setOnCheckedChangeListener { _, checkedId ->
								when (checkedId) {
									R.id.all_radio_button -> viewModel.updateNovelSetting(
										settings.copy(
											showOnlyReadingStatusOf = null
										)
									)

									R.id.read_radio_button -> viewModel.updateNovelSetting(
										settings.copy(
											showOnlyReadingStatusOf = READ
										)
									)

									R.id.unread_radio_button -> viewModel.updateNovelSetting(
										settings.copy(
											showOnlyReadingStatusOf = UNREAD
										)
									)

								}
							}

						}
						menAda.controllerNovelInfoBottomMenu1Binding?.apply {
							triStateGroup.addOnStateChangeListener { id, state ->
								viewModel.updateNovelSetting(
									settings.copy(
										sortType = when (id) {
											R.id.by_date -> UPLOAD
											R.id.by_source -> SOURCE
											else -> UPLOAD
										},
										reverseOrder = state != CHECKED
									)
								)
							}
						}

					}
				}

			}

		}.root

	inner class MenuAdapter(
		private val context: Context
	) : PagerAdapter() {
		var controllerNovelInfoBottomMenu0Binding: ControllerNovelInfoBottomMenu0Binding? = null
		var controllerNovelInfoBottomMenu1Binding: ControllerNovelInfoBottomMenu1Binding? = null

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
					).root
					container.addView(view)
					return view
				}
				1 -> {
					val view = ControllerNovelInfoBottomMenu1Binding.inflate(
						inflater,
						container,
						false
					).also {
						controllerNovelInfoBottomMenu1Binding = it


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