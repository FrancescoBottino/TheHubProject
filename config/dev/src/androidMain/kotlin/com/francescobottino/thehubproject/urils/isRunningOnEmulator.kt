package com.francescobottino.thehubproject

import android.os.Build

fun isRunningOnEmulator(): Boolean {
    // Check for a combination of properties that are common on emulators.
    return (
            // AVD-based emulators
            Build.FINGERPRINT.startsWith("generic") ||
                    Build.FINGERPRINT.startsWith("unknown") ||
                    Build.MODEL.contains("google_sdk", ignoreCase = true) ||
                    Build.MODEL.contains("Emulator", ignoreCase = true) ||
                    Build.MODEL.contains("Android SDK built for x86", ignoreCase = true) ||

                    // Genymotion
                    Build.MANUFACTURER.contains("Genymotion", ignoreCase = true) ||

                    // Generic indicators
                    (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||

                    // Hardware properties for common emulators (like goldfish, ranchu for AVD)
                    Build.HARDWARE.contains("goldfish", ignoreCase = true) ||
                    Build.HARDWARE.contains("ranchu", ignoreCase = true) ||
                    Build.HARDWARE.contains("vbox86", ignoreCase = true) || // For VirtualBox-based emulators

                    // Product properties
                    Build.PRODUCT.contains("sdk", ignoreCase = true) ||
                    Build.PRODUCT.contains("google_sdk", ignoreCase = true) ||
                    Build.PRODUCT.contains("sdk_google_phone", ignoreCase = true) ||
                    Build.PRODUCT.contains("sdk_gphone", ignoreCase = true) ||
                    Build.PRODUCT.contains("vbox86p", ignoreCase = true) ||
                    Build.PRODUCT.contains("emulator", ignoreCase = true) ||
                    Build.PRODUCT.contains("simulator", ignoreCase = true)
            )
}