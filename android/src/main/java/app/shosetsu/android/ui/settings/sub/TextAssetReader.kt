package app.shosetsu.android.ui.settings.sub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import app.shosetsu.android.common.enums.TextAsset
import app.shosetsu.android.common.ext.getString
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.controller.ViewedController
import app.shosetsu.android.viewmodel.abstracted.ATextAssetReaderViewModel
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.databinding.LargeReaderBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.LargeReaderBinding.inflate

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
 * Shosetsu
 * 9 / June / 2019
 */
class TextAssetReader(bundleI: Bundle) : ViewedController<LargeReaderBinding>(bundleI) {

	private val viewModel: ATextAssetReaderViewModel by viewModel()

	/**
	 * Constructor via [TextAsset]
	 */
	constructor(target: TextAsset) : this(target.bundle)


	@ExperimentalStdlibApi
	override fun onViewCreated(view: View) {
		viewModel.setTarget(args.getInt(BUNDLE_KEY, TextAsset.LICENSE.bundle.getInt(BUNDLE_KEY)))

		viewModel.targetLiveData.handleObserve {
			setViewTitle(getString(it.titleRes))
		}

		viewModel.liveData.handleObserve {
			binding.content.text = it
		}
	}

	override fun bindView(inflater: LayoutInflater): LargeReaderBinding = inflate(inflater)

	override fun handleErrorResult(e: HResult.Error) {}

	companion object {
		const val BUNDLE_KEY: String = "target"
		private val TextAsset.bundle: Bundle
			get() = bundleOf(BUNDLE_KEY to ordinal)
	}
}