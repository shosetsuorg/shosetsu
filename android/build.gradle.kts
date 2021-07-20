import com.android.build.gradle.api.BaseVariantOutput
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
acraProperties.load(FileInputStream(acraPropertiesFile))

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

		buildConfigField("String", "acraUsername", acraProperties["username"].toString())
		buildConfigField("String", "acraPassword", acraProperties["password"].toString())

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
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
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
	implementation("androidx.work:work-runtime:2.7.0-alpha04")
	implementation("androidx.work:work-runtime-ktx:2.7.0-alpha04")
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
	implementation("androidx.core:core-ktx:1.6.0")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
	implementation("androidx.room:room-ktx:2.3.0")
	implementation("androidx.collection:collection-ktx:1.1.0")

	// - Life Cycle
	implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
	implementation(project(mapOf("path" to ":common")))

	// Test classes
	testImplementation("junit:junit:4.13.2")
	testImplementation("androidx.test.ext:junit:1.1.3")
	androidTestImplementation("androidx.test:runner:1.4.0")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

	// Annotations
	implementation("org.jetbrains:annotations:21.0.1")

	// Core libraries
	val shosetsuLibVersion: String by extra
	implementation("org.luaj:luaj-jse:3.0.1")
	implementation("com.github.shosetsuorg:kotlin-lib:v1.0.0-rc62")


	// Image loading
	implementation("com.squareup.picasso:picasso:2.71828")

	//TODO GITHUB sign in to save backup data to cloud
	//implementation "com.github.kohsuke:github-api:github-api-1.95"

	// Markdown view
	//implementation("us.feras.mdv:markdownview:1.1.0")

	// Time control
	implementation("joda-time:joda-time:2.10.10")


	// TODO Implement readerview provided by Mozilla
	//implementation "org.mozilla.components:feature-readerview:1.0.0"
	//implementation "org.mozilla.components:support-base:1.0.0"

	// Cloud flare calculator
	//implementation("com.zhkrb.cloudflare-scrape-android:scrape-webview:0.0.3")

	// Network
	implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")

	// Kotlin libraries
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")

	// Showcase
	implementation("com.github.deano2390:MaterialShowcaseView:1.3.4")

	// A cuter view
	implementation("com.github.shosetsuorg:DiscreteScrollView:1.5.1")

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
	val conductorVersion = "3.0.1"
	fun conductor(module: String, version: String = conductorVersion) =
		"com.bluelinelabs:$module:$version"

	implementation(conductor("conductor"))
	implementation(conductor("conductor-support", "3.0.0-rc2"))
	implementation(conductor("conductor-androidx-transition"))
	implementation(conductor("conductor-archlifecycle"))

	// FastScroll
	implementation("com.github.turing-tech:MaterialScrollBar:13.3.4")

	// Material Intro https://github.com/heinrichreimer/material-intro#standard-slide-simpleslide
	implementation("com.heinrichreimersoftware:material-intro:2.0.0")

	// Color Picker
	implementation("com.github.skydoves:colorpickerview:2.2.3")

	// Seek bar
//	implementation(project(mapOf("path" to ":bubbleseekbar")))
	implementation("com.github.shosetsuorg:Bubbleseekbar:2dae010baf")

	// Room
	implementation("androidx.room:room-runtime:2.3.0")
	kapt("androidx.room:room-compiler:2.3.0")

	// Fast Adapter
	val latestFastAdapterRelease = "5.4.1"
	fun fastadapter(module: String, version: String = latestFastAdapterRelease) =
		"com.mikepenz:$module:$version"

	implementation(fastadapter("fastadapter"))
	implementation(fastadapter("fastadapter-extensions-expandable"))
	implementation(fastadapter("fastadapter-extensions-binding"))
	implementation(fastadapter("fastadapter-extensions-diff"))
	implementation(fastadapter("fastadapter-extensions-drag"))
	implementation(fastadapter("fastadapter-extensions-paged"))
	implementation(fastadapter("fastadapter-extensions-scroll"))
	implementation(fastadapter("fastadapter-extensions-swipe"))
	implementation(fastadapter("fastadapter-extensions-ui"))
	implementation(fastadapter("fastadapter-extensions-utils"))

	// Guava cache
	implementation("com.google.guava:guava:30.1.1-android")

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

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.0")

	// KTX - Serialization
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")

	// Roomigrant
	val enableRoomigrant = false

	val roomigrantVersion = "0.3.4"
	implementation("com.github.MatrixDev.Roomigrant:RoomigrantLib:$roomigrantVersion")
	if (enableRoomigrant) {
		kapt("com.github.MatrixDev.Roomigrant:RoomigrantCompiler:$roomigrantVersion")
	}

	// Banner
	//implementation("com.github.shosetsuorg:MaterialBanner:2.0.7")
}

