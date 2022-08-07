package app.shosetsu.android.view.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

fun Modifier.selectedOutline(isSelected: Boolean) = composed {
    val secondary = MaterialTheme.colors.secondary
    if (isSelected) {
        drawBehind {
            val additional = 24.dp.value
            val offset = additional / 2
            val height = size.height + additional
            val width = size.width + additional
            drawRoundRect(
                color = secondary,
                topLeft = Offset(-offset, -offset),
                size = Size(width, height),
                cornerRadius = CornerRadius(offset),
            )
        }
    } else {
        this
    }
}