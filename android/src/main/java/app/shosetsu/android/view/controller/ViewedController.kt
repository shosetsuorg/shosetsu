package app.shosetsu.android.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import app.shosetsu.android.common.ext.logI

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
abstract class ViewedController<VB : ViewBinding> : ShosetsuController {


	/**
	 * The ViewBinding that is used by child views
	 */
	lateinit var binding: VB

	constructor()
	constructor(args: Bundle) : super(args)

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
	 * Creates the viewBinding
	 */
	abstract fun bindView(inflater: LayoutInflater): VB

	/**
	 * Show an error on screen
	 */
	abstract fun handleErrorResult(e: Throwable)
}