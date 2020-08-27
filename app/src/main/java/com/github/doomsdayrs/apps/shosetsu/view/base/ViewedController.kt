package com.github.doomsdayrs.apps.shosetsu.view.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchAsync
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchUI
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

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
abstract class ViewedController : LifecycleController, KodeinAware {
	/**
	 * Tells [ViewedController] to attach [id] to ihe [AnnotationTarget.FIELD]
	 * @param id ID of the view
	 */
	@Retention(AnnotationRetention.RUNTIME)
	@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
	@Nullable
	annotation class Attach(@IdRes val id: Int)

	constructor()
	constructor(args: Bundle) : super(args)

	/** Title of this view, Applies to the app system */
	@StringRes
	open val viewTitle: Int = -1

	override val kodein: Kodein by lazy { (applicationContext as KodeinAware).kodein }

	/**
	 * Layout res of the view to build
	 */
	abstract val layoutRes: Int

	/**
	 * Should this be attached to root
	 */
	open val attachToRoot: Boolean = false
	private var attachedFields = ArrayList<KMutableProperty<*>>()

	/**
	 * Function run when destroying the UI
	 */
	@CallSuper
	override fun onDestroyView(view: View) {
		val s = StringBuilder()
		attachedFields.forEachIndexed { index, kMutableProperty ->
			s.append(kMutableProperty.name)
			if (index + 1 != attachedFields.size) s.append(", ")
			kMutableProperty.setter.call(this, null)
		}
		Log.d(logID(), "Destroyed:\t$s")
		attachedFields = ArrayList()
	}

	@CallSuper
	override fun onDestroy() {
		Log.d(logID(), "Destroying Controller")
		launchAsync {
			(activity as AppCompatActivity).viewModelStore.clear()
		}
		super.onDestroy()
	}

	@CallSuper
	override fun onDetach(view: View) {
		Log.d(logID(), "Detaching View")
		super.onDetach(view)
	}

	@CallSuper
	override fun onAttach(view: View) {
		Log.d(logID(), "Attaching View")
		super.onAttach(view)
	}

	/**
	 * This creates the view of the activity, Also attaches all [Attach] annotations
	 */
	@CallSuper
	open fun createViewInstance(inflater: LayoutInflater, container: ViewGroup): View {
		val view = inflater.inflate(layoutRes, container, attachToRoot)
		this::class.memberProperties
				.filter { it.annotations.isNotEmpty() }
				.filter { it.findAnnotation<Attach>() != null }
				.filter { it.visibility == KVisibility.PUBLIC }
				.filterIsInstance<KMutableProperty<*>>()
				.forEach { field ->
					//Log.d(logID(), "Processing Attach Target\t${field.name}")
					field.findAnnotation<Attach>()?.let {
						//Log.d(logID(), "\tApplying ${a.id} to ${field.name}")
						field.setter.call(this, view.findViewById(it.id))
						attachedFields.add(field)
					} ?: Log.e(logID(), "Failed to find annotation on field")
				}
		return view
	}

	/**
	 * The main creation method
	 */
	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup,
			savedViewState: Bundle?
	): View {
		if (viewTitle != -1) activity?.setTitle(viewTitle)
		val view = createViewInstance(inflater, container)
		onViewCreated(view)
		return view
	}

	/**
	 * What to do once the view is created
	 */
	abstract fun onViewCreated(view: View)

	/**
	 * Show an error on screen
	 */
	open fun showError(e: HResult.Error) {
		toast { e.message }
	}


	/** @see [toast] */
	fun toast(
			length: Int = Toast.LENGTH_SHORT,
			message: () -> String
	) {
		launchUI {
			applicationContext?.toast(message(), length)
		}
	}

	/** @see [toast] */
	fun toast(
			length: Int = Toast.LENGTH_SHORT,
			@StringRes message: Int
	) {
		launchUI {
			applicationContext?.toast(message, length)
		}
	}
}