package app.shosetsu.android.view.compose.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import com.chargemap.compose.numberpicker.NumberPicker
import com.github.doomsdayrs.apps.shosetsu.R

@Composable
fun NumberPickerSettingContent(
	title: String,
	description: String,
	range: IntRange,
	repo: ISettingsRepository,
	key: SettingKey<Int>,
	modifier: Modifier = Modifier,
) {
	val selection by repo.getIntFlow(key).collectAsState(key.default)

	NumberPickerSettingContent(
		title, description, selection, range, modifier
	) {
		launchIO { repo.setInt(key, it) }
	}
}

@Composable
fun NumberPickerSettingContent(
	title: String,
	description: String,
	value: Int,
	range: IntRange,
	modifier: Modifier = Modifier,
	onValueChanged: (newValue: Int) -> Unit
) {
	var openDialog by remember { mutableStateOf(false) }

	GenericRightSettingLayout(title, description, modifier) {
		IconButton({
			openDialog = true
		}) {
			Text("$value", color = colorResource(R.color.colorAccent))
		}
	}

	if (openDialog)
		Dialog({ openDialog = false }) {
			NumberPickerSettingDialogContent(
				title,
				value,
				range,
				onValueChanged
			)
		}

}

@Composable
fun NumberPickerSettingDialogContent(
	title: String,
	value: Int,
	range: IntRange,
	onValueChanged: (newValue: Int) -> Unit,
) {
	Card(modifier = Modifier.fillMaxWidth()) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				title,
				modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
				textAlign = TextAlign.Center
			)
			NumberPicker(
				value = value,
				onValueChange = onValueChanged,
				range = range,
				dividersColor = colorResource(R.color.colorPrimary),
				modifier = Modifier
					.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
			)
		}
	}
}


@Preview
@Composable
fun PreviewNumberPickerSettingDialogContent() {
	Box(modifier = Modifier.size(300.dp, 500.dp)) {
		NumberPickerSettingDialogContent(
			value = 5,
			range = 0..10,
			title = "Test Dialog",
		) {
		}
	}
}

@Preview
@Composable
fun PreviewPickerSettingContent() {
	NumberPickerSettingContent(
		"A Number picker",
		"This is a number picker",
		2,
		range = 0..10
	) {

	}
}