package app.shosetsu.android.common

class MissingFeatureException(feature: String) :
	Exception("This flavour does not have the feature: $feature")