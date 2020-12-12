package app.shosetsu.android.ui.novel.filter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.FrameLayout
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import app.shosetsu.android.view.widget.SimpleAnimationListener
import app.shosetsu.android.view.widget.TriStateButton.State
import app.shosetsu.android.view.widget.TriStateButton.State.CHECKED
import app.shosetsu.android.view.widget.TriStateButton.State.UNCHECKED
import app.shosetsu.android.viewmodel.abstracted.INovelViewModel
import app.shosetsu.common.enums.ChapterSortType.SOURCE
import app.shosetsu.common.enums.ChapterSortType.UPLOAD
import app.shosetsu.common.enums.ReadingStatus.READ
import app.shosetsu.common.enums.ReadingStatus.UNREAD
import com.github.doomsdayrs.apps.shosetsu.R
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
class NovelFilterMenu @JvmOverloads constructor(
		context: Context,
		attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
	var viewModel: INovelViewModel? = null
	var onShowListener: () -> Unit = {}
	var onHideListener: () -> Unit = {}

	internal val layoutInflater = context.getSystemService<LayoutInflater>()!!

	private val binding by lazy {
		NovelChaptersFilterMenuBinding.inflate(
				LayoutInflater.from(context),
				this,
				true
		).also { binding ->
			binding.viewPager.apply {
				this.adapter = MenuAdapter()
			}
			binding.background.setOnClickListener {
				hide()
			}
		}
	}

	fun show() {
		onShowListener()
		binding.root.isVisible = true

		binding.root.startAnimation(loadAnimation(context, R.anim.bottom_slide_up).apply {
			duration = 300
			interpolator = AccelerateDecelerateInterpolator()
			setAnimationListener(object : SimpleAnimationListener() {
				override fun onAnimationStart(animation: Animation) {
					binding.background.isVisible = false
					binding.cardView.isVisible = true
					binding.root.isVisible = true
				}

				override fun onAnimationEnd(animation: Animation) {
					binding.background.startAnimation(loadAnimation(context, R.anim.fade_in).apply {
						duration = 100
						setAnimationListener(object : SimpleAnimationListener() {
							override fun onAnimationEnd(animation: Animation) {
								binding.background.isVisible = true
							}
						})
					})
				}
			})
		})

	}

	fun hide() {
		binding.background.startAnimation(loadAnimation(context, R.anim.fade_out).apply {
			duration = 50
			setAnimationListener(object : SimpleAnimationListener() {
				override fun onAnimationEnd(animation: Animation) {
					binding.background.isVisible = false

					binding.cardView.startAnimation(loadAnimation(context, R.anim.bottom_slide_down).apply {
						duration = 300
						interpolator = AccelerateDecelerateInterpolator()
						setAnimationListener(object : SimpleAnimationListener() {
							override fun onAnimationStart(animation: Animation) {

							}

							override fun onAnimationEnd(animation: Animation) {
								binding.cardView.isVisible = false
								binding.root.isVisible = false
								onHideListener()
							}
						})
					})
				}
			})
		})


	}

	inner class MenuAdapter : PagerAdapter() {
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
					val view = NovelChaptersFilterMenu0Binding.inflate(
							layoutInflater,
							container,
							false
					).also {
						it.bookmarked.isChecked = viewModel!!.showOnlyBookmarkedChapters()

						it.bookmarked.setOnCheckedChangeListener { buttonView, isChecked ->
							viewModel?.toggleOnlyBookmarked()
						}

						it.downloaded.isChecked = viewModel!!.showOnlyDownloadedChapters()

						it.downloaded.setOnCheckedChangeListener { buttonView, isChecked ->
							viewModel?.toggleOnlyDownloaded()
						}

						when (viewModel!!.getSortReadingStatusOf()) {
							UNREAD -> it.unread.isChecked = true
							READ -> it.read.isChecked = true
							else -> it.all.isChecked = true
						}

						it.radioGroup.setOnCheckedChangeListener { group, checkedId ->
							when (checkedId) {
								R.id.all -> viewModel?.showOnlyStatus(null)

								R.id.read -> viewModel?.showOnlyStatus(READ)

								R.id.unread -> viewModel?.showOnlyStatus(UNREAD)

							}
						}

					}.root
					container.addView(view)
					return view
				}
				1 -> {
					val view = NovelChaptersFilterMenu1Binding.inflate(
							layoutInflater,
							container,
							false
					).also {
						val reversed = viewModel!!.isReversedSortOrder()
						when (viewModel!!.getSortType()) {
							SOURCE -> it.bySource.state = if (!reversed) CHECKED else UNCHECKED
							UPLOAD -> it.byDate.state = if (!reversed) CHECKED else UNCHECKED
						}
						it.triStateGroup.addOnStateChangeListener { id, state ->
							when (id) {
								R.id.by_date -> {
									viewModel?.setSortType(UPLOAD)
									viewModel?.setReverse(state != CHECKED)
								}
								R.id.by_source -> {
									viewModel?.setSortType(SOURCE)
									viewModel?.setReverse(state != CHECKED)
								}
								else -> {

								}
							}
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