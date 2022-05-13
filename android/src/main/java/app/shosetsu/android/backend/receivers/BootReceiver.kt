package app.shosetsu.android.backend.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import app.shosetsu.android.backend.workers.perodic.AppUpdateCheckCycleWorker
import app.shosetsu.android.backend.workers.perodic.NovelUpdateCycleWorker
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

/**
 * This class receives boot signals from android and sends it to services
 */
class BootReceiver : BroadcastReceiver() {
	/**
	 * Handles reception
	 */
	override fun onReceive(context: Context, intent: Intent) {
		logI("Received BOOT_COMPLETED signal")
		if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
			logE("Action did not match")
			return
		}
		// Starts perodic workers
		AutoStartUpdateWorker(context).invoke()
		AutoStartAppUpdateWorker(context).invoke()
	}

	internal class AutoStartUpdateWorker(val context: Context) : DIAware {
		override val di: DI by closestDI(context)
		private val iSettingsRepository: ISettingsRepository by instance()
		private val manager: NovelUpdateCycleWorker.Manager by instance()
		operator fun invoke() {
			launchIO {
				val b = iSettingsRepository.getBoolean(SettingKey.UpdateNovelsOnStartup)
				if (b && !manager.isRunning()) {
					Log.i(logID(), "Starting update worker on boot")
					manager.start()
				}
			}
		}
	}

	internal class AutoStartAppUpdateWorker(val context: Context) : DIAware {
		override val di: DI by closestDI(context)
		private val manager: AppUpdateCheckCycleWorker.Manager by instance()
		private val iSettingsRepository: ISettingsRepository by instance()
		operator fun invoke() {
			launchIO {
				val b = iSettingsRepository.getBoolean(SettingKey.AppUpdateOnStartup)
				if (b && !manager.isRunning()) {
					Log.i(logID(), "Starting app update worker on boot")
					manager.start()
				}
			}
		}
	}
}
