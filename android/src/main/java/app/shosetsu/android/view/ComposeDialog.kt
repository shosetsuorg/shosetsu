package app.shosetsu.android.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner

@OptIn(ExperimentalAnimationGraphicsApi::class)
class ComposeDialog(context: Context, val owner: SavedStateRegistryOwner) : Dialog(context) {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.window?.decorView?.let { view ->
			ViewTreeLifecycleOwner.set(view, owner)
			ViewTreeSavedStateRegistryOwner.set(view, owner)
		}
	}
}
