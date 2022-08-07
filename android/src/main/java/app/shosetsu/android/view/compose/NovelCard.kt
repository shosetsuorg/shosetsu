package app.shosetsu.android.view.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.accompanist.placeholder.material.placeholder

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

const val coverRatio = 12.8F / 18.2F

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

@Preview
@Composable
fun PreviewNovelCardNormalContent() {
	ShosetsuCompose {
		NovelCardNormalContent(
			"Test",
			"",
			onClick = {},
			onLongClick = {}
		)
	}
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
	isBookmarked: Boolean = false
) {
	Card(
		modifier = Modifier
			.selectedOutline(isSelected)
			.combinedClickable(
				onClick = onClick,
				onLongClick = onLongClick
			)
			.alpha(if (isBookmarked) .5f else 1f),
	) {
		Box {
			SubcomposeAsyncImage(
				imageURL,
				stringResource(R.string.controller_novel_info_image),
				modifier = Modifier
					.fillMaxSize()
					.aspectRatio(coverRatio)
					.placeholder(visible = isPlaceholder),
				contentScale = ContentScale.Crop,
				error = {
					ImageLoadingError()
				}
			)

			Box(
				modifier = Modifier
					.aspectRatio(coverRatio)
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
					.placeholder(visible = isPlaceholder)
					.padding(4.dp),
				textAlign = TextAlign.Center,
				color = Color.White,
				overflow = TextOverflow.Ellipsis,
				maxLines = 3,
				fontSize = 14.sp
			)
			if (overlay != null)
				overlay()
		}
	}
}

@Composable
fun PlaceholderNovelCardCozyContent() {
	NovelCardCozyContent(
		"",
		"",
		onClick = {},
		onLongClick = {},
		isPlaceholder = true
	)
}

@Preview
@Composable
fun PreviewNovelCardCozyContent() {
	ShosetsuCompose {
		NovelCardCozyContent(
			"Test",
			"",
			onClick = {},
			onLongClick = {}
		)
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NovelCardCozyContent(
	title: String,
	imageURL: String,
	onClick: () -> Unit,
	onLongClick: () -> Unit,
	overlay: @Composable (BoxScope.() -> Unit)? = null,
	isPlaceholder: Boolean = false,
	isSelected: Boolean = false,
	isBookmarked: Boolean = false
) {
	Column(
		modifier = Modifier
			.selectedOutline(isSelected)
			.alpha(if (isBookmarked) .5f else 1f)
	) {
		Card(
			modifier = Modifier
				.combinedClickable(
					onClick = onClick,
					onLongClick = onLongClick
				),
		) {
			Box {
				SubcomposeAsyncImage(
					ImageRequest.Builder(LocalContext.current)
						.data(imageURL)
						.crossfade(true)
						.build(),
					stringResource(R.string.controller_novel_info_image),
					modifier = Modifier
						.fillMaxSize()
						.aspectRatio(coverRatio)
						.placeholder(visible = isPlaceholder),
					contentScale = ContentScale.Crop,
					error = {
						ImageLoadingError()
					},
					loading = {
						Box(Modifier.placeholder(true))
					}
				)

				if (overlay != null)
					overlay()

			}
		}

		Text(
			title,
			modifier = Modifier
				.placeholder(visible = isPlaceholder)
				.padding(4.dp),
			textAlign = TextAlign.Center,
			overflow = TextOverflow.Ellipsis,
			maxLines = 3,
			fontSize = 14.sp
		)
	}
}


@Preview
@Composable
fun PreviewNovelCardCompressedContent() {
	ShosetsuCompose {
		NovelCardCompressedContent(
			"Test",
			"",
			onClick = {},
			onLongClick = {}
		)
	}
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
	isBookmarked: Boolean = false
) {
	Card(
		modifier = Modifier
			.selectedOutline(isSelected)
			.combinedClickable(
				onClick = onClick,
				onLongClick = onLongClick
			)
			.alpha(if (isBookmarked) .5f else 1f),
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
					SubcomposeAsyncImage(
						ImageRequest.Builder(LocalContext.current)
							.data(imageURL)
							.crossfade(true)
							.build(),
						stringResource(R.string.controller_novel_info_image),
						modifier = Modifier
							.width(64.dp)
							.aspectRatio(1.0f),
						contentScale = ContentScale.Crop,
						error = {
							ImageLoadingError()
						},
						loading = {
							Box(Modifier.placeholder(true))
						}
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