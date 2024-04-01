plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.ktlint.plugin)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover.core)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
}

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())

//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//    }

    androidTarget {
        publishLibraryVariants("release")
    }

//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()

    jvm()

    sourceSets {
        commonMain.dependencies {
            with(libs) {
                implementation(kotlin.datetime)
                implementation(kotlinx.serialization.json)
                implementation(koin.core)
                implementation(kottage)
            }
        }

        androidMain.dependencies {
            with(libs) {
                implementation(android.app.context)
                implementation(koin.android)
            }
        }
    }
}

android {
    namespace = "com.palacera.kmpcore.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
dependencies {
    implementation(libs.androidx.startup.runtime)
}

ktlint {
    version.set(libs.versions.gradle.ktlint.core.get())
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom("${project.rootDir}/config/detekt/detekt-config.yml")
    source.setFrom(kotlin.sourceSets.flatMap { it.kotlin.sourceDirectories })
    ignoreFailures = false
}

publishing {
    publications {
        create<MavenPublication>("default") {
            // Adjust groupId, artifactId, and version according to your needs
            group = "com.palacera.kmpcache"
            artifactId = "kmpcache"
            version = "0.0.10"

            // Include artifacts from Kotlin Multiplatform targets
            from(components["kotlin"])
        }
    }
}
