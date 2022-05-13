package app.shosetsu.android.view.compose.setting

import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import com.github.doomsdayrs.apps.shosetsu.R

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
	onCheckChange: (newValue: Boolean) -> Unit
) {
	GenericRightSettingLayout(title, description, modifier) {
		Switch(
			isChecked,
			onCheckChange,
			colors = SwitchDefaults.colors(
				checkedThumbColor = colorResource(R.color.colorAccent)
			)
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
) {
	val value by repo.getBooleanFlow(key).collectAsState(key.default)
	SwitchSettingContent(
		title, description, value, modifier
	) {
		launchIO { repo.setBoolean(key, it) }
	}
}