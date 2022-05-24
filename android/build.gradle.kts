import org.jetbrains.kotlin.konan.properties.Properties
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

plugins {
	id("com.android.application")
	kotlin("android")
	kotlin("kapt")
	kotlin("plugin.serialization")
}

@Throws(IOException::class)
fun String.execute(): Process = Runtime.getRuntime().exec(this)

@Throws(IOException::class)
fun Process.getText(): String =
	org.codehaus.groovy.runtime.IOGroovyMethods.getText(
		BufferedReader(
			InputStreamReader(
				inputStream
			)
		)
	).also {
		org.codehaus.groovy.runtime.ProcessGroovyMethods.closeStreams(this)
	}

@Throws(IOException::class)
fun getCommitCount(): String = "git rev-list --count HEAD".execute().getText().trim()

val acraPropertiesFile = rootProject.file("acra.properties")
val acraProperties = Properties()

if (acraPropertiesFile.exists())
	acraProperties.load(FileInputStream(acraPropertiesFile))

android {
	compileSdk = 31
	defaultConfig {
		applicationId = "com.github.doomsdayrs.apps.shosetsu"
		minSdk = 22
		targetSdk = 31
		versionCode = 26
		versionName = "2.0.0"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		multiDexEnabled = true

		javaCompileOptions {
			annotationProcessorOptions {
				arguments += "room.schemaLocation" to "$projectDir/schemas"
			}
		}

		buildConfigField("String", "acraUsername", acraProperties["username"]?.toString() ?: "\"\"")
		buildConfigField("String", "acraPassword", acraProperties["password"]?.toString() ?: "\"\"")

		setProperty("archivesBaseName", rootProject.name)
	}

	buildFeatures {
		viewBinding = true
		dataBinding = true
		compose = true
	}

	composeOptions {
		kotlinCompilerExtensionVersion = "1.1.0"
	}

	splits {
		abi {
			isEnable = true

			isUniversalApk = true
		}
	}


	buildTypes {
		named("release") {
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
			versionNameSuffix = ""
			multiDexEnabled = true
		}
		named("debug") {
			versionNameSuffix = "-${getCommitCount()}"
			applicationIdSuffix = ".debug"
			isDebuggable = true
		}
	}
	flavorDimensions.add("default")
	productFlavors {
		create("playstore") {
			// play store will be in this
			applicationId = "app.shosetsu.android"
			applicationIdSuffix = ".play"
			versionNameSuffix = "-play"
		}
		create("uptodown") {
			applicationIdSuffix = ".uptodown"
			versionNameSuffix = "-uptodown"
		}
		create("fdroid") {
			applicationIdSuffix = ".fdroid"
			versionNameSuffix = "-fdroid"
		}
		create("standard") {
			isDefault = true
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildToolsVersion = "30.0.3"

	lint {
		abortOnError = false
	}
	namespace = "com.github.doomsdayrs.apps.shosetsu"
}

//TODO Fix application variant naming
//android.applicationVariants.forEach { variant ->
//	variant.outputs.all {
//val v: BaseVariantOutput = this
//val appName = "shosetsu"
//val versionName = variant.versionName
//def versionCode = variant.versionCode
//val flavorName = variant.flavorName
//val buildType = variant.buildType.name
//def variantName = variant.name
//val gitCount = getCommitCount()

//if (buildType == "debug" && flavorName.toString() == "standard") {
//outputFileName = "${appName}-${gitCount}.apk"
//} else {
//outputFileName = "${appName}-${versionName}.apk"
//}
//	}

//}

dependencies {
	implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

	// Google view things
	implementation("com.google.android.material:material:1.6.0")

	// Androidx
	//implementation("androidx.constraintlayout:constraintlayout:2.1.3")
	implementation("androidx.work:work-runtime:2.7.1")
	implementation("androidx.work:work-runtime-ktx:2.7.1")
	implementation("androidx.gridlayout:gridlayout:1.0.0")
	//implementation("androidx.preference:preference-ktx:1.2.0")
	//implementation("androidx.recyclerview:recyclerview:1.2.1")
	//implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
	implementation("androidx.appcompat:appcompat:1.4.1")
	//implementation("androidx.multidex:multidex:2.0.1")
	//implementation("androidx.biometric:biometric:1.1.0") TODO biometric unlocking
	implementation("androidx.annotation:annotation:1.3.0")
	//implementation("androidx.cardview:cardview:1.0.0")
	//implementation("androidx.browser:browser:1.4.0")
	implementation("androidx.core:core-ktx:1.7.0")



	implementation("androidx.collection:collection-ktx:1.2.0")
	implementation("androidx.core:core-splashscreen:1.0.0-rc01")

	// - Life Cycle
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
	implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

	// Test classes
	testImplementation("junit:junit:4.13.2")
	testImplementation("androidx.test.ext:junit:1.1.3")
	androidTestImplementation("androidx.test:runner:1.4.0")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

	// Annotations
	implementation("org.jetbrains:annotations:23.0.0")

	// Core libraries
	//val shosetsuLibVersion: String by extra
	implementation("org.luaj:luaj-jse:3.0.1")
	implementation("com.github.shosetsuorg:kotlin-lib:76c4750")
	implementation("org.jsoup:jsoup:1.14.3")


	// Image loading
	implementation("io.coil-kt:coil-compose:2.0.0")

	//TODO GITHUB sign in to save backup data to cloud
	//implementation "com.github.kohsuke:github-api:github-api-1.95"

	// Markdown view
	//implementation("us.feras.mdv:markdownview:1.1.0")

	// Time control
	implementation("joda-time:joda-time:2.10.14")

	// Cloud flare calculator
	//implementation("com.zhkrb.cloudflare-scrape-android:scrape-webview:0.0.3")

	// Network
	implementation("com.squareup.okhttp3:okhttp:4.9.3")

	// Kotlin libraries
	implementation(kotlin("stdlib-jdk8"))
	//implementation(kotlin("reflect"))

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")

	// Showcase
	//implementation("com.github.deano2390:MaterialShowcaseView:1.3.4")

	// A cuter view
	//implementation("com.github.shosetsuorg:DiscreteScrollView:1.5.1")

	// Error logging
	val acraVersion = "5.8.3"
	fun acra(module: String, version: String = acraVersion) =
		"ch.acra:$module:$version"

	implementation(acra("acra-http"))
	//implementation(acra("acra-mail"))
	implementation(acra("acra-dialog"))

	// Auto service
	//kapt("com.google.auto.service:auto-service:+")
	//compileOnly("com.google.auto.service:auto-service-annotations:1.0")

	// Conductor
	val conductorVersion = "3.1.4"
	fun conductor(module: String, version: String = conductorVersion) =
		"com.bluelinelabs:$module:$version"

	implementation(conductor("conductor"))
	implementation(conductor("conductor-support", "3.0.0-rc2"))
	implementation(conductor("conductor-androidx-transition"))
	implementation(conductor("conductor-archlifecycle"))

	// FastScroll
	//implementation("com.github.turing-tech:MaterialScrollBar:13.3.4")

	// Material Intro https://github.com/heinrichreimer/material-intro#standard-slide-simpleslide
	implementation("com.heinrichreimersoftware:material-intro:2.0.0")

	// Color Picker
	//implementation("com.github.skydoves:colorpickerview:2.2.3")

	// Seek bar
//	implementation(project(mapOf("path" to ":bubbleseekbar")))
//	implementation("com.github.shosetsuorg:Bubbleseekbar:2dae010baf")

	// Room
	val roomVersion = "2.5.0-alpha01"
	fun room(module: String, version: String = roomVersion) =
		"androidx.room:$module:$version"

	implementation(room("room-runtime"))
	annotationProcessor(room("room-compiler"))
	kapt(room("room-compiler"))
	implementation(room("room-ktx"))
	implementation(room("room-paging"))

	// Guava cache
	implementation("com.google.guava:guava:31.0.1-android")

	// kode-in
	val kodeinVersion = "7.6.0"
	fun kodein(module: String, version: String = kodeinVersion) =
		"org.kodein.di:$module:$version"

	implementation(kodein("kodein-di"))
	implementation(kodein("kodein-di-jvm"))
	implementation(kodein("kodein-di-framework-android-core"))
	implementation(kodein("kodein-di-framework-android-support"))
	implementation(kodein("kodein-di-framework-android-x"))

	// KTX

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.1")

	// KTX - Serialization
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

	// Roomigrant
	val enableRoomigrant = false

	val roomigrantVersion = "0.3.4"
	implementation("com.github.MatrixDev.Roomigrant:RoomigrantLib:$roomigrantVersion")
	if (enableRoomigrant) {
		kapt("com.github.MatrixDev.Roomigrant:RoomigrantCompiler:$roomigrantVersion")
	}

	// Banner
	//implementation("com.github.shosetsuorg:MaterialBanner:2.0.7")


	// Compose
	val androidxCompose = "1.1.1"
	fun androidxCompose(
		module: String,
		submodule: String = module,
		version: String = androidxCompose
	) = "androidx.compose.$submodule:$module:$version"

	implementation(androidxCompose("ui"))

	implementation(androidxCompose("compiler"))

	//- Tooling support (Previews, etc.)
	implementation(androidxCompose("ui-tooling", "ui"))

	//- Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
	implementation(androidxCompose("foundation"))
	implementation(androidxCompose("animation"))
	implementation(androidxCompose("animation-graphics", "animation"))
	implementation(androidxCompose("animation-core", "animation"))

	// - Material
	implementation("androidx.compose.material:material:1.1.1")
	//implementation("androidx.compose.material:material-ripple:1.0.2")

	//- Material design icons
	//implementation("androidx.compose.material:material-icons-core:1.0.2")
	//implementation("androidx.compose.material:material-icons-extended:1.0.2")

	// - accompanist
	val accompanistVersion = "0.23.1"
	fun accompanist(module: String, version: String = accompanistVersion) =
		"com.google.accompanist:$module:$version"

	implementation(accompanist("accompanist-appcompat-theme"))
	implementation(accompanist("accompanist-pager"))
	implementation(accompanist("accompanist-swiperefresh"))
	implementation(accompanist("accompanist-webview", "0.24.6-alpha"))
	implementation(accompanist("accompanist-placeholder-material"))
	implementation(accompanist("accompanist-pager-indicators"))
	//- Integration with observables
	implementation("androidx.compose.runtime:runtime-livedata:1.1.1")

	//- UI Tests
	//androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.2")

	// MDC Adapter
	implementation("com.google.android.material:compose-theme-adapter:1.1.10")

	val androidxActivity = "1.4.0"
	fun androidxActivity(module: String, version: String = androidxActivity) =
		"androidx.activity:$module:$version"
	implementation(androidxActivity("activity"))
	implementation(androidxActivity("activity-ktx"))
	implementation(androidxActivity("activity-compose"))

	implementation("com.chargemap.compose:numberpicker:1.0.3")

	// QR Code
	implementation("io.github.g00fy2.quickie:quickie-bundled:1.4.1")

	implementation("com.github.doomsdayrs:qrcode-kotlin:513d290b")

	// - paging
	val pagingVersion = "3.1.1"
	fun paging(module: String, version: String = pagingVersion) =
		"androidx.paging:$module:$version"

	implementation(paging("paging-runtime"))
	implementation(paging("paging-compose", "1.0.0-alpha14"))
	implementation(kotlin("reflect"))


}