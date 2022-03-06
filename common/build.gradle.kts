plugins {
	kotlin("jvm")
	`java-library`
	kotlin("plugin.serialization")
}

val shosetsuLibVersion: String by extra

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
	// Kotlin libraries
	implementation(kotlin("stdlib"))
	implementation(kotlin("reflect"))

	// Kotlin extensions
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

	// Core library
	implementation("com.github.shosetsuorg:kotlin-lib:e91ea4e")

	// Testing
	testImplementation(kotlin("test"))
	testImplementation(kotlin("test-junit"))
}