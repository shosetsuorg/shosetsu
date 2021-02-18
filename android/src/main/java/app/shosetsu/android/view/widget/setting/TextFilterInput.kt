package app.shosetsu.android.view.widget.setting

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import app.shosetsu.lib.Filter

class TextFilterInput @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	override val filterID: Int = -1
) : FilterSettingWidget<String>, AppCompatEditText(context, attrs) {
	override var result: String
		get() = super.getText().toString()
		set(value) = super.setText(value)

	constructor(
		filter: Filter.Text,
		context: Context,
		attrs: AttributeSet? = null
	) : this(
		context,
		attrs,
		filterID = filter.id
	) {
		hint = filter.name
		result = filter.state
	}
}