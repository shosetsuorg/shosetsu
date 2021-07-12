package app.shosetsu.android.common.ext

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import app.shosetsu.android.activity.MainActivity
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.github.doomsdayrs.apps.shosetsu.R
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

fun Controller.displayOfflineSnackBar(@StringRes res: Int = R.string.you_not_online) {
	makeSnackBar(
		res,
		Snackbar.LENGTH_LONG
	)?.setAction(R.string.generic_wifi_settings) {
		startActivity(android.content.Intent(Settings.ACTION_WIFI_SETTINGS))
	}?.show()
}

fun Controller.makeSnackBar(
	string: String,
	@BaseTransientBottomBar.Duration length: Int = Snackbar.LENGTH_SHORT,
) = (activity as? MainActivity)?.makeSnackBar(string, length)


fun Controller.withFadeTransaction(): RouterTransaction = RouterTransaction.with(this)
	.pushChangeHandler(FadeChangeHandler())
	.popChangeHandler(FadeChangeHandler())

/**
 * Invoke [Router.pushController] while applying [withFadeTransaction] on [target]
 */
fun Router.shosetsuPush(target: Controller) {
	pushController(target.withFadeTransaction())
}


val Controller.context: Context?
	get() = applicationContext

fun Controller.getString(@StringRes resId: Int, vararg formatArgs: Any): String =
	resources?.getString(resId, *formatArgs)!!