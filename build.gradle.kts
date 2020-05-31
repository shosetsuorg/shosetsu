var minSDK = 24
var kotlin_version = "1.3.72"
var kodein_version = "6.5.5"
var conductor_version = "3.0.0-rc4"
var conductor_version_support = "3.0.0-rc2"

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

	repositories {
		google()
		jcenter()
	}

	dependencies {
		classpath("com.android.tools.build:gradle:4.0.0")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
		// NOTE: Do not place your application dependencies here; they belong
		// in the individual module build.gradle files
	}
}

allprojects {
	repositories {
		google()
		jcenter()
		maven("https://jitpack.io")
	}
}

task("clean", Delete::class) {
	delete(rootProject.buildDir)
}
