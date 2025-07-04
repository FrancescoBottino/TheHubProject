plugins {
    `kotlin-dsl`
}

group = "com.francescobottino.thehubproject.build_logic.convention"

gradlePlugin {
    plugins {
        register("envornmentPlugin") {
            id = "envornment-plugin"
            implementationClass = "com.francescobottino.thehubproject.build_logic.convention.plugin.EnvironmentPlugin"
        }
    }
}