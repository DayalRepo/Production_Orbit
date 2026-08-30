import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    androidLibrary {
        namespace = "com.orbitai.erp.core.designsystem"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        // Runs commonTest on the JVM so tests are executable from any host, not just macOS.
        withHostTestBuilder {}
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
        commonMain.dependencies {
            // Exposed as `api` so feature modules consume Material3 through the design system
            // rather than depending on Compose directly.
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.ui)
            api(libs.compose.components.resources)
            api(libs.compose.material.icons.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

// Without this the package is derived from the module path and comes out as
// `orbitai.core.designsystem.generated.resources`. Pin it instead.
compose.resources {
    packageOfResClass = "com.orbitai.erp.core.designsystem.resources"
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
