package app.shosetsu.android.view.uimodels.model

import android.view.View
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.BindViewHolder
import app.shosetsu.android.view.uimodels.model.RepositoryUI.ViewHolder
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.dto.Convertible
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.RecyclerRepositoryInfoBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.RecyclerRepositoryInfoBinding.bind

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

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @see RepositoryEntity
 */
data class RepositoryUI(
	val id: Int,
	val url: String,
	val name: String,
	val isRepoEnabled: Boolean
) : BaseRecyclerItem<ViewHolder>(), Convertible<RepositoryEntity> {
	override val layoutRes: Int = R.layout.recycler_repository_info
	override val type: Int = R.layout.recycler_repository_info
	override var identifier: Long
		get() = id.toLong()
		set(_) {}

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	class ViewHolder(view: View) :
		BindViewHolder<RepositoryUI, RecyclerRepositoryInfoBinding>(view) {
		override val binding: RecyclerRepositoryInfoBinding = bind(view)

		override fun RecyclerRepositoryInfoBinding.bindView(
			item: RepositoryUI,
			payloads: List<Any>
		) {
			repoID.text = item.id.toString()
			title.text = item.name
			url.text = item.url
			switchWidget.isChecked = item.isRepoEnabled
		}

		override fun RecyclerRepositoryInfoBinding.unbindView(item: RepositoryUI) {
			repoID.setText(R.string.negative_one)
			title.text = ""
			url.text = ""
			switchWidget.isChecked = false
		}
	}

	override fun convertTo(): RepositoryEntity = RepositoryEntity(id, url, name, isRepoEnabled)
}