package app.shosetsu.android.datasource.remote.impl.update

import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.common.domain.model.local.AppUpdateEntity

class PlayAppUpdateDataSource : IRemoteAppUpdateDataSource {
	override suspend fun loadAppUpdate(): AppUpdateEntity {
		TODO("Add play store update source")
	}

}