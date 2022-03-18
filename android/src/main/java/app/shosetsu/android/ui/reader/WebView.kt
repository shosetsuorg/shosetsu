package app.shosetsu.android.ui.reader

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.shosetsu.android.ui.reader.LoadingState.Finished
import app.shosetsu.android.ui.reader.LoadingState.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ##################################
// ##################################
// ##################################
// ##################################
// ##################################
// ##################################
// Imported, APACHE LICENSE BY GOOGLE
// ##################################
// ##################################
// ##################################
// ##################################
// ##################################
// ##################################

/**
 * A wrapper around the Android View WebView to provide a basic WebView composable.
 *
 * If you require more customisation you are most likely better rolling your own and using this
 * wrapper as an example.
 *
 * @param state The webview state holder where the Uri to load is defined.
 * @param captureBackPresses Set to true to have this Composable capture back presses and navigate
 * the WebView back.
 * @param navigator An optional navigator object that can be used to control the WebView's
 * navigation from outside the composable.
 * @param onCreated Called when the WebView is first created, this can be used to set additional
 * settings on the WebView. WebChromeClient and WebViewClient should not be set here as they will be
 * subsequently overwritten after this lambda is called.
 * @param onError Called when the WebView encounters an error. Forwarded event from the
 * WebViewClient
 * @sample com.google.accompanist.sample.webview.BasicWebViewSample
 */
@Composable
fun WebView(
	state: WebViewState,
	modifier: Modifier = Modifier,
	captureBackPresses: Boolean = true,
	navigator: WebViewNavigator = rememberWebViewNavigator(),
	onCreated: (WebView) -> Unit = {},
	onError: (request: WebResourceRequest?, error: WebResourceError?) -> Unit = { _, _ -> },
	onPageFinished: (WebView, url: String?) -> Unit = { _, _ -> }
) {
	var webView by remember { mutableStateOf<WebView?>(null) }

	BackHandler(captureBackPresses && navigator.canGoBack) {
		webView?.goBack()
	}

	LaunchedEffect(webView, navigator) {
		with(navigator) { webView?.handleNavigationEvents() }
	}

	AndroidView(
		factory = { context ->
			WebView(context).apply {
				onCreated(this)

				layoutParams = ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT
				)

				webChromeClient = object : WebChromeClient() {
					override fun onReceivedTitle(view: WebView?, title: String?) {
						super.onReceivedTitle(view, title)
						state.pageTitle = title
					}

					override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
						super.onReceivedIcon(view, icon)
						state.pageIcon = icon
					}

					override fun onProgressChanged(view: WebView?, newProgress: Int) {
						super.onProgressChanged(view, newProgress)
						if (state.loadingState is LoadingState.Finished) return
						state.loadingState = LoadingState.Loading(newProgress / 100.0f)
					}
				}

				webViewClient = object : WebViewClient() {
					override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
						super.onPageStarted(view, url, favicon)
						state.loadingState = LoadingState.Loading(0.0f)
						state.errorsForCurrentRequest.clear()
						state.pageTitle = null
						state.pageIcon = null
					}

					override fun onPageFinished(view: WebView?, url: String?) {
						super.onPageFinished(view, url)
						state.loadingState = LoadingState.Finished
						navigator.canGoBack = view?.canGoBack() ?: false
						navigator.canGoForward = view?.canGoForward() ?: false
						onPageFinished(view!!, url)
					}

					override fun doUpdateVisitedHistory(
						view: WebView?,
						url: String?,
						isReload: Boolean
					) {
						super.doUpdateVisitedHistory(view, url, isReload)
						// WebView will often update the current url itself.
						// This happens in situations like redirects and navigating through
						// history. We capture this change and update our state holder url.
						// On older APIs (28 and lower), this method is called when loading
						// html data. We don't want to update the state in this case as that will
						// overwrite the html being loaded.
						if (url != null &&
							!url.startsWith("data:text/html") &&
							state.content.getCurrentUrl() != url
						) {
							state.content = WebContent.Url(url)
						}
					}

					override fun onReceivedError(
						view: WebView?,
						request: WebResourceRequest?,
						error: WebResourceError?
					) {
						super.onReceivedError(view, request, error)

						if (error != null) {
							state.errorsForCurrentRequest.add(WebViewError(request, error))
						}

						onError(request, error)
					}

					override fun shouldOverrideUrlLoading(
						view: WebView?,
						request: WebResourceRequest?
					): Boolean {
						// Override all url loads to make the single source of truth
						// of the URL the state holder Url
						request?.let {
							val content = WebContent.Url(it.url.toString())
							state.content = content
						}
						return true
					}
				}
			}.also { webView = it }
		},
		modifier = modifier
	) { view ->
		when (val content = state.content) {
			is WebContent.Url -> {
				val url = content.url

				if (url.isNotEmpty() && url != view.url) {
					view.loadUrl(url)
				}
			}
			is WebContent.Data -> {
				view.loadData(content.data, "text/html", "UTF-8")
				//view.loadDataWithBaseURL(content.baseUrl, content.data, "text/html", "utf-8", null)
			}
		}

		navigator.canGoBack = view.canGoBack()
		navigator.canGoForward = view.canGoForward()
	}
}

