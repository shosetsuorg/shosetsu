package app.shosetsu.android.view.compose.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.shosetsu.android.view.compose.ShosetsuCompose
import com.github.doomsdayrs.apps.shosetsu.R


@Composable
fun PreviewGenericRightSetting() {
	ShosetsuCompose {
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
	onClick: (() -> Unit)? = null,
	right: @Composable () -> Unit
) {
	Row(
		modifier = modifier then Modifier
			.defaultMinSize(minHeight = 56.dp)
			.let {
				if (onClick != null)
					it.clickable(onClick = onClick)
				else it
			}
			.padding(horizontal = 16.dp),
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