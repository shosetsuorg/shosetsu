package app.shosetsu.android.view.controller

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.getString
import app.shosetsu.android.common.ext.handleObserve
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
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
 */

/**
 * Shosetsu
 *
 * @since 04 / 08 / 2021
 * @author Doomsdayrs
 */
abstract class ShosetsuController : LifecycleController, DIAware {
	/** Title of this view, Applies to the app system */
	@StringRes
	open val viewTitleRes: Int = -1

	/** String from [viewTitleRes] else just Shosetsu */
	open val viewTitle: String by lazy {
		if (viewTitleRes != -1)
			getString(viewTitleRes)
		else getString(R.string.app_name)
	}

	constructor()
	constructor(args: Bundle) : super(args)

	override val di: DI by lazy { (applicationContext as DIAware).di }

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

	private fun Controller.instance(): String =
		"${javaClass.simpleName}@${Integer.toHexString(hashCode())}"

	/**
	 * What to do once the view is created
	 * Called by the [lifecycleListeners] via [LifecycleListener.postCreateView]
	 */
	abstract fun onViewCreated(view: View)

	/**
	 * Set the title of the view
	 */
	open fun setViewTitle(viewTitle: String = this.viewTitle) {
		logI("Activity title $viewTitle")
		activity?.title = viewTitle
	}

	/**
	 * Convenience method to observe [LiveData] without having to pass the owner argument
	 */
	fun <T> LiveData<T>.observe(observer: (T) -> Unit) =
		observe(this@ShosetsuController, observer)

	/**
	 * Convenience method to simplify [handleObserve] with self
	 */
	inline fun <T : HResult<D>, reified D> LiveData<T>.handleObserve(
		crossinline onLoading: () -> Unit = {},
		crossinline onEmpty: () -> Unit = {},
		crossinline onError: (HResult.Error) -> Unit = {},
		crossinline onSuccess: (D) -> Unit
	) = handleObserve(this@ShosetsuController, onLoading, onEmpty, onError, onSuccess)
}