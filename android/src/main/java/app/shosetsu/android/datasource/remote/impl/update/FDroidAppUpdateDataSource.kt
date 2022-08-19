package app.shosetsu.android.datasource.remote.impl.update

import app.shosetsu.android.common.EmptyResponseBodyException
import app.shosetsu.android.common.ext.quickie
import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.android.domain.model.local.AppUpdateEntity
import app.shosetsu.lib.exceptions.HTTPException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.OkHttpClient
import java.io.IOException
import java.io.InputStream

/**
 * Load app updates from F-Droid
 */
class FDroidAppUpdateDataSource(
	private val okHttpClient: OkHttpClient
) : IRemoteAppUpdateDataSource,
	IRemoteAppUpdateDataSource.Downloadable {
	companion object {
		private const val FDROID_UPDATE_URL =
			"https://f-droid.org/api/v1/packages/app.shosetsu.android"

		private const val FDROID_DOWNLOAD_URL = "https://f-droid.org/repo/app.shosetsu.android_"

		private val json = Json {
			encodeDefaults = true
		}
	}

	@Serializable
	data class PackagesInfo(
		val packageName: String = "",
		val suggestedVersionCode: Int = -1,
		val packages: List<PackageData> = emptyList(),
		val error: String? = null
	)

	@Serializable
	data class PackageData(
		val versionName: String,
		val versionCode: Int
	)

	@Throws(HTTPException::class, EmptyResponseBodyException::class, IOException::class)
	@OptIn(ExperimentalSerializationApi::class)
	override suspend fun loadAppUpdate(): AppUpdateEntity {
		okHttpClient.quickie(FDROID_UPDATE_URL).use { response ->
			if (response.isSuccessful) {
				return response.body?.use { responseBody ->
					responseBody.byteStream().use { responseStream ->
						val info = json.decodeFromStream<PackagesInfo>(responseStream)
						if (info.error != null)
							throw EmptyResponseBodyException(info.error)

						var packageData = info.packages.firstOrNull {
							it.versionCode == info.suggestedVersionCode
						}

						if (packageData == null)
							packageData = info.packages.first()

						AppUpdateEntity(
							packageData.versionName,
							packageData.versionCode,
							url = FDROID_DOWNLOAD_URL + "${packageData.versionCode}.apk",
							notes = emptyList()
						)
					}
				} ?: throw EmptyResponseBodyException(FDROID_UPDATE_URL)
			}
			throw HTTPException(response.code)
		}
	}

	@Throws(EmptyResponseBodyException::class, HTTPException::class, IOException::class)
	override suspend fun downloadAppUpdate(update: AppUpdateEntity): InputStream {
		okHttpClient.quickie(update.url).let { response ->
			if (response.isSuccessful) {
				return response.body?.byteStream()
					?: throw EmptyResponseBodyException(update.url)
			} else throw HTTPException(response.code)
		}
	}

}