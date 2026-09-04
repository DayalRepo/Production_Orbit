package com.orbitai.erp.ui.datetime

import com.orbitai.erp.core.designsystem.component.datetime.OrbitCalendarDate
import com.orbitai.erp.core.designsystem.component.datetime.OrbitTimeOfDay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * The bridge between `kotlinx-datetime` and the design system's calendar types.
 *
 * ### Why this file exists at all
 *
 * `:core:designsystem` has no date library — see `OrbitCalendarDate` — so something has to convert.
 * Putting that something here rather than in the design system keeps the dependency on the consumer
 * side of the boundary, and gives the conversion one place to live instead of being retyped in every
 * screen that opens a picker.
 *
 * It is deliberately four one-line functions. There is no clock abstraction, no injected time source
 * and no interface: a screen that needs a fixed date for a test passes an `OrbitCalendarDate`
 * literal straight to `OrbitCalendarBounds`, because the design system already takes today as data.
 * The seam is in the component's signature, so it does not also need to be here.
 */

/** Today in the device's zone, as the design system's calendar date. */
@OptIn(ExperimentalTime::class)
fun orbitToday(zone: TimeZone = TimeZone.currentSystemDefault()): OrbitCalendarDate =
    Clock.System.todayIn(zone).toOrbitCalendarDate()

/**
 * Month arrives as an enum and leaves as a 1-based number.
 *
 * Via `ordinal` rather than the `monthNumber` property, which this version of `kotlinx-datetime` has
 * deprecated, or a `number` property, which it does not yet have — the enum's own ordering is the one
 * thing guaranteed to be stable across that migration.
 */
fun LocalDate.toOrbitCalendarDate(): OrbitCalendarDate =
    OrbitCalendarDate(year = year, month = month.ordinal + 1, day = day)

fun OrbitCalendarDate.toLocalDate(): LocalDate =
    LocalDate(year = year, month = Month.entries[month - 1], day = day)

fun LocalTime.toOrbitTimeOfDay(): OrbitTimeOfDay =
    OrbitTimeOfDay(hour = hour, minute = minute)

fun OrbitTimeOfDay.toLocalTime(): LocalTime =
    LocalTime(hour = hour, minute = minute)

/**
 * A launch-style remaining clock until the end of [endDate] (midnight at the start of the next day).
 *
 * `27 days · 26d 23h:04m:33s` — allocated days, then the live remainder. Zeroed rather than
 * negative once the span has elapsed, so the field never reads as a countdown running backwards.
 */
@OptIn(ExperimentalTime::class)
fun orbitRemainingCountdown(
    allocatedDays: Int,
    endDate: OrbitCalendarDate,
    now: Instant = Clock.System.now(),
    zone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    val deadline = LocalDate.fromEpochDays(endDate.toLocalDate().toEpochDays() + 1)
        .atStartOfDayIn(zone)
    val remaining = (deadline - now).inWholeSeconds.coerceAtLeast(0)
    val days = remaining / SecondsPerDay
    val hours = (remaining % SecondsPerDay) / 3600
    val minutes = (remaining % 3600) / 60
    val seconds = remaining % 60
    val allocated = if (allocatedDays == 1) "1 day" else "$allocatedDays days"
    return "$allocated · ${pad2(days)}d ${pad2(hours)}h:${pad2(minutes)}m:${pad2(seconds)}s"
}

private fun pad2(value: Long): String = value.toString().padStart(2, '0')

private const val SecondsPerDay = 86_400L
