package app.shosetsu.android.common.ext

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import app.shosetsu.android.activity.MainActivity
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

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
				if (ContextCompat.checkSelfPermission(
						it,
						permission
					) != PackageManager.PERMISSION_GRANTED
				) {
					requestPermissions(arrayOf(permission), requestCode)
				}
			}
		}
	} ?: return
}

fun Controller.makeSnackBar(
	@StringRes stringRes: Int,
	@BaseTransientBottomBar.Duration length: Int = Snackbar.LENGTH_SHORT,
) = (activity as? MainActivity)?.makeSnackBar(stringRes, length)


fun Controller.withFadeTransaction(): RouterTransaction = RouterTransaction.with(this)
	.pushChangeHandler(FadeChangeHandler())
	.popChangeHandler(FadeChangeHandler())


val Controller.context: Context?
	get() = applicationContext

fun Controller.getString(@StringRes resId: Int, default: String = "NULL"): String = try {
	resources?.getString(resId) ?: default
} catch (e: Resources.NotFoundException) {
	Log.d(logID(), "Could not find string resource: $resId")
	default
}