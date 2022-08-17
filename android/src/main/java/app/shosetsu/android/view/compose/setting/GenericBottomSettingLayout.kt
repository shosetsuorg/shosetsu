package app.shosetsu.android.view.compose.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.shosetsu.android.R
import app.shosetsu.android.view.compose.ShosetsuCompose

@Preview
@Composable
fun PreviewGenericBottomSetting() {
	ShosetsuCompose {
		GenericBottomSettingLayout(
			"Test",
			"Description"
		) {

		}
	}
}

@Composable
fun GenericBottomSettingLayout(
	title: String,
	description: String,
	modifier: Modifier = Modifier,
	bottom: @Composable () -> Unit
) {
	Column(
		modifier = modifier then Modifier
			.defaultMinSize(minHeight = 56.dp)
			.padding(horizontal = 16.dp),
	) {
		Column {
			Text(title)
			Text(
				description,
				fontSize = dimensionResource(R.dimen.sub_text_size).value.sp,
				modifier = Modifier.alpha(0.7f)
			)
		}
		bottom()
	}
}