package app.shosetsu.android.common.enums

enum class ProductFlavors(val key: String) {
	PLAY_STORE("playstore"),
	F_DROID("fdroid"),
	UP_TO_DOWN("uptodown"),
	STANDARD("standard");

	companion object {
		fun fromKey(key: String) = values().find { it.key == key }!!
	}
}