import com.android.build.api.dsl.ApplicationExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.reload)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            // compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.resources)
            implementation(libs.compose.preview)

            // lifecycle
            implementation(libs.lifecycle.runtime)
            implementation(libs.lifecycle.viewmodel)

            // navigation
            implementation(libs.navigation)

            // serialization
            implementation(libs.serialization)

            // ktor
            implementation(libs.ktor)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.json)
        }

        androidMain.dependencies {
            // compose preview
            implementation(libs.compose.preview)

            // compose activity
            implementation(libs.compose.activity)

            // ktor
            implementation(libs.ktor.okhttp)
        }

        nativeMain.dependencies {
            // ktor
            implementation(libs.ktor.darwin)
        }

        jvmMain.dependencies {
            // current os
            implementation(compose.desktop.currentOs)

            // coroutines
            implementation(libs.coroutines.swing)

            // ktor
            implementation(libs.ktor.okhttp)
        }
    }
}

extensions.configure<ApplicationExtension> {
    namespace = "xyz.teamgravity.cmppaginationmanual"
    compileSdk = libs.versions.sdk.compile.get().toInt()

    defaultConfig {
        applicationId = "xyz.teamgravity.cmppaginationmanual"
        minSdk = libs.versions.sdk.min.get().toInt()
        targetSdk = libs.versions.sdk.target.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.tooling)
}

compose.desktop {
    application {
        mainClass = "xyz.teamgravity.cmppaginationmanual.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "xyz.teamgravity.cmppaginationmanual"
            packageVersion = "1.0.0"
        }
    }
}