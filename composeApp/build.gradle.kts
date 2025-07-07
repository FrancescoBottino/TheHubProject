import com.android.build.api.variant.impl.VariantOutputImpl
import com.francescobottino.thehubproject.build_logic.convention.AppEnvironment
import com.francescobottino.thehubproject.build_logic.convention.domain
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.envornment)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.napier)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.transitions)
            implementation(libs.composeIcons.feather)
            implementation(libs.arrow.core)

            implementation(projects.shared)
            implementation(
                when(appEnvironment.current) {
                    AppEnvironment.DEVELOPMENT -> projects.config.dev
                    AppEnvironment.STAGING -> projects.config.staging
                    AppEnvironment.PRODUCTION -> projects.config.prod
                }
            )
            implementation(projects.clientShared)
            implementation(projects.clientFeatures.auth)
            implementation(projects.games.tictactoe.client)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.slf4j.api)
            implementation(libs.slf4j.android)
            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.java)
            implementation(libs.slf4j)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

dependencies {
    debugImplementation(compose.runtime)
    debugImplementation(compose.foundation)
    debugImplementation(compose.material3)
    debugImplementation(compose.ui)
    debugImplementation(compose.components.resources)
    debugImplementation(compose.components.uiToolingPreview)
    debugImplementation(compose.uiTooling)
}

private val mainVersionCode = 1
private val mainVersionName = "1.0.$mainVersionCode"

android {
    namespace = "com.francescobottino.thehubproject"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    val keystorePropertiesFile = project.file("keystore.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(keystorePropertiesFile.inputStream())
    }

    signingConfigs {
        if (keystoreProperties.getProperty("keyAlias") != null) {
            create("release") {
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
            }
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = false
            isDebuggable = true
        }
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = true
            isDebuggable = false
        }
    }
    defaultConfig {
        applicationId = "com.francescobottino.thehubproject"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = mainVersionCode
        versionName = mainVersionName
        manifestPlaceholders["deepLinkDomain"] = appEnvironment.current.domain().orEmpty()
        manifestPlaceholders["deepLinkScheme"] = "thehubproject"
    }
    lint {
        disable += "NullSafeMutableLiveData"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    androidComponents {
        onVariants(selector().all()) { variant ->
            variant.outputs.forEach { output ->
                val newName = "TheHubProject-${variant.name}.apk"
                (output as? VariantOutputImpl)?.let {
                    it.outputFileName = newName
                }
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.francescobottino.thehubproject.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg, TargetFormat.Pkg,     // macOS
                TargetFormat.Msi, TargetFormat.Exe,     // Windows
                TargetFormat.Deb, TargetFormat.Rpm,     // Red Hat/Fedora
                //TargetFormat.AppImage // Universal Linux
            )
            packageName = "The Hub Project"
            packageVersion = mainVersionName

            macOS {
                iconFile = project.file("src/desktopMain/assets/app_icon.icns")
            }
            windows {
                iconFile = project.file("src/desktopMain/assets/app_icon.png")
            }
            linux {
                iconFile = project.file("src/desktopMain/assets/app_icon.png")
            }
        }

        buildTypes {
            release {
                proguard {
                    optimize = false
                    obfuscate = false
                    isEnabled = false
                }
            }
        }
    }
}