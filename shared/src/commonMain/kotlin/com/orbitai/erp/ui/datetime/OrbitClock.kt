package com.orbitai.erp.ui.datetime

import com.orbitai.erp.core.designsystem.component.datetime.OrbitCalendarDate
import com.orbitai.erp.core.designsystem.component.datetime.OrbitTimeOfDay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
