package app.shosetsu.android.ui.reader.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.shosetsu.android.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.android.view.compose.DiscreteSlider
import app.shosetsu.android.view.compose.setting.GenericBottomSettingLayout
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChapterReaderBottomSheetContent(
	scaffoldState: BottomSheetScaffoldState,
	isTTSCapable: Boolean,
	isTTSPlaying: Boolean,
	isBookmarked: Boolean,
	isRotationLocked: Boolean,
	setting: NovelReaderSettingEntity,

	toggleRotationLock: () -> Unit,
	toggleBookmark: () -> Unit,
	exit: () -> Unit,
	onPlayTTS: () -> Unit,
	onStopTTS: () -> Unit,
	updateSetting: (NovelReaderSettingEntity) -> Unit,
	lowerSheet: LazyListScope.() -> Unit,
	toggleFocus: () -> Unit,
	onShowNavigation: (() -> Unit)?
) {
	val coroutineScope = rememberCoroutineScope()
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(56.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(onClick = exit) {
			Icon(Icons.Filled.ArrowBack, null)
		}

		Row {
			IconButton(onClick = toggleFocus) {
				Icon(
					painterResource(R.drawable.ic_baseline_visibility_off_24),
					null
				)
			}
			IconButton(onClick = toggleBookmark) {
				Icon(
					painterResource(
						if (!isBookmarked) {
							R.drawable.empty_bookmark
						} else {
							R.drawable.filled_bookmark
						}
					),
					null
				)
			}

			IconButton(onClick = toggleRotationLock) {
				Icon(
					painterResource(
						if (!isRotationLocked)
							R.drawable.ic_baseline_screen_rotation_24
						else R.drawable.ic_baseline_screen_lock_rotation_24
					),
					null
				)
			}

			if (isTTSCapable && !isTTSPlaying)
				IconButton(onClick = onPlayTTS) {
					Icon(
						painterResource(R.drawable.ic_baseline_audiotrack_24),
						null
					)
				}

			if (isTTSPlaying)
				IconButton(onClick = onStopTTS) {
					Icon(
						painterResource(R.drawable.ic_baseline_stop_circle_24),
						null
					)
				}

			if (onShowNavigation != null) {
				IconButton(onClick = onShowNavigation) {
					Icon(
						painterResource(R.drawable.unfold_less),
						null
					)
				}
			}
		}

		IconButton(onClick = {
			coroutineScope.launch {
				if (!scaffoldState.bottomSheetState.isExpanded)
					scaffoldState.bottomSheetState.expand()
				else scaffoldState.bottomSheetState.collapse()
			}
		}) {
			Icon(
				if (scaffoldState.bottomSheetState.isExpanded) {
					painterResource(R.drawable.expand_more)
				} else {
					painterResource(R.drawable.expand_less)
				},
				null
			)
		}
	}

	LazyColumn(
		contentPadding = PaddingValues(vertical = 16.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		item {
			GenericBottomSettingLayout(
				stringResource(R.string.paragraph_spacing),
				"",
			) {
				DiscreteSlider(
					setting.paragraphSpacingSize,
					"${setting.paragraphSpacingSize}",
					{ it, a ->
						updateSetting(
							setting.copy(
								paragraphSpacingSize = if (!a)
									it.roundToInt().toFloat()
								else it
							)
						)
					},
					0..10,
				)
			}

		}

		item {
			GenericBottomSettingLayout(
				stringResource(R.string.paragraph_indent),
				"",
			) {
				DiscreteSlider(
					setting.paragraphIndentSize,
					"${setting.paragraphIndentSize}",
					{ it, _ ->
						updateSetting(setting.copy(paragraphIndentSize = it))
					},
					0..10,
				)
			}
		}
		lowerSheet()
	}
}