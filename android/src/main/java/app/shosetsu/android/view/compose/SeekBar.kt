package app.shosetsu.android.view.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun PreviewSeekBar() {
	var value by remember { mutableStateOf(1) }
	Box(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		DiscreteSlider(range = 1..5, indexName = {
			"$it"
		}, value = value, onValueChange = {
			value = it
		},
			showAllIndices = true
		)
	}
}

@Composable
fun DiscreteSlider(
	range: IntRange,
	indexName: (index: Int) -> String,
	value: Int,
	onValueChange: (Int) -> Unit,
	showAllIndices: Boolean = false,
	showIndex: ((Int) -> Boolean)? = null,
	textStyle: TextStyle = LocalTextStyle.current
) {
	if (!showAllIndices)
		requireNotNull(showIndex)

	val (sliderValue, setSliderValue) = remember { mutableStateOf(value) }
	val drawPadding = with(LocalDensity.current) { 10.dp.toPx() }
	val lineHeightDp = 10.dp
	val lineHeightPx = with(LocalDensity.current) { lineHeightDp.toPx() }
	val canvasHeight = 50.dp
	val textSize = textStyle.fontSize.value
	println(textSize)
	val textPaint = android.graphics.Paint().apply {
		color = textStyle.color.value.toInt()
		textAlign = android.graphics.Paint.Align.CENTER
		this.textSize = textSize * 2
	}
	Box(contentAlignment = Alignment.Center) {
		Canvas(
			modifier = Modifier
				.height(canvasHeight)
				.fillMaxWidth()
				.padding(
					top = canvasHeight
						.div(2)
						.minus(lineHeightDp.div(2))
				)
		) {
			val yStart = 0f
			val distance = (size.width.minus(2 * drawPadding)).div(range.last - 1)
			range.forEachIndexed { index, step ->
				drawLine(
					color = Color.DarkGray,
					start = Offset(x = drawPadding + index.times(distance), y = yStart),
					end = Offset(x = drawPadding + index.times(distance), y = lineHeightPx)
				)
				if (showAllIndices || showIndex!!.invoke(index)) {
					this.drawContext.canvas.nativeCanvas.drawText(
						indexName(step),
						drawPadding + index.times(distance),
						textSize,
						textPaint
					)
				}
			}
		}
		Slider(
			modifier = Modifier.fillMaxWidth(),
			value = sliderValue.toFloat(),
			valueRange = range.first.toFloat()..range.last.toFloat(),
			steps = range.last.minus(2),
			onValueChange = {
				setSliderValue(it.toInt())
				onValueChange(it.toInt())
			})
	}
}