package app.shosetsu.android.view.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
				allowUserInput = true
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
	valueRange: IntRange,
	allowUserInput: Boolean = false
) {
	Row(
		verticalAlignment = Alignment.CenterVertically
	) {
		var showDialog by remember { mutableStateOf(false) }

		if (showDialog) {
			var newValue by remember { mutableStateOf(value) }

			Dialog(
				onDismissRequest = {
					showDialog = false
				}
			) {
				Card {
					Column {
						var isTextError by remember { mutableStateOf(false) }
						TextField(
							value = "$newValue", onValueChange = {
								val value = it.toIntOrNull() ?: if (it.isEmpty()) 0 else null

								if (value != null) {
									newValue = value
									isTextError = false
								} else {
									isTextError = true
								}
							}, singleLine = true, keyboardOptions = KeyboardOptions(
								keyboardType = KeyboardType.Number
							)
						)

						TextButton(onClick = {
							updateValue(newValue)
							showDialog = false
						}) {
							Text(stringResource(android.R.string.ok))
						}
					}
				}
			}
		}

		TextButton(onClick = {
			if (allowUserInput) {
				showDialog = true
			}
		}) {
			Text(text = parsedValue)
		}
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

/**
 * This creates a sudo discrete slider
 *
 * @param value Value to set [Slider] to
 * @param parsedValue [value] parsed to be displayed as a string
 * @param updateValue Called when [Slider] updates its value
 * @param valueRange An integer range of possible values
 */
@Composable
fun DiscreteSlider(
	value: Float,
	parsedValue: String,
	updateValue: (Float) -> Unit,
	valueRange: IntRange,
	allowUserInput: Boolean = false
) {
	Row(
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(parsedValue, modifier = Modifier.padding(end = 8.dp))
		Slider(
			value,
			{
				updateValue(it)
			},
			valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
			steps = valueRange.count() - 2
		)
	}
}