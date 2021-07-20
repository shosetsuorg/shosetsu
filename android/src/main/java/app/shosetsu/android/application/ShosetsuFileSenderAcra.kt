package app.shosetsu.android.application

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.

/**
 * Shosetsu
 *
 * @since 19 / 07 / 2021
 * @author Doomsdayrs
 */
class ShosetsuFileSenderAcra(context: Context) : ReportSender {
	private val di by closestDI(context)
	private val fileDataSource by di.instance<IFileCrashDataSource>()

	override fun requiresForeground(): Boolean = false

	override fun send(context: Context, errorContent: CrashReportData) {

		fileDataSource.writeCrash(errorContent.toJSON()).handle(
			onError = {
				logE("Failed to write crash log")
			}
		) {
			logI("Successfully wrote crash to file")
		}
	}


	@AutoService(ReportSenderFactory::class)
	class Factory : ReportSenderFactory {
		override fun create(context: Context, config: CoreConfiguration): ReportSender =
			ShosetsuFileSenderAcra(context)

		override fun enabled(config: CoreConfiguration): Boolean = true


	}
}
 */