sealed class WebContent {
	data class Url(val url: String) : WebContent()
	data class Data(val data: String, val baseUrl: String? = null) : WebContent()

	fun getCurrentUrl(): String? {
		return when (this) {
			is Url -> url
			is Data -> baseUrl
		}
	}
}

/**
 * Sealed class for constraining possible loading states.
 * See [Loading] and [Finished].
 */
sealed class LoadingState {

	/**
	 * Describes a webview between `onPageStarted` and `onPageFinished` events, contains a
	 * [progress] property which is updated by the webview.
	 */
	data class Loading(val progress: Float) : LoadingState()

	/**
	 * Describes a webview that has finished loading content (or not started).
	 */
	object Finished : LoadingState()
}

/**
 * A state holder to hold the state for the WebView. In most cases this will be remembered
 * using the rememberWebViewState(uri) function.
 */
@Stable
class WebViewState(webContent: WebContent) {
	/**
	 *  The content being loaded by the WebView
	 */
	var content by mutableStateOf<WebContent>(webContent)

	/**
	 * Whether the WebView is currently [LoadingState.Loading] data in its main frame (along with
	 * progress) or the data loading has [LoadingState.Finished]. See [LoadingState]
	 */
	var loadingState: LoadingState by mutableStateOf(LoadingState.Finished)
		internal set

	/**
	 * Whether the webview is currently loading data in its main frame
	 */
	val isLoading: Boolean
		get() = loadingState !is LoadingState.Finished

	/**
	 * The title received from the loaded content of the current page
	 */
	var pageTitle: String? by mutableStateOf(null)
		internal set

	/**
	 * the favicon received from the loaded content of the current page
	 */
	var pageIcon: Bitmap? by mutableStateOf(null)
		internal set

	/**
	 * A list for errors captured in the last load. Reset when a new page is loaded.
	 * Errors could be from any resource (iframe, image, etc.), not just for the main page.
	 * For more fine grained control use the OnError callback of the WebView.
	 */
	val errorsForCurrentRequest = mutableStateListOf<WebViewError>()
}

/**
 * Allows control over the navigation of a WebView from outside the composable. E.g. for performing
 * a back navigation in response to the user clicking the "up" button in a TopAppBar.
 *
 * @see [rememberWebViewNavigator]
 */
@Stable
class WebViewNavigator(private val coroutineScope: CoroutineScope) {

	private enum class NavigationEvent { BACK, FORWARD, RELOAD, STOP_LOADING }

	private val navigationEvents: MutableSharedFlow<NavigationEvent> = MutableSharedFlow()

	// Use Dispatchers.Main to ensure that the webview methods are called on UI thread
	internal suspend fun WebView.handleNavigationEvents(): Nothing = withContext(Dispatchers.Main) {
		navigationEvents.collect { event ->
			when (event) {
				NavigationEvent.BACK -> goBack()
				NavigationEvent.FORWARD -> goForward()
				NavigationEvent.RELOAD -> reload()
				NavigationEvent.STOP_LOADING -> stopLoading()
			}
		}
	}

	/**
	 * True when the web view is able to navigate backwards, false otherwise.
	 */
	var canGoBack: Boolean by mutableStateOf(false)
		internal set

	/**
	 * True when the web view is able to navigate forwards, false otherwise.
	 */
	var canGoForward: Boolean by mutableStateOf(false)
		internal set

	/**
	 * Navigates the webview back to the previous page.
	 */
	fun navigateBack() {
		coroutineScope.launch { navigationEvents.emit(NavigationEvent.BACK) }
	}

	/**
	 * Navigates the webview forward after going back from a page.
	 */
	fun navigateForward() {
		coroutineScope.launch { navigationEvents.emit(NavigationEvent.FORWARD) }
	}

	/**
	 * Reloads the current page in the webview.
	 */
	fun reload() {
		coroutineScope.launch { navigationEvents.emit(NavigationEvent.RELOAD) }
	}

	/**
	 * Stops the current page load (if one is loading).
	 */
	fun stopLoading() {
		coroutineScope.launch { navigationEvents.emit(NavigationEvent.STOP_LOADING) }
	}
}

/**
 * Creates and remembers a [WebViewNavigator] using the default [CoroutineScope] or a provided
 * override.
 */
@Composable
fun rememberWebViewNavigator(
	coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(coroutineScope) { WebViewNavigator(coroutineScope) }

/**
 * A wrapper class to hold errors from the WebView.
 */
@Immutable
data class WebViewError(
	/**
	 * The request the error came from.
	 */
	val request: WebResourceRequest?,
	/**
	 * The error that was reported.
	 */
	val error: WebResourceError
)

/**
 * Creates a WebView state that is remembered across Compositions.
 *
 * @param data The uri to load in the WebView
 */
@Composable
fun rememberWebViewStateWithHTMLData(data: String, baseUrl: String? = null) =
	remember(data, baseUrl) { WebViewState(WebContent.Data(data, baseUrl)) }