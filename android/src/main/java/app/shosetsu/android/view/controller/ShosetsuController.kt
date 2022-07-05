package app.shosetsu.android.view.controller

import android.os.Bundle
import androidx.activity.result.ActivityResultRegistry
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import app.shosetsu.android.common.ext.collectLA
import app.shosetsu.android.common.ext.collectLatestLA
import app.shosetsu.android.common.ext.logI
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
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
abstract class ShosetsuController : Fragment(), DIAware {
	/** Title of this view, Applies to the app system */
	@StringRes
	open val viewTitleRes: Int = -1

	/** String from [viewTitleRes] else just Shosetsu */
	open val viewTitle: String by lazy {
		if (viewTitleRes != -1)
			getString(viewTitleRes)
		else getString(R.string.app_name)
	}

	override val di: DI by lazy { (activity as DIAware).di }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		onLifecycleCreate(this, (activity as AppCompatActivity).activityResultRegistry)
	}

	/**
	 * Set the title of the view
	 */
	open fun setViewTitle(viewTitle: String = this.viewTitle) {
		logI("Activity title $viewTitle")
		activity?.title = viewTitle
	}

	fun <T> Flow<T>.observe(
		catch: suspend FlowCollector<T>.(Throwable) -> Unit,
		onCollect: FlowCollector<T>
	) =
		collectLA(this@ShosetsuController, catch, onCollect)

	fun <T> Flow<T>.observeLatest(
		catch: suspend FlowCollector<T>.(Throwable) -> Unit,
		onCollect: FlowCollector<T>
	) =
		collectLatestLA(this@ShosetsuController, catch, onCollect)


	open fun onLifecycleCreate(owner: LifecycleOwner, registry: ActivityResultRegistry) {}

}