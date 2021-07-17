package app.shosetsu.android.activity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.DownloadManager.*
import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.drawerlayout.widget.DrawerLayout
import app.shosetsu.android.common.consts.*
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_QUERY
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.common.utils.collapse
import app.shosetsu.android.common.utils.expand
import app.shosetsu.android.ui.browse.BrowseController
import app.shosetsu.android.ui.library.LibraryController
import app.shosetsu.android.ui.more.MoreController
import app.shosetsu.android.ui.search.SearchController
import app.shosetsu.android.ui.updates.UpdatesController
import app.shosetsu.android.view.controller.*
import app.shosetsu.android.view.controller.base.*
import app.shosetsu.android.viewmodel.abstracted.AMainViewModel
import app.shosetsu.common.dto.handle
import app.shosetsu.common.enums.AppThemes.*
import com.bluelinelabs.conductor.Conductor.attachRouter
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import java.util.*


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
class MainActivity : AppCompatActivity(), DIAware,
	ControllerChangeHandler.ControllerChangeListener {
	private lateinit var binding: ActivityMainBinding

	private var registered = false

	// The main router of the application
	private lateinit var router: Router

	private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

	private val downloadManager by lazy { getSystemService<DownloadManager>()!! }
	override val di: DI by closestDI()
	private val viewModel: AMainViewModel by viewModel()

	private val broadcastReceiver by lazy {
		object : BroadcastReceiver() {
			override fun onReceive(context: Context?, intent: Intent?) {
				intent?.let {
					handleIntentAction(it)
				} ?: logE("Null intent recieved")
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
		viewModel.navigationStyle
		viewModel.appThemeLiveData.observe(this) {
			logI("Setting theme to $it")
			when (it) {
				FOLLOW_SYSTEM -> {
					delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
				}
				LIGHT -> {
					delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
				}
				DARK -> {
					delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
				}
				AMOLED -> {
					// TODO Implement amoled mode
					delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
				}
				else -> logE("Null theme received")
			}
		}
		this.requestPerms()
		super.onCreate(savedInstanceState)
		// Do not let the launcher create a new activity http://stackoverflow.com/questions/16283079
		if (!isTaskRoot) {
			logI("Broadcasting intent ${intent.action}")
			sendBroadcast(Intent(intent.action))
			finish()
			return
		}
		registerReceiver(broadcastReceiver, IntentFilter().apply {
			addAction(ACTION_OPEN_UPDATES)
			addAction(ACTION_OPEN_LIBRARY)
			addAction(ACTION_OPEN_CATALOGUE)
			addAction(ACTION_OPEN_SEARCH)
			addAction(ACTION_OPEN_APP_UPDATE)
			addAction(ACTION_DOWNLOAD_COMPLETE)
		})
		registered = true
		setContentView(ActivityMainBinding.inflate(layoutInflater).also { binding = it }.root)

		setupView()
		setupRouter(savedInstanceState)
		handleIntentAction(intent)
		setupProcesses()
	}

	private var inProtectingBack = false

	private fun protectedBackWait() {
		launchIO {
			inProtectingBack = true
			val snackBar =
				makeSnackBar(R.string.double_back_message, Snackbar.LENGTH_INDEFINITE).apply {
					setOnDismissed { snackbar, event ->
						inProtectingBack = false
					}
				}
			snackBar.show()
			delay(2000)
			snackBar.dismiss()
		}
	}

	private fun shouldProtectBack(): Boolean =
		router.backstackSize == 1 &&
				router.getControllerWithTag("${R.id.nav_library}") != null &&
				viewModel.requireDoubleBackToExit &&
				!inProtectingBack

	/**
	 * When the back button while drawer is open, close it.
	 */
	override fun onBackPressed() {
		val backStackSize = router.backstackSize
		logD("Back stack size: $backStackSize")
		when {
			binding.drawerLayout.isDrawerOpen(GravityCompat.START) ->
				binding.drawerLayout.closeDrawer(GravityCompat.START)

			backStackSize == 1 && router.getControllerWithTag("${R.id.nav_library}") == null ->
				setSelectedDrawerItem(R.id.nav_library)

			shouldProtectBack() -> protectedBackWait()

			backStackSize == 1 || !router.handleBack() -> super.onBackPressed()
		}
	}

	// From tachiyomi
	private fun setSelectedDrawerItem(id: Int) {
		if (!isFinishing) {
			if (viewModel.navigationStyle == 0) {
				binding.bottomNavigationView.selectedItemId = id
				binding.bottomNavigationView.menu.performIdentifierAction(id, 0)
			} else {
				binding.navView.setCheckedItem(id)
				binding.navView.menu.performIdentifierAction(id, 0)
			}
		}
	}

	private fun setupView() {
		//Sets the toolbar
		setSupportActionBar(binding.toolbar)

		binding.toolbar.setNavigationOnClickListener {
			if (router.backstackSize == 1) {
				if (viewModel.navigationStyle == 1) {
					binding.drawerLayout.openDrawer(GravityCompat.START)
				}
			} else onBackPressed()
		}

		if (viewModel.navigationStyle == 0) {
			binding.bottomNavigationView.visibility = VISIBLE
			binding.navView.visibility = GONE
			setupBottomNavigationDrawer()
		} else {
			binding.navView.visibility = VISIBLE
			binding.bottomNavigationView.visibility = GONE
			setupNavigationDrawer()
		}
	}

	private fun setupNavigationDrawer() {
		logV("Setting up legacy navigation")
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		actionBarDrawerToggle = ActionBarDrawerToggle(
			this,
			binding.drawerLayout,
			binding.toolbar,
			R.string.todo,
			R.string.todo
		)

		val toggle = ActionBarDrawerToggle(
			this, binding.drawerLayout, binding.toolbar,
			R.string.navigation_drawer_open, R.string.navigation_drawer_close
		)
		binding.drawerLayout.addDrawerListener(toggle)
		toggle.syncState()


		// Navigation view
		//nav_view.setNavigationItemSelectedListener(NavigationSwapListener(this))
		binding.navView.setNavigationItemSelectedListener {
			val id = it.itemId
			val currentRoot = router.backstack.firstOrNull()
			if (currentRoot?.tag()?.toIntOrNull() != id) handleNavigationSelected(id)
			binding.drawerLayout.closeDrawer(GravityCompat.START)
			return@setNavigationItemSelectedListener true
		}
	}

	private fun setupBottomNavigationDrawer() {
		logV("Setting up modern navigation")
		binding.drawerLayout.setDrawerLockMode(
			DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
			binding.navView
		)

		binding.bottomNavigationView.setOnNavigationItemSelectedListener {
			val id = it.itemId
			val currentRoot = router.backstack.firstOrNull()
			if (currentRoot?.tag()?.toIntOrNull() != id) handleNavigationSelected(id)
			return@setOnNavigationItemSelectedListener true
		}
	}

	private fun handleNavigationSelected(id: Int) {
		when (id) {
			// Bottom nav
			R.id.nav_library -> setRoot(LibraryController(), R.id.nav_library)
			R.id.nav_browse -> setRoot(BrowseController(), R.id.nav_browse)
			R.id.nav_more -> setRoot(MoreController(), R.id.nav_more)
			// Shared
			R.id.nav_updates -> setRoot(UpdatesController(), R.id.nav_updates)
		}
	}

	private fun setupRouter(savedInstanceState: Bundle?) {
		router = attachRouter(this, binding.controllerContainer, savedInstanceState)
		router.addChangeListener(this)
		syncActivityViewWithController(router.backstack.lastOrNull()?.controller)
	}

	private fun actionMain() {
		if (!router.hasRootController()) {
			setSelectedDrawerItem(R.id.nav_library)
		} else {
			logE("Router has a root controller")
		}
	}

	internal fun handleIntentAction(intent: Intent) {
		logD("Intent received was ${intent.action}")
		when (intent.action) {
			ACTION_OPEN_CATALOGUE -> setSelectedDrawerItem(R.id.nav_browse)
			ACTION_OPEN_UPDATES -> setSelectedDrawerItem(R.id.nav_updates)
			ACTION_OPEN_LIBRARY -> setSelectedDrawerItem(R.id.nav_library)
			ACTION_SEARCH -> {
				if (!router.hasRootController()) setSelectedDrawerItem(R.id.nav_library)

				router.shosetsuPush(
					SearchController(
						bundleOf(
							BUNDLE_QUERY to (intent.getStringExtra(SearchManager.QUERY) ?: "")
						)
					)
				)
			}
			ACTION_OPEN_SEARCH -> {
				if (!router.hasRootController()) setSelectedDrawerItem(R.id.nav_library)

				router.shosetsuPush(
					SearchController(
						bundleOf(
							BUNDLE_QUERY to (intent.getStringExtra(SearchManager.QUERY) ?: "")
						)
					)
				)
			}
			ACTION_OPEN_APP_UPDATE -> {
				viewModel.handleAppUpdate()
				actionMain()
			}
			ACTION_MAIN -> actionMain()
			else -> if (!router.hasRootController()) {
				setSelectedDrawerItem(R.id.nav_library)
			} else {
				logE("Router has a root controller")
			}
		}
	}

	private fun setRoot(controller: Controller, id: Int) {
		router.setRoot(controller.withFadeTransaction().tag(id.toString()))
	}

	private fun setupProcesses() {
		viewModel.startUpdateCheck().observe(this) { result ->
			result.handle(
				onError = {
					applicationContext.toast("$result")
				}
			) {
				val update = it
				AlertDialog.Builder(this).apply {
					setTitle(R.string.update_app_now_question)
					setMessage(
						"${update.version}\t${update.versionCode}\n" + update.notes.joinToString(
							"\n"
						)
					)
					setPositiveButton(R.string.update) { it, _ ->
						viewModel.handleAppUpdate()
						it.dismiss()
					}
					setNegativeButton(R.string.update_not_interested) { it, _ ->
						it.dismiss()
					}
					setOnDismissListener { dialogInterface ->
						dialogInterface.dismiss()
					}
				}.let {
					launchUI { it.show() }
				}
			}
		}
	}

	@Deprecated(
		"",
		ReplaceWith("router.shosetsuPush(target)", "app.shosetsu.android.common.ext.shosetsuPush")
	)
	private fun transitionView(target: Controller) {
		router.pushController(target.withFadeTransaction())
	}

	private val holdingAtBottom = hashMapOf<View, AppBarLayout.OnOffsetChangedListener>()

	fun holdAtBottom(view: View) {
		AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
			val maxAbsOffset = appBarLayout.measuredHeight - binding.tabLayout.measuredHeight
			view.translationY = -maxAbsOffset - verticalOffset.toFloat() + appBarLayout.marginTop
		}.let {
			binding.elevatedAppBarLayout.addOnOffsetChangedListener(it)
			holdingAtBottom[view] = it
		}
	}

	fun removeHoldAtBottom(view: View) {
		binding.elevatedAppBarLayout.removeOnOffsetChangedListener(holdingAtBottom.remove(view))
	}

	@SuppressLint("ObjectAnimatorBinding")
	internal fun syncActivityViewWithController(to: Controller?, from: Controller? = null) {
		val showHamburger = router.backstackSize == 1 // Show hamburg means this is home

		logD("Show hamburger?: $showHamburger")

		if (showHamburger) {
			// Shows navigation
			if (viewModel.navigationStyle == 1) {
				logI("Sync activity view with controller for legacy")
				supportActionBar?.setDisplayHomeAsUpEnabled(true)
				actionBarDrawerToggle.isDrawerIndicatorEnabled = true
				binding.drawerLayout.setDrawerLockMode(
					DrawerLayout.LOCK_MODE_UNLOCKED,
					binding.navView
				)
			} else {
				supportActionBar?.setDisplayHomeAsUpEnabled(false)
				binding.bottomNavigationView.visibility = VISIBLE
			}
		} else {

			// Hides navigation
			if (viewModel.navigationStyle == 1) {
				logI("Sync activity view with controller for legacy")
				supportActionBar?.setDisplayHomeAsUpEnabled(false)
				actionBarDrawerToggle.isDrawerIndicatorEnabled = false
				binding.drawerLayout.setDrawerLockMode(
					DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
					binding.navView
				)
			} else {
				supportActionBar?.setDisplayHomeAsUpEnabled(true)
				binding.bottomNavigationView.visibility = GONE
			}
		}

		val fab = binding.fab
		val eFab = binding.efab
		if (from is FABController) {
			from.hideFAB(fab)
			from.resetFAB(fab)
		}

		if (to is FABController) {
			to.manipulateFAB(fab)
			to.showFAB(fab)
		}

		if (from is ExtendedFABController) {
			from.hideFAB(eFab)
			from.resetFAB(eFab)
		}

		if (to is ExtendedFABController) {
			to.manipulateFAB(eFab)
			to.showFAB(eFab)
		}

		val tabLayout = binding.tabLayout

		if (from is TabbedController) {
			tabLayout.removeAllTabs()
			tabLayout.clearOnTabSelectedListeners()
		}

		if (to is TabbedController) {
			to.acceptTabLayout(tabLayout)
			to.configureTabs(tabLayout)
		}

		// clean up TabbedController
		if (from is TabbedController && to !is TabbedController) tabLayout.collapse()

		// setup TabbedController
		if (from !is TabbedController && to is TabbedController) tabLayout.expand()

		// Change the elevation for the app bar layout
		when (to) {
			is CollapsedToolBarController -> {
				binding.elevatedAppBarLayout.drop()
			}
			is LiftOnScrollToolBarController -> {
				binding.elevatedAppBarLayout.elevate(true)
			}
			else -> {
				binding.elevatedAppBarLayout.elevate(false)
			}
		}
	}

	@SuppressLint("ShowToast")
	fun makeSnackBar(
		@StringRes stringRes: Int,
		@Duration length: Int = Snackbar.LENGTH_SHORT
	): Snackbar =
		makeSnackBar(getString(stringRes), length)

	@SuppressLint("ShowToast")
	fun makeSnackBar(
		string: String,
		@Duration length: Int = Snackbar.LENGTH_SHORT
	): Snackbar =
		Snackbar.make(binding.coordinator, string, length).apply {
			when {
				binding.fab.isVisible -> anchorView = binding.fab
				binding.efab.isVisible -> anchorView = binding.efab
				binding.bottomNavigationView.isVisible -> anchorView = binding.bottomNavigationView
			}
		}

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

}