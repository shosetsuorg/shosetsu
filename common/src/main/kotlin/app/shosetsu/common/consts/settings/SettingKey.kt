package app.shosetsu.common.consts.settings

import app.shosetsu.common.enums.MarkingTypes.ONVIEW

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
 */

/**
 * shosetsu
 * 23 / 06 / 2020
 */

sealed class SettingKey<T : Any>(val name: String, val default: T) {

	object ReaderTheme : SettingKey<Int>("readerTheme", -1)

	object FirstTime : SettingKey<Boolean>("first_time", true)


	// How things look in Reader
	object ReaderUserThemes : SettingKey<Set<String>>("readerThemes", setOf())


	object ReaderTextSize : SettingKey<Float>("readerTextSize", 14f)
	object ReaderParagraphSpacing : SettingKey<Int>("readerParagraphSpacing", 1)
	object ReaderIndentSize : SettingKey<Int>("readerIndentSize", 1)

	//- How things act in Reader
	object ReaderIsTapToScroll : SettingKey<Boolean>("tapToScroll", false)
	object ReaderVolumeScroll : SettingKey<Boolean>("volumeToScroll", false)


	object ReaderIsInvertedSwipe : SettingKey<Boolean>("invertedSwipe", false)
	object ReadingMarkingType : SettingKey<String>("readingMarkingType", ONVIEW.name)

	/**
	 * Should the application convert string returns from an extension to an Html page
	 */
	object ReaderStringToHtml : SettingKey<Boolean>("convertStringToHtml", false)

	/**
	 * User customization for CSS in html reader
	 */
	object ReaderHtmlCss : SettingKey<String>("readerHtmlCss", "")

	/**
	 * Instead of vertically moving between chapters, do a horizontal move
	 */
	object ReaderHorizontalPageSwap : SettingKey<Boolean>("readerHorizontalPaging", false)

	/**
	 * The reader smoothly moves between chapters
	 */
	object ReaderContinuousScroll : SettingKey<Boolean>("readerContinuousScroll", false)


	//- Some things
	object ChaptersResumeFirstUnread : SettingKey<Boolean>(
		"readerResumeFirstUnread",
		false
	)

	// Download options
	object IsDownloadPaused : SettingKey<Boolean>("isDownloadPaused", false)


	/**
	 * Which chapter to delete after reading
	 * If -1, then does nothing
	 * If 0, then deletes the read chapter
	 * If 1+, deletes the chapter of READ CHAPTER - [deletePreviousChapter]
	 */
	object DeleteReadChapter : SettingKey<Int>("deleteReadChapter", -1)

	object DownloadOnLowStorage : SettingKey<Boolean>("downloadNotLowStorage", false)
	object DownloadOnLowBattery : SettingKey<Boolean>("downloadNotLowBattery", false)
	object DownloadOnMeteredConnection : SettingKey<Boolean>(
		"downloadNotMetered",
		false
	)

	object DownloadOnlyWhenIdle : SettingKey<Boolean>("downloadIdle", false)

	/** Bookmark a novel if it is not bookmarked when a chapter from it is downloaded */
	object BookmarkOnDownload : SettingKey<Boolean>("bookmarkOnDownload", false)

	// Update options
	object IsDownloadOnUpdate : SettingKey<Boolean>("isDownloadOnUpdate", false)
	object OnlyUpdateOngoing : SettingKey<Boolean>("onlyUpdateOngoing", false)
	object UpdateOnStartup : SettingKey<Boolean>("updateOnStartup", true)
	object UpdateCycle : SettingKey<Int>("updateCycle", 1)
	object UpdateOnLowStorage : SettingKey<Boolean>("updateLowStorage", false)
	object UpdateOnLowBattery : SettingKey<Boolean>("updateLowBattery", false)
	object UpdateOnMeteredConnection : SettingKey<Boolean>("updateMetered", false)
	object UpdateOnlyWhenIdle : SettingKey<Boolean>("updateIdle", false)
	object UpdateNotificationStyle : SettingKey<Boolean>("updateNotificationStyle", false)


	// App Update Options
	object AppUpdateOnStartup : SettingKey<Boolean>("appUpdateOnStartup", true)
	object AppUpdateOnMeteredConnection : SettingKey<Boolean>(
		"appUpdateMetered",
		false
	)

	object AppUpdateOnlyWhenIdle : SettingKey<Boolean>("appUpdateIdle", false)
	object AppUpdateCycle : SettingKey<Int>("appUpdateCycle", 1)


	// View options
	object ChapterColumnsInPortait : SettingKey<Int>("columnsInNovelsViewP", 3)
	object ChapterColumnsInLandscape : SettingKey<Int>("columnsInNovelsViewH", 6)
	object SelectedNovelCardType : SettingKey<Int>("novelCardType", 0)
	object NavStyle : SettingKey<Int>("navigationStyle", 0)

	// Backup Options
	object BackupChapters : SettingKey<Boolean>("backupChapters", true)
	object BackupSettings : SettingKey<Boolean>("backupSettings", false)
	object BackupCycle : SettingKey<Int>("backupCycle", 3)

	object BackupOnLowStorage : SettingKey<Boolean>("backupLowStorage", false)
	object BackupOnLowBattery : SettingKey<Boolean>("backupLowBattery", false)
	object BackupOnlyWhenIdle : SettingKey<Boolean>("backupIdle", false)

	// Download Options
	object CustomExportDirectory : SettingKey<String>("downloadDirectory", "")

	/** How many threads to download at the same time */
	object DownloadThreadPool : SettingKey<Int>("downloadThreads", 1)

	/** How many extension threads allowed to work in the pool */
	object DownloadExtThreads : SettingKey<Int>("downloadExtThreads", 1)

	/** If the reader can mark a read chapter as reading when its opened / scrolled */
	object ReaderMarkReadAsReading : SettingKey<Boolean>(
		"readerMarkReadAsReading",
		false
	)

	object AppTheme : SettingKey<Int>("selectedAppTheme", 0)

	companion object {
		private val map: Map<String, SettingKey<*>> by lazy {
			SettingKey::class.sealedSubclasses
				.map { it.objectInstance }
				.filterIsInstance<SettingKey<*>>()
				.associateBy { it.name }
		}

		fun valueOf(key: String): SettingKey<*> =
			map[key] ?: error("SettingKey: Cannot find $key")
	}
}