package app.shosetsu.android.view.uimodels.model

import android.view.View
import androidx.core.view.isVisible
import app.shosetsu.android.common.ext.picasso
import app.shosetsu.android.domain.model.base.Convertible
import app.shosetsu.android.domain.model.local.ExtensionEntity
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.BindViewHolder
import app.shosetsu.lib.Version
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ExtensionCardBinding

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
 */
data class ExtensionUI(
        val id: Int,
        val repoID: Int,
        var name: String,
        val fileName: String,
        var imageURL: String?,
        var lang: String,
        var isExtEnabled: Boolean,
        var installed: Boolean,
        var installedVersion: Version?,
        var repositoryVersion: Version,
        var md5: String,
) : BaseRecyclerItem<ExtensionUI.ViewHolder>(), Convertible<ExtensionEntity> {
    override val layoutRes: Int = R.layout.extension_card
    override val type: Int = R.layout.extension_card
    override var identifier: Long
        get() = id.toLong()
        set(value) {}

    enum class State { UPDATE, NO_UPDATE, OBSOLETE }

    fun updateState(): State {
        if (repositoryVersion == Version(-9, -9, -9)) return State.OBSOLETE
        return if (installedVersion != null && installedVersion != repositoryVersion)
            State.UPDATE else State.NO_UPDATE
    }

    override fun convertTo(): ExtensionEntity = ExtensionEntity(
            id,
            repoID,
            name,
            fileName,
            imageURL,
            lang,
            isExtEnabled,
            installed,
            installedVersion,
            repositoryVersion,
            md5
    )

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    /***/
    class ViewHolder(
            view: View
    ) : BindViewHolder<ExtensionUI, ExtensionCardBinding>(view) {
        override val binding = ExtensionCardBinding.bind(view)

        override fun ExtensionCardBinding.bindView(item: ExtensionUI, payloads: List<Any>) {
            if (item.installed && item.isExtEnabled) {
                button.isVisible = false
                version.text = item.installedVersion?.let { with(it) { "$major.$minor.$patch" } }

                when (item.updateState()) {
                    State.UPDATE -> {
                        button.isVisible = true
                        button.setImageResource(R.drawable.download_tinted)
                        button.rotation = 180f

                        updateVersion.visibility = View.VISIBLE
                        updateVersion.text = with(item.repositoryVersion) { "$major.$minor.$patch" }
                    }
                    State.NO_UPDATE -> {
                        updateVersion.visibility = View.GONE
                    }
                    State.OBSOLETE -> {
                        updateVersion.visibility = View.VISIBLE
                        updateVersion.setText(R.string.obsolete_extension)
                        updateVersion.textSize = 32f
                    }
                }
            } else {
                version.text = with(item.repositoryVersion) { "$major.$minor.$patch" }
                settings.isVisible = false
            }

            title.text = item.name
            language.text = item.lang

            if (!item.imageURL.isNullOrEmpty()) picasso(item.imageURL!!, imageView)
        }

        override fun ExtensionCardBinding.unbindView(item: ExtensionUI) {
            button.setImageResource(R.drawable.download)
            button.rotation = 0f
            button.isVisible = true

            settings.isVisible = true

            version.text = null
            updateVersion.text = null
            title.text = null
            language.text = null
            imageView.setImageResource(R.drawable.broken_image)
        }
    }
}