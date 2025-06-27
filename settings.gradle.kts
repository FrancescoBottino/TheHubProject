import java.util.*

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

// Helper function to read local.properties
fun readLocalProperties(projectDir: File): Properties {
    val properties = Properties()
    val propertiesFile = File(projectDir, "local.properties")
    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use { input ->
            properties.load(input)
        }
    } else {
        println("Warning: local.properties file not found at ${propertiesFile.absolutePath}")
    }
    return properties
}

// Load variables globally for all projects
gradle.beforeProject {
    if(project == rootProject) { // Only execute once for the root project
        val envVariables = readDotEnv(rootProject.projectDir)

        val localProperties = readLocalProperties(rootProject.projectDir)
        val environment = if(project.hasProperty("environment")) project.property("environment") else localProperties["environment"]
        val devServerIp = localProperties["dev_server_ip"] ?: "Localhost"
        val devServerPort = envVariables["PORT"]?.toInt() ?: 9090

        rootProject.extra.set("environment", environment)
        println("   environment: $environment")
        rootProject.extra.set("devServerIp", devServerIp)
        println("   devServerIp: $devServerIp")
        rootProject.extra.set("devServerPort", devServerPort)
        println("   devServerPort: $devServerPort")
    }
}

pluginManagement {
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

fun includeGame(name: String) {
    // Client module
    include(":${name}-client")
    project(":${name}-client").projectDir = file("games/${name}/client")
    // Server module
    include(":${name}-server")
    project(":${name}-server").projectDir = file("games/${name}/server")
    // Shared module
    include(":${name}-shared")
    project(":${name}-shared").projectDir = file("games/${name}/shared")
}

fun includeClientFeature(name: String) {
    include(":clientFeature-${name}")
    project(":clientFeature-${name}").projectDir = file("client-features/${name}")
}

include(":composeApp")
include(":server")
include(":shared")
include(":server-shared")
include(":client-shared")
includeClientFeature("auth")
includeGame("tictactoe")