package app.shosetsu.android.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.google.android.material.bottomsheet.BottomSheetDialog

class ComposeDialog(
	context: Context,
	val owner: LifecycleOwner,
	val stateOwner: SavedStateRegistryOwner,
) : Dialog(context) {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.window?.decorView?.let { view ->
			ViewTreeLifecycleOwner.set(view, owner)
			view.setViewTreeSavedStateRegistryOwner(stateOwner)
		}
	}
}

class ComposeBottomSheetDialog(
	context: Context,
	val owner: LifecycleOwner,
	val stateOwner: SavedStateRegistryOwner,
) : BottomSheetDialog(context) {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.window?.decorView?.let { view ->
			ViewTreeLifecycleOwner.set(view, owner)
			view.setViewTreeSavedStateRegistryOwner(stateOwner)
		}
	}
}
