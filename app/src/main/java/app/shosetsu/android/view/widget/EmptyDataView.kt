package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.android.synthetic.main.common_empty_view.view.*
import kotlin.random.Random

class EmptyDataView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
		RelativeLayout(context, attrs) {

	init {
		inflate(context, R.layout.common_empty_view, this)
	}

	/**
	 * Hide the information view
	 */
	fun hide() {
		this.isVisible = false
	}

	/**
	 * Show the information view
	 * @param textResource text of information view
	 */
	fun show(@StringRes textResource: Int, actions: List<Action>? = null) {
		show(context.getString(textResource), actions)
	}

	fun show(message: String, actions: List<Action>? = null) {
		text_face.text = getRandomErrorFace()
		text_label.text = message

		actions_container.removeAllViews()
		if (!actions.isNullOrEmpty()) {
			actions.forEach {
				val button = AppCompatButton(context).apply {
					layoutParams = LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT
					)

					setText(it.resId)
					setOnClickListener(it.listener)
				}

				actions_container.addView(button)
			}
		}

		this.isVisible = true
	}

	companion object {
		private val ERROR_FACES = listOf(
				"(･o･;)",
				"Σ(ಠ_ಠ)",
				"ಥ_ಥ",
				"(˘･_･˘)",
				"(；￣Д￣)",
				"(･Д･。",
				"(┳Д┳)",
				"(☍﹏⁰)｡",
				"(;Д;)",
				"╥﹏╥",
				"(இ﹏இ`｡)",
				"༼ ༎ຶ ෴ ༎ຶ༽",
				"(⋟﹏⋞)",
				"(ノAヽ)",
				"(つ﹏⊂)",
				"（πーπ）",
				"(⊙_◎)",
				"(゜ロ゜)",
				"（￣□￣；）",
				"(_□_；)",
				"(;Ⅲ□Ⅲ;)",
				"( p_q)",
				"Σ(￣ロ￣lll)",
				"ヽ(´Д`;)ﾉ",
				"╮(╯_╰)╭",
				"┐(´д`)┌",
				"-`д´-",
				"(´-ι_-｀)",
				"(・ω・｀)………..",
				"〴⋋_⋌〵",
				"（＞μ＜＃）"
		)

		fun getRandomErrorFace(): String {
			return ERROR_FACES[Random.nextInt(ERROR_FACES.size)]
		}
	}

	data class Action(
			@StringRes val resId: Int,
			val listener: OnClickListener
	)
}
