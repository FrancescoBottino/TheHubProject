package com.francescobottino.thehubproject.build_logic.convention

fun AppEnvironment.apiDomain(): String? = when(this) {
    AppEnvironment.DEVELOPMENT -> null
    AppEnvironment.STAGING -> AppEnvironment.stagingApiDomain()
    AppEnvironment.PRODUCTION -> AppEnvironment.prodApiDomain()
}

fun AppEnvironment.Companion.prodApiDomain(): String = "thehubproject-api.up.railway.app"
fun AppEnvironment.Companion.stagingApiDomain(): String = "thehubproject-api-staging.up.railway.app"