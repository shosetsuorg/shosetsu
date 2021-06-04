import com.android.build.gradle.api.BaseVariantOutput
import java.io.BufferedReader
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

android {
	compileSdkVersion(30)
	defaultConfig {
		applicationId = "com.github.doomsdayrs.apps.shosetsu"
		minSdkVersion(22)
		targetSdkVersion(30)
		versionCode = 26
		versionName = "2.0.0"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		multiDexEnabled = true

		javaCompileOptions {
			annotationProcessorOptions {
				arguments += "room.schemaLocation" to "$projectDir/schemas"
			}
		}
	}

	buildFeatures {
		viewBinding = true
		dataBinding = true
	}

	buildTypes {
		named("release") {
			minifyEnabled(true)
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
			debuggable(true)
		}
	}
	flavorDimensions("default")
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
		targetCompatibility("1.8")
		sourceCompatibility("1.8")
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildToolsVersion = "30.0.3"
}

android.applicationVariants.forEach { variant ->
	variant.outputs.all {
		//TODO Fix this mess
		val v: BaseVariantOutput = this
		val appName = "shosetsu"
		val versionName = variant.versionName
		//def versionCode = variant.versionCode
		val flavorName = variant.flavorName
		val buildType = variant.buildType.name
		//def variantName = variant.name
		val gitCount = getCommitCount()

		if (buildType == "debug" && flavorName.toString() == "standard") {
			//outputFileName = "${appName}-${gitCount}.apk"
		} else {
			//outputFileName = "${appName}-${versionName}.apk"
		}
	}

}

android {
	lintOptions {
		isAbortOnError = false
	}
}

dependencies {
	implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

	// Google view things
	implementation("com.google.android.material:material:1.3.0")

	// Androidx
	implementation("androidx.constraintlayout:constraintlayout:2.0.4")
	implementation("androidx.work:work-runtime:2.5.0")
	implementation("androidx.work:work-runtime-ktx:2.5.0")
	implementation("androidx.gridlayout:gridlayout:1.0.0")
	implementation("androidx.preference:preference-ktx:1.1.1")
	implementation("androidx.recyclerview:recyclerview:1.2.1")
	implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
	implementation("androidx.appcompat:appcompat:1.3.0")
	implementation("androidx.multidex:multidex:2.0.1")
	implementation("androidx.biometric:biometric:1.1.0")
	implementation("androidx.annotation:annotation:1.2.0")
	implementation("androidx.appcompat:appcompat:1.3.0")
	implementation("androidx.cardview:cardview:1.0.0")
	implementation("androidx.browser:browser:1.3.0")
	implementation("androidx.core:core-ktx:1.5.0")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
	implementation("androidx.room:room-ktx:2.3.0")
	implementation("androidx.collection:collection-ktx:1.1.0")

	// - Life Cycle
	implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
	implementation(project(mapOf("path" to ":common")))

	// Test classes
	testImplementation("junit:junit:4.13.2")
	testImplementation("androidx.test.ext:junit:1.1.2")
	androidTestImplementation("androidx.test:runner:1.3.0")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

	// Annotations
	implementation("org.jetbrains:annotations:19.0.0")

	// Core libraries
	implementation("org.luaj:luaj-jse:3.0.1")
	implementation("com.github.shosetsuorg:kotlin-lib:v1.0.0-rc57")


	// Image loading
	implementation("com.squareup.picasso:picasso:2.71828")

	//TODO GITHUB sign in to save backup data to cloud
	//implementation "com.github.kohsuke:github-api:github-api-1.95"

	// Markdown view
	//implementation("us.feras.mdv:markdownview:1.1.0")

	// Time control
	implementation("joda-time:joda-time:2.10.5")


	// TODO Implement readerview provided by Mozilla
	//implementation "org.mozilla.components:feature-readerview:1.0.0"
	//implementation "org.mozilla.components:support-base:1.0.0"

	// Cloud flare calculator
	//implementation("com.zhkrb.cloudflare-scrape-android:scrape-webview:0.0.3")

	// Network
	implementation("com.squareup.okhttp3:okhttp:4.9.1")

	// Kotlin libraries
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")

	// Showcase
	implementation("com.github.deano2390:MaterialShowcaseView:1.3.4")

	// A cuter view
	implementation("com.github.shosetsuorg:DiscreteScrollView:1.5.1")

	// Error logging
	val acra_version = "5.7.0"
	implementation("ch.acra:acra-http:$acra_version")
	implementation("ch.acra:acra-mail:$acra_version")
	implementation("ch.acra:acra-dialog:$acra_version")

	// Conductor
	implementation("com.bluelinelabs:conductor:3.0.0")
	implementation("com.bluelinelabs:conductor-support:3.0.0-rc2")
	implementation("com.bluelinelabs:conductor-androidx-transition:3.0.0")
	implementation("com.bluelinelabs:conductor-archlifecycle:3.0.0")

	// FastScroll
	implementation("com.github.turing-tech:MaterialScrollBar:13.3.4")

	// Material Intro https://github.com/heinrichreimer/material-intro#standard-slide-simpleslide
	implementation("com.heinrichreimersoftware:material-intro:2.0.0")

	// Color Picker
	implementation("com.github.skydoves:colorpickerview:2.1.6")

	// Seek bar
	implementation(project(mapOf("path" to ":bubbleseekbar")))

	// Room
	implementation("androidx.room:room-runtime:2.3.0")
	kapt("androidx.room:room-compiler:2.3.0")

	// Fast Adapter
	val latestFastAdapterRelease = "5.3.2"

	implementation("com.mikepenz:fastadapter:${latestFastAdapterRelease}")
	implementation("com.mikepenz:fastadapter-extensions-expandable:${latestFastAdapterRelease}")
	implementation("com.mikepenz:fastadapter-extensions-binding:${latestFastAdapterRelease}")
	implementation("com.mikepenz:fastadapter-extensions-diff:${latestFastAdapterRelease}")
	implementation("com.mikepenz:fastadapter-extensions-drag:${latestFastAdapterRelease}")
	implementation("com.mikepenz:fastadapter-extensions-paged:${latestFastAdapterRelease}")
	implementation("com.mikepenz:fastadapter-extensions-scroll:${latestFastAdapterRelease}")
	implementation("com.mikepenz:fastadapter-extensions-swipe:${latestFastAdapterRelease}")
	implementation("com.mikepenz:fastadapter-extensions-ui:${latestFastAdapterRelease}")
	implementation("com.mikepenz:fastadapter-extensions-utils:${latestFastAdapterRelease}")

	// Guava cache
	implementation("com.google.guava:guava:30.1.1-android")

	// kode-in
	implementation("org.kodein.di:kodein-di-generic-jvm:6.5.5")
	implementation("org.kodein.di:kodein-di-framework-android-core:6.5.5")
	implementation("org.kodein.di:kodein-di-framework-android-x:6.5.5")

	// KTX

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.2")

	// KTX - Serialization
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")

	// Roomigrant
	implementation("com.github.MatrixDev.Roomigrant:RoomigrantLib:0.2.0")
	kapt("com.github.MatrixDev.Roomigrant:RoomigrantCompiler:0.2.0")
}