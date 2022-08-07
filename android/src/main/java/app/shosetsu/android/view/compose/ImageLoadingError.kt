package app.shosetsu.android.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.doomsdayrs.apps.shosetsu.R

@Composable
fun ImageLoadingError(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier then Modifier
            .fillMaxSize()
            .background(Color(0x1F888888)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(R.drawable.broken_image),
            contentDescription = null,
            tint = Color(0x1F888888),
            modifier = Modifier.size(24.dp)
        )
    }
}