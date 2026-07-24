// :core:domain is intentionally a PURE KOTLIN module (java-library), not an
// Android library. This physically enforces Engineering Law #1: the domain
// layer cannot accidentally take a dependency on Android, Compose, Room, or
// Retrofit. It only knows about Kotlin, coroutines, and its own models.
plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm) // see note in root: aliasing kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}

tasks.test {
    useJUnitPlatform()
}
