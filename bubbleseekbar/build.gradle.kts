plugins {
	id("com.android.library")
	kotlin("android")
	`maven-publish`
	`maven`
	//id("android-maven")
}

val Version = "4.0.0"

group = "app.shosetsu.libs"
version = Version
description = "An old library that makes seek bars easy"

android {
	compileSdkVersion(30)
	buildToolsVersion("30.0.3")

	defaultConfig {
		minSdkVersion(16)
		targetSdkVersion(30)
		versionCode = 37
		versionName = Version
	}

	buildTypes {
		named("release") {
			minifyEnabled(true)
			proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
		}
	}
}

dependencies {
	implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

	implementation("androidx.appcompat:appcompat:1.3.0")
	implementation("androidx.core:core-ktx:1.5.0")
	implementation(kotlin("stdlib-jdk7", "1.5.0"))
}