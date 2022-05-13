package app.shosetsu.android.view.widget

import app.shosetsu.android.common.enums.TriStateState

interface TriState {


	var state: TriStateState

	/**
	 * Lambdas that are invoked when the state changes, receiving the new state
	 */
	val onStateChangeListeners: ArrayList<(TriStateState) -> Unit>

	/**
	 * Prevents this from going into an ignored state
	 */
	var skipIgnored: Boolean

	/**
	 * Cycles through the states of the tristate button
	 */
	fun cycleState() {
		state = state.cycle(skipIgnored)
	}
}