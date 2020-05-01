package com.github.doomsdayrs.apps.shosetsu.common.ext

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

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import com.github.doomsdayrs.apps.shosetsu.viewmodel.factory.ViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.direct
import org.kodein.di.generic.instance

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */

/**
 * KODEIN EXT
 **/
inline fun <reified VM : ViewModel, T> T.viewModel(): Lazy<VM> where T : KodeinAware, T : LifecycleController {
	return lazy(LazyThreadSafetyMode.NONE) {
		ViewModelProvider(
				this.activity as AppCompatActivity,
				direct.instance<ViewModelFactory>()
		)[VM::class.java]
	}
}