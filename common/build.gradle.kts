plugins {
	kotlin("jvm")
	`java-library`
	kotlin("plugin.serialization")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.32")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")

	implementation("com.github.shosetsuorg:kotlin-lib:1.0.0-rc55")

	testImplementation(kotlin("test"))
	testImplementation(kotlin("test-junit"))
	implementation(kotlin("reflect"))
}