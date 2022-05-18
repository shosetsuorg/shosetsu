package app.shosetsu.android.view.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.consts.SELECTED_STROKE_WIDTH
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.accompanist.placeholder.material.placeholder
import com.google.android.material.composethemeadapter.MdcTheme

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Shosetsu
 *
 * @since 14 / 05 / 2022
 * @author Doomsdayrs
 */

@Composable
fun PlaceholderNovelCardNormalContent() {
	NovelCardNormalContent(
		"",
		"",
		onClick = {},
		onLongClick = {},
		isPlaceholder = true
	)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NovelCardNormalContent(
	title: String,
	imageURL: String,
	onClick: () -> Unit,
	onLongClick: () -> Unit,
	overlay: @Composable (BoxScope.() -> Unit)? = null,
	isPlaceholder: Boolean = false,
	isSelected: Boolean = false,
) {
	Card(
		modifier = Modifier
			.combinedClickable(
				onClick = onClick,
				onLongClick = onLongClick
			)
			.padding(4.dp),
		border = if (isSelected) {
			BorderStroke(
				width = (SELECTED_STROKE_WIDTH / 2).dp,
				color = MaterialTheme.colors.primary
			)
		} else {
			null
		}
	) {
		Box {
			AsyncImage(
				ImageRequest.Builder(LocalContext.current)
					.data(imageURL)
					.placeholder(R.drawable.animated_refresh)
					.error(R.drawable.broken_image)
					.build(),
				stringResource(R.string.controller_novel_info_image),
				modifier = Modifier
					.fillMaxSize()
					.aspectRatio(.75f)
					.clickable(onClick = onClick)
					.placeholder(visible = isPlaceholder),
				contentScale = ContentScale.Crop
			)

			Box(
				modifier = Modifier
					.aspectRatio(.75f)
					.fillMaxSize()
					.drawWithCache {
						onDrawWithContent {

							drawRect(
								brush = Brush.linearGradient(
									listOf(
										Color.Transparent,
										Color.Black.copy(alpha = .75f),
									),
									Offset(0.0f, 0.0f),
									Offset(0.0f, Float.POSITIVE_INFINITY),
									TileMode.Clamp
								)
							)
						}
					}
					.alpha(if (isPlaceholder) 0.0f else 1.0f)
			)
			Text(
				title,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.placeholder(visible = isPlaceholder),
				textAlign = TextAlign.Center,
				color = Color.White,
			)
			if (overlay != null)
				overlay()
		}
	}
}


@Preview
@Composable
fun PreviewNovelCardCompressedContent() {
	MdcTheme {
		NovelCardCompressedContent(
			"Test",
			"",
			onClick = {},
			onLongClick = {}
		)
	}
}

@Composable
fun PlaceholderNovelCardCompressedContent() {
	NovelCardCompressedContent(
		"",
		"",
		onClick = {},
		onLongClick = {},
		isPlaceholder = true
	)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NovelCardCompressedContent(
	title: String,
	imageURL: String,
	onClick: () -> Unit,
	onLongClick: () -> Unit,
	overlay: @Composable (RowScope.() -> Unit)? = null,
	isPlaceholder: Boolean = false,
	isSelected: Boolean = false,
) {
	Card(
		modifier = Modifier
			.combinedClickable(
				onClick = onClick,
				onLongClick = onLongClick
			)
			.padding(4.dp),
		border = if (isSelected) {
			BorderStroke(
				width = (SELECTED_STROKE_WIDTH / 2).dp,
				color = MaterialTheme.colors.primary
			)
		} else {
			null
		}
	) {
		Box {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.fillMaxSize(.70f)
				) {
					AsyncImage(
						ImageRequest.Builder(LocalContext.current)
							.data(imageURL)
							.placeholder(R.drawable.animated_refresh)
							.error(R.drawable.broken_image)
							.build(),
						stringResource(R.string.controller_novel_info_image),
						modifier = Modifier
							.width(64.dp)
							.aspectRatio(1.0f)
							.clickable(onClick = onClick)
							.placeholder(visible = isPlaceholder),
						contentScale = ContentScale.Crop
					)

					Text(
						title,
						modifier = Modifier
							.placeholder(visible = isPlaceholder)
							.padding(start = 8.dp)
							.fillMaxSize()
					)
				}

				if (overlay != null)
					overlay()
			}
		}
	}
}