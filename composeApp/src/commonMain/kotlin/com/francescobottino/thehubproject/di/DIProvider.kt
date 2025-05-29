package com.francescobottino.thehubproject.di

import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.DirectDI
import org.kodein.di.direct
import org.kodein.di.instance

/**
 * Global DI provider. Must be initialized by each platform.
 */
object DIProvider : DIAware {
    override lateinit var di: DI

    val directDI: DirectDI
        get() = di.direct
}