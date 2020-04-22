package com.github.doomsdayrs.apps.shosetsu.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import app.shosetsu.lib.ShosetsuLib
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.attachRouter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.CookieJarSync
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.backend.UpdateManager.init
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.secondDrawer.SecondDrawerController
import com.github.doomsdayrs.apps.shosetsu.backend.services.DownloadService
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CataloguesController
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.DownloadsController
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.ExtensionsController
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsController
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdatesController
import com.github.doomsdayrs.apps.shosetsu.variables.ext.requestPerms
import com.github.doomsdayrs.apps.shosetsu.variables.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Formatters
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.AppUpdaterUtils.UpdateListener
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.luaj.vm2.lib.jse.JsePlatform

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */
/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO Inform users to refresh their libraries
class MainActivity : AppCompatActivity(), Supporter {
	private lateinit var router: Router
	private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

	/**
	 * Main activity
	 *
	 * @param savedInstanceState savedData from destruction
	 */
	override fun onCreate(savedInstanceState: Bundle?) {
		this.requestPerms()
		super.onCreate(savedInstanceState)

		// Do not let the launcher create a new activity http://stackoverflow.com/questions/16283079
		if (!isTaskRoot) {
			finish()
			return
		}

		setContentView(R.layout.activity_main)

		appUpdate()

		//Sets the toolbar
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.todo, R.string.todo)


		// Navigation view
		//nav_view.setNavigationItemSelectedListener(NavigationSwapListener(this))
		nav_view.setNavigationItemSelectedListener {
			val id = it.itemId
			val currentRoot = router.backstack.firstOrNull()
			if (currentRoot?.tag()?.toIntOrNull() != id) {
				Log.d("Nav", "Selected $id")
				when (id) {
					R.id.nav_library -> setRoot(LibraryController(), R.id.nav_library)
					R.id.nav_catalogue -> setRoot(CataloguesController(), R.id.nav_catalogue)
					R.id.nav_extensions -> setRoot(ExtensionsController(), R.id.nav_extensions)
					R.id.nav_settings -> router.pushController(SettingsController().withFadeTransaction())
					R.id.nav_downloads -> router.pushController(DownloadsController().withFadeTransaction())
					R.id.nav_updater -> router.pushController(UpdatesController().withFadeTransaction())
				}
			}
			drawer_layout.closeDrawer(GravityCompat.START)
			return@setNavigationItemSelectedListener true
		}

		router = attachRouter(fragment_container, savedInstanceState)


		val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout.addDrawerListener(toggle)
		toggle.syncState()


		ShosetsuLib.httpClient = OkHttpClient.Builder()
				.addInterceptor {
					val r = it.request()
					Log.i("ShosetsuLib", r.url.toUrl().toExternalForm())
					return@addInterceptor it.proceed(r)
				}
				.cookieJar(CookieJarSync())
				.build()

		ShosetsuLib.libLoader = libLoader@{ name ->
			Log.i("LibraryLoaderSync", "Loading:\t$name")
			val libraryFile = FormatterUtils.makeLibraryFile(this, name)
			if (!libraryFile.exists()) {
				Log.e("LibraryLoaderSync", "$name does not exist")
				return@libLoader null
			}
			Log.d("LibraryLoaderSync", libraryFile.absolutePath)
			val script = JsePlatform.standardGlobals()
			script.load(ShosetsuLib())
			val l = try {
				script.load(libraryFile.readText())
			} catch (e: Error) {
				throw e
			}
			return@libLoader l.call()
		}

		GlobalScope.launch {
			Formatters.load(this@MainActivity)
		}

		toolbar.setNavigationOnClickListener { if (router.backstackSize == 1) drawer_layout.openDrawer(GravityCompat.START) else onBackPressed() }

		router.addChangeListener(object : ControllerChangeHandler.ControllerChangeListener {
			override fun onChangeStarted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {
				syncActivityViewWithController(to, from)
			}

			override fun onChangeCompleted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {
			}

		})

