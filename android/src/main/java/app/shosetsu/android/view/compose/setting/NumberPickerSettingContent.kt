package app.shosetsu.android.view.compose.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import com.chargemap.compose.numberpicker.NumberPicker

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

	GenericRightSettingLayout(title, description, modifier, onClick = { openDialog = !openDialog }) {
		IconButton({
			openDialog = true
		}) {
			Text("$value", color = MaterialTheme.colors.secondary)
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
				dividersColor = MaterialTheme.colors.secondary,
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