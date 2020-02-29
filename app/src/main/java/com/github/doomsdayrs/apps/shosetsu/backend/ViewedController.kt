package com.github.doomsdayrs.apps.shosetsu.backend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.bluelinelabs.conductor.Controller
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
    companion object {
        private const val logID = "ViewedController"
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
    annotation class Attach(@IdRes val id: Int)

    abstract val idRes: Int
    open val attachToRoot: Boolean = false
    private var attachedFields = ArrayList<KMutableProperty<*>>()

    override fun onDestroyView(view: View) {
        for (a in attachedFields.asReversed()) {
            Log.d(logID, "\tDestroying ${a.name}")
            a.setter.call(this, null)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(idRes, container, attachToRoot)
        this::class.memberProperties
                .filter { it.annotations.isNotEmpty() }
                .filter { it.findAnnotation<Attach>() != null }
                .filter { it.visibility == KVisibility.PUBLIC }
                .filterIsInstance<KMutableProperty<*>>()
                .forEach { field ->
                    Log.d(logID, "Processing Attach Target\t${field.name}")
                    val a = field.findAnnotation<Attach>()!!
                    Log.d(logID, "\tApplying ${a.id} to ${field.name}")
                    field.setter.call(this, view.findViewById(a.id))
                    attachedFields.add(field)
                }
        onViewCreated(view)
        return view
    }

    abstract fun onViewCreated(view: View)
}