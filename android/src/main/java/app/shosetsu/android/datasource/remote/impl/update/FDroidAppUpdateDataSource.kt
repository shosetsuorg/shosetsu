package app.shosetsu.android.datasource.remote.impl.update

import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.android.domain.model.local.AppUpdateEntity
import java.io.InputStream

class FDroidAppUpdateDataSource : IRemoteAppUpdateDataSource,
	IRemoteAppUpdateDataSource.Downloadable {

	override suspend fun loadAppUpdate(): AppUpdateEntity {
		TODO("Add F-DROID update source")
	}

	override suspend fun downloadAppUpdate(update: AppUpdateEntity): InputStream {
		TODO("Add F-DROID update source")
	}

}