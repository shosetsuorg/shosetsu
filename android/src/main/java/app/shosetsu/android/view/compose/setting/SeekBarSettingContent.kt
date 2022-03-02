package app.shosetsu.android.view.compose.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.view.compose.DiscreteSlider
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.repositories.base.ISettingsRepository

@Composable
fun SliderSettingContent(
	title: String,
	description: String,
	valueRange: IntRange,
	parseValue: (Int) -> String,
	repo: ISettingsRepository,
	key: SettingKey<Int>,
	modifier: Modifier = Modifier,
) {
	val choice by repo.getIntFlow(key).collectAsState(key.default)

	GenericBottomSettingLayout(
		title,
		description,
		modifier,
	) {
		DiscreteSlider(
			choice,
			parseValue(choice),
			{
				launchIO { repo.setInt(key, it) }
			},
			valueRange
		)
	}
}

@Composable
fun FloatSliderSettingContent(
	title: String,
	description: String,
	valueRange: IntRange,
	parseValue: (Float) -> String,
	repo: ISettingsRepository,
	key: SettingKey<Float>,
	modifier: Modifier = Modifier,
) {
	val choice by repo.getFloatFlow(key).collectAsState(key.default)

	GenericBottomSettingLayout(
		title,
		description,
		modifier,
	) {
		DiscreteSlider(
			choice,
			parseValue(choice),
			{
				launchIO { repo.setFloat(key, it) }
			},
			valueRange
		)
	}
}
