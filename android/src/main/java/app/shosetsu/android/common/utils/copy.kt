package app.shosetsu.android.common.utils

inline fun <reified K, reified V> Map<K, V>.copy(): HashMap<K, V> {
	val map = HashMap<K, V>()
	onEach { (k, v) ->
		map[k] = v
	}
	return map
}