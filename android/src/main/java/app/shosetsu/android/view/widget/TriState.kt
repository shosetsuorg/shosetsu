package app.shosetsu.android.view.widget

interface TriState {
	enum class State { IGNORED, CHECKED, UNCHECKED }

	var state: State

	/**
	 * Lambdas that are invoked when the state changes, receiving the new state
	 */
	val onStateChangeListeners: ArrayList<(State) -> Unit>

	/**
	 * Prevents this from going into an ignored state
	 */
	var skipIgnored: Boolean

	/**
	 * Cycles through the states of the tristate button
	 */
	fun cycleState() {
		state = when (state) {
			State.IGNORED -> State.CHECKED
			State.CHECKED -> State.UNCHECKED
			State.UNCHECKED -> if (skipIgnored) State.CHECKED else State.IGNORED
		}
	}
}