package app.shosetsu.android.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import app.shosetsu.android.common.ext.*
import app.shosetsu.common.dto.HResult
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import com.github.doomsdayrs.apps.shosetsu.R
import org.kodein.di.DI
import org.kodein.di.DIAware

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
 * ====================================================================
 */

/**
 * shosetsu
 * 23 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */
abstract class ViewedController<VB : ViewBinding> : LifecycleController, DIAware {
	/** Title of this view, Applies to the app system */
	@StringRes
	open val viewTitleRes: Int = -1

	/** String from [viewTitleRes] else just Shosetsu */
	open val viewTitle: String by lazy {
		if (viewTitleRes != -1)
			getString(viewTitleRes)
		else getString(R.string.app_name)
	}
	override val di: DI by lazy { (applicationContext as DIAware).di }

	/**
	 * The ViewBinding that is used by child views
	 */
	lateinit var binding: VB

	init {
		addLifecycleListener(object : LifecycleListener() {
			override fun postCreateView(controller: Controller, view: View) {
				logV("Manipulate view for ${controller.instance()}")
				onViewCreated(view)
			}

			override fun preCreateView(controller: Controller) {
				logV("Create view for ${controller.instance()}")
			}

			override fun preAttach(controller: Controller, view: View) {
				logV("Attach view for ${controller.instance()}")
			}

			override fun preDetach(controller: Controller, view: View) {
				logV("Detach view for ${controller.instance()}")
			}

			override fun preDestroyView(controller: Controller, view: View) {
				logV("Destroy view for ${controller.instance()}")
			}
		})
	}

	constructor()
	constructor(args: Bundle) : super(args)

	private fun Controller.instance(): String =
		"${javaClass.simpleName}@${Integer.toHexString(hashCode())}"

	@CallSuper
	override fun onDestroy() {
		logI("Destroying Controller")
		super.onDestroy()
	}

	@CallSuper
	override fun onDetach(view: View) {
		logI("Detaching View")
		super.onDetach(view)
	}

	@CallSuper
	override fun onAttach(view: View) {
		logI("Attaching View")
		super.onAttach(view)
	}

	/**
	 * Set the title of the view
	 */
	open fun setViewTitle(viewTitle: String = this.viewTitle) {
		logI("Activity title $viewTitle")
		activity?.title = viewTitle
	}

	/**
	 * The main creation method
	 */
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?,
	): View {
		setViewTitle()
		binding = bindView(inflater)
		return binding.root
	}

	/**
	 * What to do once the view is created
	 * Called by the [lifecycleListeners] via [LifecycleListener.postCreateView]
	 */
	abstract fun onViewCreated(view: View)

	/**
	 * Creates the viewBinding
	 */
	abstract fun bindView(inflater: LayoutInflater): VB

	/**
	 * Show an error on screen
	 */
	abstract fun handleErrorResult(e: HResult.Error)

	/**
	 * Convenience method to observe [LiveData] without having to pass the owner argument
	 */
	fun <T> LiveData<T>.observe(observer: (T) -> Unit) =
		observe(this@ViewedController, observer)

	/**
	 * Convenience method to simplify [handleObserve] with self
	 */
	inline fun <T : HResult<D>, reified D> LiveData<T>.handleObserve(
		crossinline onLoading: () -> Unit = {},
		crossinline onEmpty: () -> Unit = {},
		crossinline onError: (HResult.Error) -> Unit = {},
		crossinline onSuccess: (D) -> Unit
	) = handleObserve(this@ViewedController, onLoading, onEmpty, onError, onSuccess)
}