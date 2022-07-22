package app.shosetsu.android.common.ext

import android.content.res.Resources
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import app.shosetsu.android.activity.MainActivity
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


fun Fragment.makeSnackBar(
	@StringRes stringRes: Int,
	@BaseTransientBottomBar.Duration length: Int = Snackbar.LENGTH_SHORT,
) = (activity as? MainActivity)?.makeSnackBar(stringRes, length)

fun Fragment.displayOfflineSnackBar(@StringRes res: Int = R.string.you_not_online) {
	makeSnackBar(
		res,
		Snackbar.LENGTH_LONG
	)?.setAction(R.string.generic_wifi_settings) {
		startActivity(android.content.Intent(Settings.ACTION_WIFI_SETTINGS))
	}?.show()
}

fun Fragment.makeSnackBar(
	string: String,
	@BaseTransientBottomBar.Duration length: Int = Snackbar.LENGTH_SHORT,
) = (activity as? MainActivity)?.makeSnackBar(string, length)


@Throws(Resources.NotFoundException::class)
fun Fragment.getString(@StringRes resId: Int, vararg formatArgs: Any): String =
	resources.getString(resId, *formatArgs)