package com.francescobottino.thehubproject.build_logic.convention

fun AppEnvironment.domain(): String? = when(this) {
    AppEnvironment.DEVELOPMENT -> null
    AppEnvironment.STAGING -> AppEnvironment.stagingDomain()
    AppEnvironment.PRODUCTION -> AppEnvironment.prodDomain()
}

fun AppEnvironment.Companion.prodDomain(): String = "thehubproject-api.up.railway.app"
fun AppEnvironment.Companion.stagingDomain(): String = "thehubproject-api-staging.up.railway.app"