		syncActivityViewWithController(router.backstack.lastOrNull()?.controller)
		DownloadService.start(this)
		when (intent.action) {
			Intent.ACTION_USER_BACKGROUND -> {
				Log.i("MainActivity", "Updating novels")
				init(this)
				//TODO push to updates
			}
			Intent.ACTION_BOOT_COMPLETED -> {
				Log.i("MainActivity", "Bootup")
				if (Utilities.isOnline)
					init(this)
			}
			else -> {

				if (!router.hasRootController()) {
					setSelectedDrawerItem(R.id.nav_library)
				}
				//Prevent the frag from changing on rotation
				/*if (savedInstanceState == null) {
					supportFragmentManager
							.beginTransaction()
							.replace(R.id.fragment_container, libraryFragment).commit()
					nav_view.setCheckedItem(R.id.nav_library)
				}
				*/
			}
		}
	}

	/**
	 * When the back button while drawer is open, close it.
	 */
	override fun onBackPressed() {
		val backStackSize = router.backstackSize
		when {
			drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)

			backStackSize == 1 && router.getControllerWithTag("${R.id.nav_library}") == null -> setSelectedDrawerItem(R.id.nav_library)

			backStackSize == 1 || !router.handleBack() -> super.onBackPressed()
		}
	}

	override fun setTitle(name: String?) {
		if (supportActionBar != null) supportActionBar!!.title = name
	}

	// From tachiyomi
	private fun setSelectedDrawerItem(id: Int) {
		if (!isFinishing) {
			nav_view.setCheckedItem(id)
			nav_view.menu.performIdentifierAction(id, 0)
		}
	}

	private fun setRoot(controller: Controller, id: Int) {
		router.setRoot(controller.withFadeTransaction().tag(id.toString()))
	}

	private fun appUpdate() {
		val appUpdater = AppUpdater(this)
				.setUpdateFrom(UpdateFrom.XML)
				.setUpdateXML("https://raw.githubusercontent.com/Doomsdayrs/shosetsu/master/app/update.xml")
				.setDisplay(Display.DIALOG)
				.setTitleOnUpdateAvailable(getString(R.string.app_update_available))
				.setContentOnUpdateAvailable(getString(R.string.check_out_latest_app))
				.setTitleOnUpdateNotAvailable(getString(R.string.app_update_unavaliable))
				.setContentOnUpdateNotAvailable(getString(R.string.check_updates_later))
				.setButtonUpdate(getString(R.string.update_app_now_question)) //    .setButtonUpdateClickListener(...)
				.setButtonDismiss(getString(R.string.update_dismiss)) //       .setButtonDismissClickListener(...)
				.setButtonDoNotShowAgain(getString(R.string.update_not_interested)) //     .setButtonDoNotShowAgainClickListener(...)
				.setIcon(R.drawable.ic_system_update_alt_24dp)
				.setCancelable(true)
				.showEvery(5)
		appUpdater.start()
	}

	fun transitionView(target: Controller) {
		router.pushController(target.withFadeTransaction())
	}

	@Suppress("unused")
	private fun updateUtils() {
		val appUpdaterUtils = AppUpdaterUtils(this)
				.setUpdateFrom(UpdateFrom.XML).setUpdateXML("https://raw.githubusercontent.com/Doomsdayrs/shosetsu/master/app/update.xml")
				.withListener(object : UpdateListener {
					override fun onSuccess(update: Update, isUpdateAvailable: Boolean) {
						Log.d("Latest Version", isUpdateAvailable.toString())
						Log.d("Latest Version", update.latestVersion)
						Log.d("Latest Version", update.latestVersionCode.toString())
					}

					override fun onFailed(error: AppUpdaterError) {
						Log.d("AppUpdater Error", "Something went wrong")
					}
				})
		appUpdaterUtils.start()
	}

	@SuppressLint("ObjectAnimatorBinding")
	private fun syncActivityViewWithController(to: Controller?, from: Controller? = null) {
		val showHamburger = router.backstackSize == 1
		if (showHamburger) {
			supportActionBar?.setDisplayHomeAsUpEnabled(true)
			actionBarDrawerToggle.isDrawerIndicatorEnabled = true
			drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, nav_view)
		} else {
			supportActionBar?.setDisplayHomeAsUpEnabled(false)
			actionBarDrawerToggle.isDrawerIndicatorEnabled = false
			drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, nav_view)
		}
		if (from is SecondDrawerController) {
			second_nav_view.removeAllViews()
			drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, second_nav_view)
		}
		if (to is SecondDrawerController) {
			drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, second_nav_view)
			to.createDrawer(second_nav_view, drawer_layout)
		}

	}
}