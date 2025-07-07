package com.francescobottino.thehubproject.client_features.core.usecase

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

object GetRelativeTimeUseCase {
    operator fun invoke(from: Instant, to: Instant = Clock.System.now()): String {
        return when {
            //Past
            from < to -> {
                val duration = to - from

                when {
                    duration.inWholeSeconds < 60 -> "Less than a minute ago"
                    duration.inWholeMinutes < 2 -> "1 minute ago"
                    duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes} minutes ago"
                    duration.inWholeHours < 2 -> "1 hour ago"
                    duration.inWholeHours < 24 -> "${duration.inWholeHours} hours ago"
                    duration.inWholeDays < 2 -> "Yesterday"
                    duration.inWholeDays < 30 -> "${duration.inWholeDays} days ago"
                    duration.inWholeDays / 365 < 2 -> "1 year ago"
                    else -> "${duration.inWholeDays / 365} years ago"
                }
            }

            //Future
            from > to -> {
                val duration = from - to

                when {
                    duration.inWholeSeconds < 60 -> "In less than 1 minute"
                    duration.inWholeMinutes < 2 -> "In 1 minute"
                    duration.inWholeMinutes < 60 -> "In ${duration.inWholeMinutes} minutes"
                    duration.inWholeHours < 2 -> "In 1 hour"
                    duration.inWholeHours < 24 -> "In ${duration.inWholeHours} hours"
                    duration.inWholeDays < 2 -> "Tomorrow"
                    duration.inWholeDays < 30 -> "In ${duration.inWholeDays} days"
                    duration.inWholeDays / 365 < 2 -> "Next year"
                    else -> "In ${duration.inWholeDays / 365} years"
                }
            }

            else -> "Right now"
        }
    }

    fun Instant.relativeTime(to: Instant = Clock.System.now()): String = invoke(this, to)
}