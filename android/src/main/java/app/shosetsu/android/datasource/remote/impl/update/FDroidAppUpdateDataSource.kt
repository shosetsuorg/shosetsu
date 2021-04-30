package app.shosetsu.android.datasource.remote.impl.update

import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.common.domain.model.local.AppUpdateEntity
import app.shosetsu.common.dto.HResult

class FDroidAppUpdateDataSource : IRemoteAppUpdateDataSource,
	IRemoteAppUpdateDataSource.Downloadable {

	override suspend fun loadAppUpdate(): HResult<AppUpdateEntity> {
		TODO("Add F-DROID update source")
	}

	override suspend fun downloadAppUpdate(update: AppUpdateEntity): HResult<ByteArray> {
		TODO("Add F-DROID update source")
	}

}