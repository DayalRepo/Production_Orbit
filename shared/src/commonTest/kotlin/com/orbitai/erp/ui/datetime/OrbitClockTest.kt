package com.orbitai.erp.ui.datetime

import com.orbitai.erp.core.designsystem.component.datetime.OrbitCalendarDate
import com.orbitai.erp.core.designsystem.component.datetime.OrbitTimeOfDay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * The conversion between `kotlinx-datetime` and the design system's calendar types.
 *
 * Worth a test for one reason: the month hop is `ordinal + 1` in one direction and `entries[n - 1]` in
 * the other, and an off-by-one there does not fail — it produces a date one month away, which looks
 * entirely reasonable on a calendar. A round trip over all twelve months is the cheapest way to pin it.
 */
class OrbitClockTest {

    @Test
    fun `every month survives a round trip through both types`() {
        (1..12).forEach { month ->
            val original = OrbitCalendarDate(year = 2026, month = month, day = 15)
            val roundTripped = original.toLocalDate().toOrbitCalendarDate()
            assertEquals(original, roundTripped, "month $month")
        }
    }

    @Test
    fun `january and december map to the right ends of the enum`() {
        // The two months an ordinal mistake would land on, and the two a reviewer would not question.
        assertEquals(
            LocalDate(2026, 1, 1),
            OrbitCalendarDate(2026, 1, 1).toLocalDate(),
        )
        assertEquals(
            LocalDate(2026, 12, 31),
            OrbitCalendarDate(2026, 12, 31).toLocalDate(),
        )
        assertEquals(
            OrbitCalendarDate(2026, 1, 1),
            LocalDate(2026, 1, 1).toOrbitCalendarDate(),
        )
        assertEquals(
            OrbitCalendarDate(2026, 12, 31),
            LocalDate(2026, 12, 31).toOrbitCalendarDate(),
        )
    }

    @Test
    fun `a leap day converts in both directions`() {
        val leapDay = OrbitCalendarDate(2028, 2, 29)
        assertEquals(LocalDate(2028, 2, 29), leapDay.toLocalDate())
        assertEquals(leapDay, LocalDate(2028, 2, 29).toOrbitCalendarDate())
    }

    @Test
    fun `time converts without losing the hour or minute`() {
        val times = listOf(
            OrbitTimeOfDay(0, 0),
            OrbitTimeOfDay(12, 0),
            OrbitTimeOfDay(13, 45),
            OrbitTimeOfDay(23, 59),
        )
        times.forEach { original ->
            assertEquals(original, original.toLocalTime().toOrbitTimeOfDay(), "time $original")
        }
    }

    @Test
    @OptIn(ExperimentalTime::class)
    fun `remaining countdown pairs allocated days with a live clock`() {
        val end = OrbitCalendarDate(2026, 9, 4)
        val zone = TimeZone.UTC
        val after = Instant.parse("2026-09-06T00:00:00Z")
        assertEquals(
            "5 days · 00d 00h:00m:00s",
            orbitRemainingCountdown(allocatedDays = 5, endDate = end, now = after, zone = zone),
        )
        val during = Instant.parse("2026-09-03T12:00:00Z")
        assertEquals(
            "5 days · 01d 12h:00m:00s",
            orbitRemainingCountdown(allocatedDays = 5, endDate = end, now = during, zone = zone),
        )
    }

    @Test
    fun `the weekday the design system computes agrees with the date library`() {
        // The grid's column placement rests entirely on `OrbitCalendarDate.dayOfWeek`, which is hand
        // -rolled Sakamoto rather than a library call. This is the check that the hand-rolled version
        // and the real implementation have not drifted — over four years, every day.
        var date = LocalDate(2026, 1, 1)
        val end = LocalDate(2030, 1, 1)
        while (date < end) {
            val orbit = date.toOrbitCalendarDate()
            // The library's enum runs 0 = Monday through 6 = Sunday; ours runs 0 = Sunday through
            // 6 = Saturday, so shift by one and wrap.
            val expected = (date.dayOfWeek.ordinal + 1) % 7
            assertEquals(expected, orbit.dayOfWeek, "weekday of $date")
            date = LocalDate.fromEpochDays(date.toEpochDays() + 1)
        }
    }
}
