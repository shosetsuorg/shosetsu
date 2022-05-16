package app.shosetsu.android.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner

class ComposeDialog(
	context: Context,
	val owner: LifecycleOwner,
	val stateOwner: SavedStateRegistryOwner,
) : Dialog(context) {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.window?.decorView?.let { view ->
			ViewTreeLifecycleOwner.set(view, owner)
			ViewTreeSavedStateRegistryOwner.set(view, stateOwner)
		}
	}
}
