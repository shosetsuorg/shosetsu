package app.shosetsu.libs.bubbleseekbar

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.COMPLEX_UNIT_SP
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

internal object BubbleUtils {
	private const val KEY_MIUI_MANE = "ro.miui.ui.version.name"
	private val sProperties by lazy { Properties() }

	private var miui: Boolean? = null

	val isMIUI: Boolean
		get() = miui ?: run {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
				var fis: FileInputStream? = null
				try {
					fis = FileInputStream(File(Environment.getRootDirectory(), "build.prop"))
					sProperties.load(fis)
				} catch (e: IOException) {
					e.printStackTrace()
				} finally {
					if (fis != null) {
						try {
							fis.close()
						} catch (e: IOException) {
							e.printStackTrace()
						}
					}
				}
				miui = sProperties.containsKey(KEY_MIUI_MANE)
			} else {
				val clazz: Class<*>
				try {
					@SuppressLint("PrivateApi")
					clazz = Class.forName("android.os.SystemProperties")
					val getMethod = clazz.getDeclaredMethod("get", String::class.java)
					val name = getMethod.invoke(null, KEY_MIUI_MANE) as String
					miui = !TextUtils.isEmpty(name)
				} catch (e: Exception) {
					miui = false
				}
			}
			miui!!
		}

	fun dp2px(dp: Int): Int =
		TypedValue.applyDimension(
			COMPLEX_UNIT_DIP, dp.toFloat(),
			Resources.getSystem().displayMetrics
		).toInt()

	fun sp2px(sp: Int): Int =
		TypedValue.applyDimension(
			COMPLEX_UNIT_SP, sp.toFloat(),
			Resources.getSystem().displayMetrics
		).toInt()
}