plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.ktor)
    application
}

group = "com.francescobottino.thehubproject"
version = "1.0.0"
application {
    mainClass.set("com.francescobottino.thehubproject.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
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
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kodein)
    implementation(libs.kodein.ktorServer)
    implementation(libs.dotenv)
}

// Reference the composeApp project
val composeAppProject = project(":composeApp") // Adjust if your module name is different

tasks.register<Copy>("copyWasmJsBrowserDistribution") {
    group = "build"
    description = "Copies the WasmJs browser distribution to the server's static resources."

    // Depends on the WasmJs build task from the composeApp module
    dependsOn(composeAppProject.tasks.named("wasmJsBrowserProductionWebpack"))

    // Source directory: output of the WasmJs build
    from(composeAppProject.layout.buildDirectory.dir("dist/wasmJs/productionExecutable"))

    // Destination directory: within the server's resources
    // This will place files in 'build/resources/main/static/' which Ktor can serve.
    into(layout.buildDirectory.dir("resources/main/static"))
}

// Make sure the 'processResources' task depends on this copy task
tasks.named("processResources") {
    dependsOn("copyWasmJsBrowserDistribution")
}

tasks.named("buildFatJar") { // Or your specific packaging task like shadowJar
    dependsOn("copyWasmJsBrowserDistribution")
}