package app.shosetsu.android.ui.settings.sub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.common.ext.readAsset
import app.shosetsu.android.view.controller.ViewedController
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.databinding.LargeReaderBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.LargeReaderBinding.inflate
import java.util.*


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
	companion object {
		const val BUNDLE_KEY: String = "target"
	}

	/**
	 * Constructor via [Target]
	 */
	constructor(target: Target) : this(target.bundle)

	enum class Target(val bundle: Bundle) {
		LICENSE(bundleOf(Pair(BUNDLE_KEY, "license-gplv3"))),
		DISCLAIMER(bundleOf(Pair(BUNDLE_KEY, "disclaimer")));
	}

	private var type: String = ""
	private var message: String = ""

	private fun handleB() {
		Log.d(logID(), "Setting Message")
		type = args.getString(BUNDLE_KEY, "license")
		message = activity?.readAsset("$type.text") ?: ""
	}

	override fun onSaveInstanceState(outState: Bundle) {
		outState.putString("m", message)
		outState.putString("t", type)
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		message = savedInstanceState.getString("m", "")
		type = savedInstanceState.getString("t", "")
	}

	@ExperimentalStdlibApi
	override fun onViewCreated(view: View) {
		if (message.isEmpty()) handleB()
		activity?.title =
			(type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
		binding.title.text = message
	}

	override fun bindView(inflater: LayoutInflater): LargeReaderBinding = inflate(inflater)
	override fun handleErrorResult(e: HResult.Error) {
		TODO("Not yet implemented")
	}

}