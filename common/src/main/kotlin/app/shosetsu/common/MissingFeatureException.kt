package app.shosetsu.common

class MissingFeatureException(feature: String) :
	Exception("This flavour does not have the feature: $feature")