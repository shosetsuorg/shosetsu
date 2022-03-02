package app.shosetsu.android.view.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.MdcTheme
import kotlin.math.roundToInt

@Preview
@Composable
fun PreviewSeekBar() {
	var value by remember { mutableStateOf(1) }
	MdcTheme {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)
		) {
			DiscreteSlider(
				value,
				"${value}h",
				{
					value = it
				},
				0..10,
			)
		}
	}
}


/**
 * This creates a sudo discrete slider
 *
 * @param value Value to set [Slider] to
 * @param parsedValue [value] parsed to be displayed as a string
 * @param updateValue Called when [Slider] updates its value, is fed a rounded float
 * @param valueRange An integer range of possible values
 */
@Composable
fun DiscreteSlider(
	value: Int,
	parsedValue: String,
	updateValue: (Int) -> Unit,
	valueRange: IntRange
) {
	Row(
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(parsedValue, modifier = Modifier.padding(end = 8.dp))
		Slider(
			value.toFloat(),
			{
				updateValue(it.roundToInt())
			},
			valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
			steps = valueRange.count() - 2
		)
	}
}