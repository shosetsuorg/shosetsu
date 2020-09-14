package app.shosetsu.android.activity

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import app.shosetsu.android.common.utils.collapse
import app.shosetsu.android.common.utils.expand
import app.shosetsu.android.ui.browse.BrowseController
import app.shosetsu.android.ui.catalogue.CatalogsController
import app.shosetsu.android.ui.downloads.DownloadsController
import app.shosetsu.android.ui.extensions.ExtensionsController
import app.shosetsu.android.ui.library.LibraryController
import app.shosetsu.android.ui.more.MoreController
import app.shosetsu.android.ui.search.SearchController
import app.shosetsu.android.ui.settings.SettingsController
import app.shosetsu.android.ui.updates.UpdatesController
import app.shosetsu.android.view.base.*
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
	private var registered = false

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
		if (registered)
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
			Log.i(logID(), "Broadcasting intent ${intent.action}")
			sendBroadcast(Intent(intent.action))
			finish()
			return
		}
		registerReceiver(broadcastReceiver, IntentFilter().apply {
			addAction(ShortCuts.ACTION_OPEN_UPDATES)
			addAction(ShortCuts.ACTION_OPEN_LIBRARY)
			addAction(ShortCuts.ACTION_OPEN_CATALOGUE)
			addAction(ShortCuts.ACTION_OPEN_SEARCH)
		})
		registered = true
		setContentView(R.layout.activity_main)
		setupView()
		setupRouter(savedInstanceState)
		handleIntentAction(intent)
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
			if (viewModel.navigationStyle() == 0) {
				bottomNavigationView.selectedItemId = id
				bottomNavigationView.menu.performIdentifierAction(id, 0)
			} else {
				nav_view.setCheckedItem(id)
				nav_view.menu.performIdentifierAction(id, 0)
			}
		}
	}

	private fun setupView() {
		//Sets the toolbar
		setSupportActionBar(toolbar)

		toolbar.setNavigationOnClickListener {
			if (router.backstackSize == 1 && viewModel.navigationStyle() == 1)
				drawer_layout.openDrawer(GravityCompat.START)
			else onBackPressed()
		}

		if (viewModel.navigationStyle() == 0) {
			bottomNavigationView.visibility = VISIBLE
			nav_view.visibility = GONE
			setupBottomNavigationDrawer()
		} else {
			nav_view.visibility = VISIBLE
			bottomNavigationView.visibility = GONE
			setupNavigationDrawer()
		}
	}

	private fun setupNavigationDrawer() {
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


		// Navigation view
		//nav_view.setNavigationItemSelectedListener(NavigationSwapListener(this))
		nav_view.setNavigationItemSelectedListener {
			val id = it.itemId
			val currentRoot = router.backstack.firstOrNull()
			if (currentRoot?.tag()?.toIntOrNull() != id) handleNavigationSelected(id)
			drawer_layout.closeDrawer(GravityCompat.START)
			return@setNavigationItemSelectedListener true
		}
	}

	private fun setupBottomNavigationDrawer() {
		drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, nav_view)

		bottomNavigationView.setOnNavigationItemSelectedListener {
			val id = it.itemId
			val currentRoot = router.backstack.firstOrNull()
			if (currentRoot?.tag()?.toIntOrNull() != id) handleNavigationSelected(id)
			return@setOnNavigationItemSelectedListener true
		}
	}

	private fun handleNavigationSelected(id: Int) {
		Log.d("Nav", "Selected $id")
		when (id) {
			R.id.nav_library -> setRoot(LibraryController(), R.id.nav_library)
			R.id.nav_catalogue -> setRoot(CatalogsController(), R.id.nav_catalogue)
			R.id.nav_extensions -> setRoot(ExtensionsController(), R.id.nav_extensions)
			R.id.nav_settings -> transitionView(SettingsController())
			R.id.nav_downloads -> transitionView(DownloadsController())
			R.id.nav_browse -> setRoot(BrowseController(), R.id.nav_browse)
			R.id.nav_more -> setRoot(MoreController(), R.id.nav_more)
			R.id.nav_updates -> if (viewModel.navigationStyle() == 1)
				transitionView(UpdatesController())
			else setRoot(UpdatesController(), R.id.nav_updates)
		}
	}

	private fun setupRouter(savedInstanceState: Bundle?) {
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
	}

	internal fun handleIntentAction(intent: Intent) {
		Log.d(logID(), "Intent received was ${intent.action}")
		when (intent.action) {
			ShortCuts.ACTION_OPEN_CATALOGUE -> if (viewModel.navigationStyle() == 1)
				setSelectedDrawerItem(R.id.nav_catalogue)
			else setSelectedDrawerItem(R.id.nav_browse)
			ShortCuts.ACTION_OPEN_UPDATES -> setSelectedDrawerItem(R.id.nav_updates)
			ShortCuts.ACTION_OPEN_LIBRARY -> setSelectedDrawerItem(R.id.nav_library)
			Intent.ACTION_SEARCH -> {
				if (!router.hasRootController()) setSelectedDrawerItem(R.id.nav_library)

				transitionView(SearchController(bundleOf(
						BUNDLE_QUERY to (intent.getStringExtra(SearchManager.QUERY) ?: "")
				)))
			}
			ShortCuts.ACTION_OPEN_SEARCH -> {
				if (!router.hasRootController()) setSelectedDrawerItem(R.id.nav_library)

				transitionView(SearchController(bundleOf(
						BUNDLE_QUERY to (intent.getStringExtra(SearchManager.QUERY) ?: "")
				)))
			}
			Intent.ACTION_MAIN -> {
				if (!router.hasRootController()) {
					setSelectedDrawerItem(R.id.nav_library)
				} else {
					Log.e(logID(), "Router has a root controller")
				}
			}
			else -> if (!router.hasRootController()) {
				setSelectedDrawerItem(R.id.nav_library)
			} else {
				Log.e(logID(), "Router has a root controller")
			}
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

	private fun transitionView(target: Controller) {
		router.pushController(target.withFadeTransaction())
	}

	@SuppressLint("ObjectAnimatorBinding")
	internal fun syncActivityViewWithController(to: Controller?, from: Controller? = null) {
		val showHamburger = router.backstackSize == 1 // Show hamburg means this is home

		Log.d(logID(), "Show hamburger?: $showHamburger")

		if (showHamburger) {


			// Shows navigation
			if (viewModel.navigationStyle() == 1) {
				supportActionBar?.setDisplayHomeAsUpEnabled(true)
				actionBarDrawerToggle.isDrawerIndicatorEnabled = true
				drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, nav_view)
			} else {
				supportActionBar?.setDisplayHomeAsUpEnabled(false)
				bottomNavigationView.visibility = VISIBLE
			}
		} else {

			// Hides navigation
			if (viewModel.navigationStyle() == 1) {
				supportActionBar?.setDisplayHomeAsUpEnabled(false)
				actionBarDrawerToggle.isDrawerIndicatorEnabled = false
				drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, nav_view)
			} else {
				supportActionBar?.setDisplayHomeAsUpEnabled(true)
				bottomNavigationView.visibility = GONE
			}
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

		if (to is PushCapableController) to.acceptPushing { transitionView(it) }

		if (from is TabbedController) {
			tabLayout.removeAllTabs()
			tabLayout.clearOnTabSelectedListeners()
		}

		if (to is TabbedController) to.acceptTabLayout(tabLayout)
		if (from is TabbedController && to !is TabbedController) tabLayout.collapse()
		if (from !is TabbedController && to is TabbedController) tabLayout.expand()

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

	private fun toggleTabLayout() {
		@Suppress("CheckedExceptionsKotlin")
		val animator: Animation = AnimationUtils.loadAnimation(
				tabLayout.context,
				if (tabLayout.visibility == VISIBLE)
					R.anim.slide_up
				else R.anim.slide_down
		).apply {
			duration = 250
		}
		tabLayout.startAnimation(animator)
		tabLayout.post {
			tabLayout.visibility = if (tabLayout.visibility == VISIBLE) GONE else VISIBLE
		}
	}

}