package app.shosetsu.android.common.utils

interface ColumnCalculator {
	fun getColumnsInP(): Int

	fun getColumnsInH(): Int
	
	fun calculatePColumnCount(
			widthPixels: Int,
			density: Float,
			columnWidthDp: Float,
	): Int {
		val columnCount = getColumnsInP()
		val screenWidthDp = widthPixels / density
		return if (columnCount <= 0) (screenWidthDp / columnWidthDp + 0.5).toInt()
		else (screenWidthDp / (screenWidthDp / columnCount) + 0.5).toInt()
	}

	fun calculateHColumnCount(
			widthPixels: Int,
			density: Float,
			columnWidthDp: Float,
	): Int {
		val columnCount = getColumnsInH()
		val screenWidthDp = widthPixels / density
		return if (columnCount <= 0) (screenWidthDp / columnWidthDp + 0.5).toInt()
		else (screenWidthDp / (screenWidthDp / columnCount) + 0.5).toInt()
	}
}