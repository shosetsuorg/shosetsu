package com.github.doomsdayrs.apps.shosetsu.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.attachRouter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.isOnline
import com.github.doomsdayrs.apps.shosetsu.backend.services.UpdateWorker
import com.github.doomsdayrs.apps.shosetsu.common.ext.requestPerms
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogsController
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.DownloadsController
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.ExtensionsController
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsController
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdatesController
import com.github.doomsdayrs.apps.shosetsu.view.base.SecondDrawerController
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IMainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

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
class MainActivity : AppCompatActivity(), KodeinAware {
	// The main router of the application
	private lateinit var router: Router

	private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

	override val kodein: Kodein by closestKodein()
	private val viewModel by instance<IMainViewModel>()

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
		setupView()
		setupMain(savedInstanceState)
		setupProcesses()
	}

	/**
	 * When the back button while drawer is open, close it.
	 */
	override fun onBackPressed() {
		val backStackSize = router.backstackSize
		when {
			drawer_layout.isDrawerOpen(GravityCompat.START) ->
				drawer_layout.closeDrawer(GravityCompat.START)
			backStackSize == 1 && router.getControllerWithTag("${R.id.nav_library}") == null ->
				setSelectedDrawerItem(R.id.nav_library)
			backStackSize == 1 || !router.handleBack() -> super.onBackPressed()
		}
	}

	// From tachiyomi
	private fun setSelectedDrawerItem(id: Int) {
		if (!isFinishing) {
			nav_view.setCheckedItem(id)
			nav_view.menu.performIdentifierAction(id, 0)
		}
	}

	private fun setupView() {
		//Sets the toolbar
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		actionBarDrawerToggle = ActionBarDrawerToggle(
				this,
				drawer_layout,
				toolbar,
				R.string.todo,
				R.string.todo
		)

		val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout.addDrawerListener(toggle)
		toggle.syncState()

		toolbar.setNavigationOnClickListener {
			if (router.backstackSize == 1)
				drawer_layout.openDrawer(GravityCompat.START)
			else onBackPressed()
		}
	}

	private fun setupMain(savedInstanceState: Bundle?) {
		// Navigation view
		//nav_view.setNavigationItemSelectedListener(NavigationSwapListener(this))
		nav_view.setNavigationItemSelectedListener {
			val id = it.itemId
			val currentRoot = router.backstack.firstOrNull()
			if (currentRoot?.tag()?.toIntOrNull() != id) {
				Log.d("Nav", "Selected $id")
				when (id) {
					R.id.nav_library -> setRoot(LibraryController(), R.id.nav_library)
					R.id.nav_catalogue -> setRoot(CatalogsController(), R.id.nav_catalogue)
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

		router.addChangeListener(object : ControllerChangeHandler.ControllerChangeListener {
			override fun onChangeStarted(
					to: Controller?,
					from: Controller?,
					isPush: Boolean,
					container: ViewGroup,
					handler: ControllerChangeHandler
			) {
				syncActivityViewWithController(to, from)
			}

			override fun onChangeCompleted(
					to: Controller?,
					from: Controller?,
					isPush: Boolean,
					container: ViewGroup,
					handler: ControllerChangeHandler
			) {
			}

		})

		syncActivityViewWithController(router.backstack.lastOrNull()?.controller)

		when (intent.action) {
			Intent.ACTION_USER_BACKGROUND -> {
				Log.i("MainActivity", "Updating novels")
				UpdateWorker.init(this)
				//TODO push to updates
			}
			Intent.ACTION_BOOT_COMPLETED -> {
				Log.i("MainActivity", "Bootup")
				if (isOnline) UpdateWorker.init(this)
			}
			else -> {
				if (!router.hasRootController()) {
					setSelectedDrawerItem(R.id.nav_library)
				}
			}
		}
	}

	private fun setRoot(controller: Controller, id: Int) {
		router.setRoot(controller.withFadeTransaction().tag(id.toString()))
	}

	private fun setupProcesses() {
		viewModel.startUpdateCheck()
		viewModel.startDownloadWorker()
		viewModel.startUpdateWorker()
	}

	fun transitionView(target: Controller) {
		router.pushController(target.withFadeTransaction())
	}

	@SuppressLint("ObjectAnimatorBinding")
	internal fun syncActivityViewWithController(to: Controller?, from: Controller? = null) {
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