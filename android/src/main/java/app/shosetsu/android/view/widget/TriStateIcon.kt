package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import app.shosetsu.android.common.enums.TriStateState
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
					checkedRes = getResourceId(R.styleable.TriStateIcon_icon_checked, -1)
					uncheckedRes = getResourceId(R.styleable.TriStateIcon_icon_unchecked, -1)
					ignoredRes = getResourceId(R.styleable.TriStateIcon_icon_ignored, -1)
					state =
						TriStateState.values()[getResourceId(
							R.styleable.TriStateIcon_icon_state,
							0
						)]
				} finally {
					recycle()
				}
			}
	}

	override var state: TriStateState = TriStateState.IGNORED
		set(value) {
			if (value == TriStateState.IGNORED) {
				if (ignoredRes != -1) {
					setImageResource(ignoredRes)
				} else visibility = INVISIBLE
				return
			}
			// If checked / unchecked, set visibility to visible
			isVisible = true

			if (checkedRes != -1 && uncheckedRes != -1)
				setImageResource(if (value == TriStateState.CHECKED) checkedRes else uncheckedRes)

			if (onStateChangeListeners != null)
				onStateChangeListeners.forEach { listener ->
					listener(value)
				}

			field = value
		}

	override val onStateChangeListeners = ArrayList<(TriStateState) -> Unit>()

	override var skipIgnored: Boolean = false
}