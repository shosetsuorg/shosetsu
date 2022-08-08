package app.shosetsu.android.view.compose.setting

import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.repository.base.ISettingsRepository

@Preview
@Composable
fun PreviewSwitchSettingContent() {
	SwitchSettingContent("A Switch", "This is a switch", true) {}
}

@Composable
fun SwitchSettingContent(
	title: String,
	description: String,
	isChecked: Boolean,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	onCheckChange: (newValue: Boolean) -> Unit
) {
	GenericRightSettingLayout(title, description, modifier, onClick = { onCheckChange(!isChecked) }) {
		Switch(
			isChecked,
			null,
			enabled = enabled
		)
	}
}

@Composable
fun SwitchSettingContent(
	title: String,
	description: String,
	repo: ISettingsRepository,
	key: SettingKey<Boolean>,
	modifier: Modifier = Modifier,
	enabled: Boolean = true
) {
	val value by repo.getBooleanFlow(key).collectAsState(key.default)
	SwitchSettingContent(
		title, description, value, modifier, enabled
	) {
		launchIO { repo.setBoolean(key, it) }
	}
}