package app.shosetsu.android.ui.extensionsConfigure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_EXTENSION
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.view.controller.FastAdapterRecyclerController
import app.shosetsu.android.view.controller.base.CollapsedToolBarController
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.viewmodel.abstracted.AExtensionConfigureViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ConfigureExtensionViewBinding
import kotlinx.coroutines.delay

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
	val viewModel: AExtensionConfigureViewModel by viewModel()

	override fun onViewCreated(view: View) {
		viewModel.setExtensionID(args.getInt(BUNDLE_EXTENSION))

		var reloaded = false
		// Due to an obscure issue, the follow check is made and will reload the extension if there is no entries
		launchIO {
			delay(200)
			if (itemAdapter.adapterItemCount == 0 && !reloaded && !binding.emptyDataView.isVisible) {
				reloaded = true
				launchUI {
					viewModel.destroy()
					viewModel.setExtensionID(args.getInt(BUNDLE_EXTENSION))
				}
			}
		}

		observeChanges()
	}

	private fun observeChanges() {
		viewModel.liveData.observe { handleExtensionResult(it) }
		viewModel.extensionSettings.observe {
			logV("Received settings result $it")
			handleRecyclerUpdate(it)
		}
	}

	override fun onDestroyView(view: View) {
		binding.imageView.setImageResource(R.drawable.animated_refresh)
		binding.fileName.text = null
		binding.identification.text = null
		binding.language.text = null
		binding.name.text = null
		binding.uninstallButton.setOnClickListener(null)
	}

	private fun handleExtensionResult(result: HResult<ExtensionUI>) =
		result.handle(
			onEmpty = {
				onDestroyView(binding.root)
			},
			onLoading = {
				onDestroyView(binding.root)
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

	override fun showEmpty() {
		binding.settingsProgressBar.isVisible = false
		binding.emptyDataView.show(R.string.controller_configure_extension_empty_settings)
	}

	override fun showLoading() {
		binding.settingsProgressBar.isVisible = true
	}

	override fun hideEmpty() {
		binding.settingsProgressBar.isVisible = false
		binding.emptyDataView.hide()
	}

	override fun onDestroy() {
		super.onDestroy()
		viewModel.destroy()
	}

	override fun bindView(inflater: LayoutInflater): ConfigureExtensionViewBinding =
		ConfigureExtensionViewBinding.inflate(inflater).also { recyclerView = it.settings }

	override fun updateUI(newList: List<SettingsItemData>) {
		logD("updateUI with ${newList.size}")
		super.updateUI(newList)
		binding.settingsProgressBar.isVisible = false
	}

	override fun handleErrorResult(e: HResult.Error) {
		logW("handleErrorResult invoked")
		e.exception?.printStackTrace()
	}
}