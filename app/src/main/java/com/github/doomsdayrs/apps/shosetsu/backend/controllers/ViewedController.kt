package com.github.doomsdayrs.apps.shosetsu.backend.controllers

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.Nullable
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.variables.ext.logID
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
abstract class ViewedController(bundle: Bundle = Bundle()) : Controller(bundle) {

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
    @Nullable
    annotation class Attach(@IdRes val id: Int)

    abstract val layoutRes: Int
    open val attachToRoot: Boolean = false
    private var attachedFields = ArrayList<KMutableProperty<*>>()

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

    open fun onCreateView1(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(layoutRes, container, attachToRoot)
        this::class.memberProperties
                .filter { it.annotations.isNotEmpty() }
                .filter { it.findAnnotation<Attach>() != null }
                .filter { it.visibility == KVisibility.PUBLIC }
                .filterIsInstance<KMutableProperty<*>>()
                .forEach { field ->
                    Log.d(logID(), "Processing Attach Target\t${field.name}")
                    val a = field.findAnnotation<Attach>()!!
                    Log.d(logID(), "\tApplying ${a.id} to ${field.name}")
                    field.setter.call(this, view.findViewById(a.id))
                    attachedFields.add(field)
                }
        return view
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = onCreateView1(inflater, container)
        onViewCreated(view)
        return view
    }

    abstract fun onViewCreated(view: View)
}