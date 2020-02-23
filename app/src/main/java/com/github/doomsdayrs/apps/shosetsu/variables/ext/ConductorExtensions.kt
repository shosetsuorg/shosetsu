package com.github.doomsdayrs.apps.shosetsu.variables.ext

import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler

fun Router.popControllerWithTag(tag: String): Boolean {
    val controller = getControllerWithTag(tag)
    if (controller != null) {
        popController(controller)
        return true
    }
    return false
}

fun Controller.requestPermissionsSafe(permissions: Array<String>, requestCode: Int) {
    activity?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions.forEach { permission ->
                if (ContextCompat.checkSelfPermission(it, permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(permission), requestCode)
                }
            }
        }
    } ?: return
}

fun Controller.withFadeTransaction(): RouterTransaction = RouterTransaction.with(this)
        .pushChangeHandler(FadeChangeHandler())
        .popChangeHandler(FadeChangeHandler())
