package app.shosetsu.android.ui.extensionsConfigure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_EXTENSION
import app.shosetsu.android.common.ext.picasso
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.controller.FastAdapterRecyclerController
import app.shosetsu.android.view.controller.base.CollapsedToolBarController
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.viewmodel.abstracted.IExtensionConfigureViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ConfigureExtensionViewBinding

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
 * 21 / 01 / 2020
 *
 * Opens up detailed view of an extension, allows modifications
 */
class ConfigureExtension(bundle: Bundle) :
	FastAdapterRecyclerController<ConfigureExtensionViewBinding, SettingsItemData>(bundle),
	CollapsedToolBarController {
	val viewModel: IExtensionConfigureViewModel by viewModel()

	override fun onViewCreated(view: View) {
		viewModel.setExtensionID(args.getInt(BUNDLE_EXTENSION))
		observe()
	}

	private fun observe() {
		viewModel.liveData.observe(this) { handleExtensionResult(it) }
		viewModel.extensionSettings.observe(this) { handleRecyclerUpdate(it) }
	}

	private fun handleExtensionResult(result: HResult<ExtensionUI>) =
		result.handle(
			onLoading = {
				binding.imageView.setImageResource(R.drawable.animated_refresh)
				binding.fileName.text = ""
				binding.identification.text = ""
				binding.language.text = ""
				binding.name.text = ""
				binding.uninstallButton.setOnClickListener {}
			}
		) { extensionUI ->
			if (!extensionUI.imageURL.isNullOrEmpty())
				picasso(extensionUI.imageURL!!, binding.imageView)
			binding.fileName.text = extensionUI.fileName
			binding.identification.text = extensionUI.id.toString()
			binding.language.text = extensionUI.lang
			binding.name.text = extensionUI.name
			binding.uninstallButton.setOnClickListener {
				viewModel.uninstall(extensionUI)
				activity?.onBackPressed()
			}
		}

	override fun onDestroy() {
		super.onDestroy()
		viewModel.destroy()
	}

	override fun bindView(inflater: LayoutInflater): ConfigureExtensionViewBinding =
		ConfigureExtensionViewBinding.inflate(inflater).also { recyclerView = it.settings }

	override fun handleErrorResult(e: HResult.Error) {
		TODO("Not yet implemented")
	}
}