package app.shosetsu.android.datasource.remote.impl.update

import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.common.domain.model.local.AppUpdateEntity
import app.shosetsu.common.dto.HResult

class PlayAppUpdateDataSource : IRemoteAppUpdateDataSource {
	override suspend fun loadAppUpdate(): HResult<AppUpdateEntity> {
		TODO("Add play store update source")
	}

}