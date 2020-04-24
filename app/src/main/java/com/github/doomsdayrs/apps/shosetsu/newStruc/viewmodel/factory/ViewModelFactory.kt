package com.github.doomsdayrs.apps.shosetsu.newStruc.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.doomsdayrs.apps.shosetsu.newStruc.application.ShosetsuApplication
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.TT
import org.kodein.di.direct

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
 * 23 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ViewModelFactory(context: Context) : ViewModelProvider.Factory, KodeinAware {
	override val kodein: Kodein = (context as ShosetsuApplication).kodein
	override fun <T : ViewModel> create(modelClass: Class<T>): T = kodein.direct.Instance(TT(modelClass))
}