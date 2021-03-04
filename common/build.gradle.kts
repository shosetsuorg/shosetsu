plugins {
	kotlin("jvm")
	`java-library`
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.30")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
	implementation("com.github.shosetsuorg:kotlin-lib:1.0.0-rc55")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
	testImplementation(kotlin("test"))
	testImplementation(kotlin("test-junit"))
	implementation(kotlin("reflect"))
}