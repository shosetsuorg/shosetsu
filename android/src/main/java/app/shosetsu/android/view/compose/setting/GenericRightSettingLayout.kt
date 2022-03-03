package app.shosetsu.android.view.compose.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.sp
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme


@Composable
fun PreviewGenericRightSetting() {
	MdcTheme {
		GenericRightSettingLayout(
			"Test",
			"Description",
		) {

		}
	}
}

@Composable
fun GenericRightSettingLayout(
	title: String,
	description: String,
	modifier: Modifier = Modifier,
	right: @Composable () -> Unit
) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Column(
			Modifier.fillMaxWidth(0.6f)
		) {
			Text(title)
			Text(
				description,
				fontSize = dimensionResource(R.dimen.sub_text_size).value.sp,
				modifier = Modifier.alpha(0.7f)
			)
		}
		right()
	}
}