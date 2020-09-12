package app.shosetsu.android.activity

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_QUERY
import app.shosetsu.android.common.consts.ShortCuts
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.common.ext.requestPerms
import app.shosetsu.android.common.ext.toast
import app.shosetsu.android.common.ext.withFadeTransaction
import app.shosetsu.android.ui.catalogue.CatalogsController
import app.shosetsu.android.ui.downloads.DownloadsController
import app.shosetsu.android.ui.extensions.ExtensionsController
import app.shosetsu.android.ui.library.LibraryController
import app.shosetsu.android.ui.search.SearchController
import app.shosetsu.android.ui.settings.SettingsController
import app.shosetsu.android.ui.updates.UpdatesController
import app.shosetsu.android.view.base.CollapsedToolBarController
import app.shosetsu.android.view.base.FABController
import app.shosetsu.android.view.base.LiftOnScrollToolBarController
import app.shosetsu.android.view.base.SecondDrawerController
import app.shosetsu.android.viewmodel.abstracted.IMainViewModel
import com.bluelinelabs.conductor.Conductor.attachRouter
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.apps.shosetsu.R
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

	private val broadcastReceiver by lazy {
		object : BroadcastReceiver() {
			override fun onReceive(context: Context?, intent: Intent?) {
				intent?.let {
					handleIntentAction(it)
				} ?: Log.e(logID(), "Null intent recieved")
			}
		}
	}

	override fun onDestroy() {
		unregisterReceiver(broadcastReceiver)
		super.onDestroy()
	}

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
		registerReceiver(broadcastReceiver, IntentFilter().apply {
			addAction(ShortCuts.ACTION_OPEN_UPDATES)
			addAction(ShortCuts.ACTION_OPEN_LIBRARY)
			addAction(ShortCuts.ACTION_OPEN_CATALOGUE)
			addAction(ShortCuts.ACTION_OPEN_SEARCH)
		})
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

		router = attachRouter(this, controller_container, savedInstanceState)

		router.addChangeListener(object : ControllerChangeHandler.ControllerChangeListener {
			override fun onChangeStarted(
					to: Controller?,
					from: Controller?,
					isPush: Boolean,
					container: ViewGroup,
					handler: ControllerChangeHandler,
			) {
				syncActivityViewWithController(to, from)
			}

			override fun onChangeCompleted(
					to: Controller?,
					from: Controller?,
					isPush: Boolean,
					container: ViewGroup,
					handler: ControllerChangeHandler,
			) {
			}

		})

		syncActivityViewWithController(router.backstack.lastOrNull()?.controller)

		handleIntentAction(intent)
	}

	internal fun handleIntentAction(intent: Intent) {
		Log.d(logID(), "Intent received was ${intent.action}")
		when (intent.action) {
			ShortCuts.ACTION_OPEN_CATALOGUE -> setSelectedDrawerItem(R.id.nav_catalogue)
			ShortCuts.ACTION_OPEN_UPDATES -> setSelectedDrawerItem(R.id.nav_updater)
			ShortCuts.ACTION_OPEN_LIBRARY -> setSelectedDrawerItem(R.id.nav_library)
			Intent.ACTION_SEARCH -> {
				if (!router.hasRootController()) setSelectedDrawerItem(R.id.nav_library)
				router.pushController(SearchController(bundleOf(
						BUNDLE_QUERY to (intent.getStringExtra(SearchManager.QUERY) ?: "")
				)).withFadeTransaction())
			}
			ShortCuts.ACTION_OPEN_SEARCH -> {
				if (!router.hasRootController()) setSelectedDrawerItem(R.id.nav_library)
				router.pushController(SearchController(bundleOf(
						BUNDLE_QUERY to (intent.getStringExtra(SearchManager.QUERY) ?: "")
				)).withFadeTransaction())
			}
			else -> if (!router.hasRootController()) setSelectedDrawerItem(R.id.nav_library)
		}
	}

	private fun setRoot(controller: Controller, id: Int) {
		router.setRoot(controller.withFadeTransaction().tag(id.toString()))
	}

	private fun setupProcesses() {
		viewModel.startUpdateCheck().observe(this) { result ->
			when (result) {
				is HResult.Loading -> {
				}
				is HResult.Error -> {
					applicationContext.toast("$result")
				}
				is HResult.Empty -> {
					applicationContext.toast(R.string.app_update_unavaliable)
				}
				is HResult.Success -> {
					val update = result.data
					AlertDialog.Builder(this).apply {
						setTitle(R.string.update_app_now_question)
						setMessage("${update.version}\t${update.versionCode}\n" + update.notes.joinToString("\n"))
						setPositiveButton(R.string.update) { it, _ ->
							viewModel.share(
									update.url,
									if (update.versionCode == -1) "Discord" else "Github"
							)
							it.dismiss()
						}
						setNegativeButton(R.string.update_not_interested) { it, _ ->
							it.dismiss()
						}
						setOnDismissListener {
							it.dismiss()
						}
					}.let {
						app.shosetsu.android.common.ext.launchUI { it.show() }
					}
				}
			}
		}
		viewModel.startDownloadWorker()
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

		if (from is FABController) {
			from.hideFAB(fab)
			from.resetFAB(fab)
		}

		if (to is FABController) {
			to.acceptFAB(fab)
			to.setFABIcon(fab)
			to.manipulateFAB(fab)
			to.showFAB(fab)
		}

		when (to) {
			is CollapsedToolBarController -> {
				elevatedAppBarLayout.drop()
			}
			is LiftOnScrollToolBarController -> {
				elevatedAppBarLayout.elevate(true)
			}
			else -> {
				elevatedAppBarLayout.elevate(false)
			}
		}
	}

}