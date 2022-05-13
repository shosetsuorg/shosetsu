package app.shosetsu.android.common

import app.shosetsu.android.domain.model.local.GenericExtensionEntity
import app.shosetsu.lib.KOTLIN_LIB_VERSION
import app.shosetsu.lib.Version

/**
 * Thrown when [extension] is not compatible with this version of Shosetsu
 */
class IncompatibleExtensionException(
	extension: GenericExtensionEntity,
	extensionMadeFor: Version
) :
	Exception(
		"""
			Extension (${extension.id}) from Repository (${extension.repoID}) is not compatible with this version of Shosetsu. 
			Current lib version $KOTLIN_LIB_VERSION, extension expects $extensionMadeFor
		""".trimMargin()
	)