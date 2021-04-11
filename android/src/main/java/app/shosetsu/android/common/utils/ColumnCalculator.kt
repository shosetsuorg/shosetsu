package app.shosetsu.android.common.utils

import androidx.lifecycle.LiveData
import app.shosetsu.common.consts.settings.SettingKey

interface ColumnCalculator {
	val columnsInP: LiveData<Int>

	val columnsInH: LiveData<Int>

	fun calculatePColumnCount(
		widthPixels: Int,
		density: Float,
		columnWidthDp: Float,
	): Int {
		val columnCount = columnsInP.value ?: SettingKey.ChapterColumnsInPortait.default
		val screenWidthDp = widthPixels / density
		return if (columnCount <= 0) (screenWidthDp / columnWidthDp + 0.5).toInt()
		else (screenWidthDp / (screenWidthDp / columnCount) + 0.5).toInt()
	}

	fun calculateHColumnCount(
		widthPixels: Int,
		density: Float,
		columnWidthDp: Float,
	): Int {
		val columnCount = columnsInH.value ?: SettingKey.ChapterColumnsInLandscape.default
		val screenWidthDp = widthPixels / density
		return if (columnCount <= 0) (screenWidthDp / columnWidthDp + 0.5).toInt()
		else (screenWidthDp / (screenWidthDp / columnCount) + 0.5).toInt()
	}
}