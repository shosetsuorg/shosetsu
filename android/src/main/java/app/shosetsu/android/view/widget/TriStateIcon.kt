package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.github.doomsdayrs.apps.shosetsu.R

class TriStateIcon @JvmOverloads constructor(
	context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), TriState {
	@DrawableRes
	var checkedRes: Int

	@DrawableRes
	var uncheckedRes: Int

	@DrawableRes
	var ignoredRes: Int

	init {
		context.theme.obtainStyledAttributes(attrs, R.styleable.TriStateIcon, defStyleAttr, 0)
			.apply {
				try {
					checkedRes = getResourceId(R.styleable.TriState_button_checked, 0)
					uncheckedRes = getResourceId(R.styleable.TriState_button_unchecked, 0)
					ignoredRes = getResourceId(R.styleable.TriState_button_ignored, 0)
					state =
						TriState.State.values()[getResourceId(R.styleable.TriState_state, 0)]
				} finally {
					recycle()
				}
			}
	}

	override var state: TriState.State = TriState.State.IGNORED
		set(value) {
			if (value == TriState.State.IGNORED) {
				if (ignoredRes != 0) {
					setImageResource(ignoredRes)
				} else visibility = INVISIBLE
				return
			}
			// If checked / unchecked, set visibility to visible
			isVisible = true

			if (checkedRes != 0 && uncheckedRes != 0)
				setImageResource(if (value == TriState.State.CHECKED) checkedRes else uncheckedRes)

			if (onStateChangeListeners != null)
				onStateChangeListeners.forEach { listener ->
					listener(value)
				}

			field = value
		}

	override val onStateChangeListeners = ArrayList<(TriState.State) -> Unit>()

	override var skipIgnored: Boolean = false
}