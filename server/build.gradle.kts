import java.util.*

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.buildconfig)
    application
}

group = "com.francescobottino.thehubproject.server"
version = "1.0.0"
application {
    mainClass.set("com.francescobottino.thehubproject.server.ApplicationKt")
}

dependencies {
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.bcrypt)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.postgresql.driver)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.dotenv)

    implementation(projects.shared)
    implementation(projects.serverShared)
    implementation(projects.tictactoeServer)
}

val localProperties: Properties by rootProject.extra

buildConfig {
    packageName("com.francescobottino.thehubproject.server")
    className("ServerConfig")
    useKotlinOutput()
    buildConfigField<String?>("ENVIRONMENT", (localProperties["environment"] as? String))
}