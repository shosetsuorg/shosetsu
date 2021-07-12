package app.shosetsu.android.ui.library

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.BindViewHolder
import app.shosetsu.android.view.widget.TriState.State.*
import app.shosetsu.android.viewmodel.abstracted.ALibraryViewModel
import app.shosetsu.common.enums.InclusionState
import app.shosetsu.common.enums.InclusionState.EXCLUDE
import app.shosetsu.common.enums.InclusionState.INCLUDE
import app.shosetsu.common.enums.NovelSortType.*
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerLibraryBottomMenu0Binding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerLibraryBottomMenu1Binding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBottomMenuBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.TriStateCheckboxBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil.calculateDiff
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL as LLM_VERTICAL

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
	private val controller: LibraryController,
	private val viewModel: ALibraryViewModel
) {
	@Suppress("ProtectedInFinal")
	protected val layoutInflater = controller.activity!!.layoutInflater

	fun build(): View =
		ControllerNovelInfoBottomMenuBinding.inflate(
			layoutInflater
		).also { binding ->
			binding.viewPager.apply {
				this.adapter = MenuAdapter(binding.root.context)
			}
		}.root

	/** Creates the first menu */
	private inner class Menu0 {
		inner class ListModel(
			val textView: AppCompatTextView,
			val recyclerView: RecyclerView,
			val liveData: LiveData<List<String>>,
			val retrieveState: () -> HashMap<String, InclusionState>,
			val setState: (String, InclusionState) -> Unit,
			val removeState: (String) -> Unit
		) {
			/**
			 * Represents the checkbox to toggle with
			 * @param inclusionState, The initial state this should start with
			 */
			inner class FilterModel(
				val filterKeyName: String,
				val inclusionState: InclusionState?
			) : BaseRecyclerItem<FilterModel.ViewHolder>() {
				override var identifier: Long
					get() = filterKeyName.hashCode().toLong()
					set(_) {}

				inner class ViewHolder(view: View) :
					BindViewHolder<FilterModel, TriStateCheckboxBinding>(view) {

					override val binding = TriStateCheckboxBinding.bind(view)

					override fun TriStateCheckboxBinding.bindView(
						item: FilterModel,
						payloads: List<Any>
					) {
						this.root.apply {
							setText(item.filterKeyName)
							state = item.inclusionState?.let {
								when (it) {
									INCLUDE -> CHECKED
									EXCLUDE -> UNCHECKED
								}
							} ?: IGNORED
							onStateChangeListeners.add {
								when (it) {
									CHECKED -> setState(item.filterKeyName, INCLUDE)
									UNCHECKED -> setState(item.filterKeyName, EXCLUDE)
									IGNORED -> removeState(item.filterKeyName)
								}
							}
						}
					}

					override fun TriStateCheckboxBinding.unbindView(item: FilterModel) {
						this.root.apply {
							onStateChangeListeners.clear()
							clearOnClickListeners()
							setText(null)
							state = IGNORED
						}
					}
				}

				override val layoutRes: Int = R.layout.tri_state_checkbox
				override val type: Int = R.layout.tri_state_checkbox
				override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

				override fun toString(): String =
					"FilterModel(key='$filterKeyName', state=$inclusionState, id=$identifier)"
			}
		}

		operator fun invoke(container: ViewGroup): View =
			ControllerLibraryBottomMenu0Binding.inflate(
				layoutInflater,
				container,
				false
			).apply {
				buildView()
			}.root

		private fun ControllerLibraryBottomMenu0Binding.buildView() {
			unreadStatus.apply {
				state = when (viewModel.getUnreadFilter()) {
					INCLUDE -> CHECKED
					EXCLUDE -> UNCHECKED
					null -> IGNORED
				}
				onStateChangeListeners.add {
					when (it) {
						IGNORED -> viewModel.setUnreadFilter(null)
						CHECKED -> viewModel.setUnreadFilter(INCLUDE)
						UNCHECKED -> viewModel.setUnreadFilter(EXCLUDE)
					}
				}
			}
			arrayListOf(
				ListModel(
					filterGenresLabel,
					filterGenres,
					viewModel.genresLiveData,
					{ viewModel.getFilterGenres() },
					{ s, b -> viewModel.addGenreToFilter(s, b) }
				) { viewModel.removeGenreFromFilter(it) },
				ListModel(
					filterTagsLabel,
					filterTags,
					viewModel.tagsLiveData,
					{ viewModel.getFilterTags() },
					{ s, b -> viewModel.addTagToFilter(s, b) }
				) { viewModel.removeTagFromFilter(it) },
				ListModel(
					filterAuthorsLabel,
					filterAuthors,
					viewModel.authorsLiveData,
					{ viewModel.getFilterAuthors() },
					{ s, b -> viewModel.addAuthorToFilter(s, b) }
				) { viewModel.removeAuthorFromFilter(it) },
				ListModel(
					filterArtistsLabel,
					filterArtists,
					viewModel.artistsLiveData,
					{ viewModel.getFilterArtists() },
					{ s, b -> viewModel.addArtistToFilter(s, b) }
				) { viewModel.removeArtistFromFilter(it) }
			).forEach { listModel ->
				val textView = listModel.textView
				val recycler = listModel.recyclerView
				val live = listModel.liveData
				val retrieve = listModel.retrieveState

				val itemAdapter = ItemAdapter<ListModel.FilterModel>()
				live.observe(controller) { originalList: List<String> ->
					// Gets the current states
					val r = retrieve()
					// Converts and applies states into a newList
					val newItems = originalList.sorted().map { key ->
						listModel.FilterModel(
							key,
							r[key]
						)
					}
					// Passes update to itemAdapter
					FastAdapterDiffUtil[itemAdapter] = calculateDiff(itemAdapter, newItems)
				}
				recycler.layoutManager = LinearLayoutManager(recycler.context, LLM_VERTICAL, false)
				recycler.adapter = FastAdapter.with(itemAdapter)

				textView.setOnClickListener {
					recycler.isVisible = !recycler.isVisible
					textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
						if (recycler.isVisible)
							R.drawable.expand_less
						else R.drawable.expand_more,
						0,
						0,
						0
					)
				}

			}
		}
	}

	/** Creates the second menu */
	private inner class Menu1 {
		operator fun invoke(container: ViewGroup): View =
			ControllerLibraryBottomMenu1Binding.inflate(
				layoutInflater,
				container,
				false
			).apply {
				buildView()
			}.root

		private fun ControllerLibraryBottomMenu1Binding.buildView() {
			val reversed = viewModel.isSortReversed()

			when (viewModel.getSortType()) {
				BY_TITLE -> byTitle::state
				BY_UNREAD_COUNT -> byUnreadCount::state
				BY_ID -> byId::state
			}.set(if (!reversed) CHECKED else UNCHECKED)

			triStateGroup.addOnStateChangeListener { id, state ->
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
		}
	}

	/**
	 * Menu adapter for the filter menu
	 */
	private inner class MenuAdapter(
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
					return Menu0().invoke(container).also {
						container.addView(it)
					}
				}
				1 -> {
					return Menu1().invoke(container).also {
						container.addView(it)
					}
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