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
import kotlin.reflect.full.hasAnnotation
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
            a.setter.call(this, null)
        }
    }

    fun fieldHasAttach(field: KMutableProperty<*>): Boolean {
        Log.d(logID, "\tExpecting ${Attach::class}")
        Log.d(logID, "\tField has ${field.annotations.size} annotations")
        field.annotations.forEach { an ->
            Log.d(logID, "\tProcessing ${an.annotationClass}")
            if (an.annotationClass == Attach::class)
                return true
        }
        return false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(idRes, container, attachToRoot)
        this::class.memberProperties
                .let {
                    Log.d(logID, "Start size ${it.size}")
                    return@let it
                }
                .filterIsInstance<KMutableProperty<*>>()
                .let {
                    Log.d(logID, "Size after Var check ${it.size}")
                    return@let it
                }
                .filter { it.annotations.isNotEmpty() }
                .let {
                    Log.d(logID, "Size after Empty check ${it.size}")
                    return@let it
                }
                .filter { @UseExperimental(ExperimentalStdlibApi::class) it.hasAnnotation<Attach>() }
                .let {
                    Log.d(logID, "Size after Attach check ${it.size}")
                    return@let it
                }
                .filter { it.visibility == KVisibility.PUBLIC }
                .let {
                    Log.d(logID, "Size after Public check ${it.size}")
                    return@let it
                }
                .forEach { field ->
                    Log.d(logID, "Processing\t${field.name}")
                    if (fieldHasAttach(field)) {
                        val a = field.findAnnotation<Attach>()
                        a?.let {
                            Log.d(logID, "Applying ${it.id} to ${field.name}")
                            field.setter.call(this, view.findViewById(it.id))
                            attachedFields.add(field)
                        }
                    }
                }
        onViewCreated(view)
        return view
    }

    abstract fun onViewCreated(view: View)
}