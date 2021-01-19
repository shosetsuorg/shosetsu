package app.shosetsu.android.backend.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import app.shosetsu.android.backend.workers.perodic.AppUpdateCheckCycleWorker
import app.shosetsu.android.backend.workers.perodic.NovelUpdateCycleWorker
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logID
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

/**
 * This class receives boot signals from android and sends it to services
 */
class BootReceiver : BroadcastReceiver() {
	/**
	 * Handles reception
	 */
	override fun onReceive(context: Context, intent: Intent) {
		Log.i(logID(), "Received BOOT_COMPLETED signal")
		// Starts perodic workers
		AutoStartUpdateWorker(context).invoke()
		AutoStartAppUpdateWorker(context).invoke()
	}

	internal class AutoStartUpdateWorker(val context: Context) : KodeinAware {
		override val kodein: Kodein by kodein(context)
		private val iSettingsRepository: ISettingsRepository by instance()
		private val manager: NovelUpdateCycleWorker.Manager by instance()
		operator fun invoke() {
			launchIO {
				val b = iSettingsRepository.getBoolean(SettingKey.UpdateOnStartup)
				if (b is HResult.Success && b.data && !manager.isRunning()) {
					Log.i(logID(), "Starting update worker on boot")
					manager.start()
				}
			}
		}
	}

	internal class AutoStartAppUpdateWorker(val context: Context) : KodeinAware {
		override val kodein: Kodein by kodein(context)
		private val manager: AppUpdateCheckCycleWorker.Manager by instance()
		private val iSettingsRepository: ISettingsRepository by instance()
		operator fun invoke() {
			launchIO {
				val b = iSettingsRepository.getBoolean(SettingKey.AppUpdateOnStartup)
				if (b is HResult.Success && b.data && !manager.isRunning()) {
					Log.i(logID(), "Starting app update worker on boot")
					manager.start()
				}
			}
		}
	}
}
