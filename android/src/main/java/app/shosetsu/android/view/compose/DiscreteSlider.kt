package app.shosetsu.android.view.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.doomsdayrs.apps.shosetsu.R
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
				{ it, _ ->
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
	updateValue: (Int, fromDialog: Boolean) -> Unit,
	valueRange: IntRange,
	haveSteps: Boolean = true,
	maxHeaderSize: Dp? = null,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically
	) {
		var showDialog by remember { mutableStateOf(false) }

		if (showDialog) {
			var newValue: Int? by remember { mutableStateOf(value) }

			Dialog(
				onDismissRequest = {
					showDialog = false
				}
			) {
				Card {
					Column(
						modifier = Modifier.padding(8.dp),
					) {
						var isTextError by remember { mutableStateOf(false) }

						Text(
							stringResource(R.string.input_float),
							style = MaterialTheme.typography.h6,
							modifier = Modifier.padding(
								bottom = 16.dp,
								top = 8.dp,
								start = 24.dp,
								end = 24.dp
							)
						)

						Text(
							stringResource(
								R.string.input_int_range_desc,
								valueRange.first,
								valueRange.last
							),
							style = MaterialTheme.typography.body1,
							modifier = Modifier.padding(
								bottom = 16.dp,
								start = 24.dp,
								end = 24.dp
							)
						)

						TextField(
							value = if (newValue != null) "$newValue" else "",
							onValueChange = {
								val value = it.toIntOrNull()

								if (value != null) {
									if (value in valueRange) {
										newValue = value
										isTextError = false
										return@TextField
									}
								} else if (it.isEmpty()) {
									newValue = null
								}

								isTextError = true
							},
							singleLine = true,
							keyboardOptions = KeyboardOptions(
								keyboardType = KeyboardType.Number
							),
							modifier = Modifier.padding(bottom = 8.dp, start = 24.dp, end = 24.dp)
								.fillMaxWidth()
						)

						Row(
							horizontalArrangement = Arrangement.End,
							modifier = Modifier.fillMaxWidth()

						) {
							TextButton(
								onClick = {
									showDialog = false
								},
							) {
								Text(stringResource(android.R.string.cancel))
							}

							TextButton(
								onClick = {
									updateValue(newValue!!, true)
									showDialog = false
								},
								enabled = !isTextError
							) {
								Text(stringResource(R.string.apply))
							}
						}

					}
				}
			}
		}

		TextButton(onClick = {
			showDialog = true
		}) {
			Text(
				text = parsedValue,
				modifier = Modifier.let {
					if (maxHeaderSize != null)
						it.width(maxHeaderSize)
					else it
				}
			)
		}
		Slider(
			value.toFloat(),
			{
				updateValue(it.roundToInt(), false)
			},
			valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
			steps = if (haveSteps) valueRange.count() - 2 else 0
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
	updateValue: (Float, fromDialog: Boolean) -> Unit,
	valueRange: IntRange,
	haveSteps: Boolean = true,
	maxHeaderSize: Dp? = null
) {
	Row(
		verticalAlignment = Alignment.CenterVertically
	) {
		var showDialog by remember { mutableStateOf(false) }

		if (showDialog) {
			var newValue: Float? by remember { mutableStateOf(value) }

			Dialog(
				onDismissRequest = {
					showDialog = false
				}
			) {
				Card {
					Column(
						modifier = Modifier.padding(8.dp),
					) {
						var isTextError by remember { mutableStateOf(false) }

						Text(
							stringResource(R.string.input_float),
							style = MaterialTheme.typography.h6,
							modifier = Modifier.padding(
								bottom = 16.dp,
								top = 8.dp,
								start = 24.dp,
								end = 24.dp
							)
						)

						Text(
							stringResource(
								R.string.input_float_range_desc,
								valueRange.first,
								valueRange.last
							),
							style = MaterialTheme.typography.body1,
							modifier = Modifier.padding(
								bottom = 16.dp,
								start = 24.dp,
								end = 24.dp
							)
						)

						TextField(
							value = if (newValue != null) "$newValue" else "",
							onValueChange = {
								val value = it.toFloatOrNull()

								if (value != null) {
									if (valueRange.first <= value || value <= valueRange.last) {
										newValue = value
										isTextError = false
										return@TextField
									}
								} else if (it.isEmpty()) {
									newValue = null
								}

								isTextError = true
							},
							singleLine = true,
							keyboardOptions = KeyboardOptions(
								keyboardType = KeyboardType.Number
							),
							modifier = Modifier.padding(bottom = 8.dp, start = 24.dp, end = 24.dp)
								.fillMaxWidth()
						)

						Row(
							horizontalArrangement = Arrangement.End,
							modifier = Modifier.fillMaxWidth()

						) {
							TextButton(
								onClick = {
									showDialog = false
								},
							) {
								Text(stringResource(android.R.string.cancel))
							}

							TextButton(
								onClick = {
									updateValue(newValue!!, true)
									showDialog = false
								},
								enabled = !isTextError
							) {
								Text(stringResource(R.string.apply))
							}
						}

					}
				}
			}
		}

		TextButton(onClick = {
			showDialog = true
		}) {
			Text(
				text = parsedValue,
				modifier = Modifier.let {
					if (maxHeaderSize != null)
						it.width(maxHeaderSize)
					else it
				}
			)
		}
		Slider(
			value,
			{
				updateValue(it, false)
			},
			valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
			steps = if (haveSteps) valueRange.count() - 2 else 0
		)
	}
}