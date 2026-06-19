package com.noemi_balazs.animator.util.ext

import kotlinx.datetime.LocalDateTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

fun Long.formatDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    return instant.toLocalDateTime(TimeZone.UTC)
        .format(
            LocalDateTime.Format {
                day()
                chars("-")
                monthNumber()
                chars("-")
                year()
            }
        )
}