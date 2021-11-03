package app.shosetsu.android.ui.webView

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_URL
import app.shosetsu.android.common.ext.launchUI
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.ext.openInBrowser
import app.shosetsu.android.common.utils.CookieJarSync
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ActivityWebviewBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.ActivityWebviewBinding.inflate
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI

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
 * shosetsu
 * 31 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 *
 * Opens a URL in the apps internal webview
 * This allows cross saving cookies, allowing the app to access features such as logins
 */
class WebViewApp : AppCompatActivity(), DIAware {
	override val di: DI by closestDI()

	private lateinit var binding: ActivityWebviewBinding

	override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
		return super.onCreateView(name, context, attrs)?.also {
			binding = ActivityWebviewBinding.bind(it)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> finish()
			R.id.open_browser -> app.shosetsu.android.common.ext.launchIO {
				openInBrowser(intent.getStringExtra(BUNDLE_URL)!!)
				finish()
			}
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onBackPressed() {
		if (binding.webview.canGoBack()) {
			binding.webview.goBack()
		} else super.onBackPressed()
	}


	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.webview_menu, menu)
		return true
	}

	@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(inflate(layoutInflater).also { binding = it }.root)
		this.setSupportActionBar(binding.toolbar)
		supportActionBar?.apply {
			setDisplayHomeAsUpEnabled(true)
		}

		binding.webview.settings.javaScriptEnabled = true
		CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webview, true)

		binding.webview.webViewClient = object : WebViewClient() {
			override fun onPageFinished(view: WebView, url: String) {
				logI(url)
				view.evaluateJavascript("document.cookie") { cookies ->
					val httpUrl = url.toHttpUrl()
					CookieJarSync.saveFromResponse(
						httpUrl,
						cookies.split("; ").mapNotNull { Cookie.parse(httpUrl, it) }
					)
					logV("Cookies: $cookies")
				}
			}
		}

		intent.getStringExtra(BUNDLE_URL)?.let {
			binding.webview.loadUrl(it)
		} ?: run {
			launchUI {
				(parent as? MainActivity)?.makeSnackBar(R.string.activity_webview_null_url)?.show()
			}
			onBackPressed()
		}
	}
}