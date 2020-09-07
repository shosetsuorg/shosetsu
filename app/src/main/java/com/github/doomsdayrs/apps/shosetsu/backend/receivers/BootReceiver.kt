package com.github.doomsdayrs.apps.shosetsu.backend.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.backend.workers.AppUpdateWorker
import com.github.doomsdayrs.apps.shosetsu.backend.workers.UpdateWorker
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
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
		AutoStartUpdateWorker(context).invoke()
		AutoStartAppUpdateWorker(context).invoke()
	}

	internal class AutoStartUpdateWorker(val context: Context) : KodeinAware {
		override val kodein: Kodein by kodein(context)
		private val shosetsuSettings: ShosetsuSettings by instance()
		private val manager: UpdateWorker.Manager by instance()

		operator fun invoke() {
			if (shosetsuSettings.updateOnStartup && !manager.isRunning()) {
				Log.i(logID(), "Starting update worker on boot")
				manager.start()
			}
		}
	}

	internal class AutoStartAppUpdateWorker(val context: Context) : KodeinAware {
		override val kodein: Kodein by kodein(context)
		private val shosetsuSettings: ShosetsuSettings by instance()
		private val manager: AppUpdateWorker.Manager by instance()

		operator fun invoke() {
			if (shosetsuSettings.appUpdateOnStartup && !manager.isRunning()) {
				Log.i(logID(), "Starting app update worker on boot")
				manager.start()
			}
		}
	}
}
