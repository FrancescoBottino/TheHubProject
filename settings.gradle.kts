rootProject.name = "TheHubProject"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Helper function to read .env file
fun readDotEnv(projectDir: File): Map<String, String> {
    val envFile = File(projectDir, ".env")
    if (!envFile.exists()) {
        println("Warning: .env file not found at ${envFile.absolutePath}")
        return emptyMap()
    }

    val envMap = mutableMapOf<String, String>()
    envFile.readLines().forEach { line ->
        val trimmedLine = line.trim()
        if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
            val parts = trimmedLine.split("=", limit = 2)
            if (parts.size == 2) {
                envMap[parts[0].trim()] = parts[1].trim()
            }
        }
    }
    return envMap
}

// Load variables globally for all projects
gradle.beforeProject {
    if(project == rootProject) { // Only execute once for the root project
        val envVariables = readDotEnv(rootProject.projectDir)

        val environment: String by project
        val devServerHost = envVariables["SERVER_HOST"] ?: "Localhost"
        val devServerPort = envVariables["SERVER_PORT"]?.toInt() ?: 9090

        println("   environment: $environment")
        rootProject.extra.set("devServerHost", devServerHost)
        println("   devServerHost: $devServerHost")
        rootProject.extra.set("devServerPort", devServerPort)
        println("   devServerPort: $devServerPort")
    }
}

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")
include(":server")
include(":shared")
include(":config:dev")
include(":config:staging")
include(":config:prod")
include(":server-shared")
include(":client-shared")
include(":client-features:auth")
include(":games:tictactoe:client")
include(":games:tictactoe:server")
include(":games:tictactoe:shared")