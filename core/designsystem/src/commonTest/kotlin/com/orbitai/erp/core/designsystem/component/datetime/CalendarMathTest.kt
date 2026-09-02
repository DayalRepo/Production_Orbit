package com.orbitai.erp.core.designsystem.component.datetime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The arithmetic under the calendar grid.
 *
 * Worth testing at this level of detail because every one of these is a bug that renders as a
 * *plausible* calendar — a grid offset by one column, a February with the wrong length, a range that
 * counts one day short — rather than as a crash. A human reviewing a screenshot will not catch which
 * weekday the 1st fell on, so the test has to.
 */
class CalendarMathTest {

    @Test
    fun `day of week matches known dates across centuries`() {
        // 0 = Sunday. Spread across leap years, century boundaries and the Jan/Feb shift, which are
        // the three places Sakamoto's method could be wired up wrong and still look right in one month.
        val known = listOf(
            OrbitCalendarDate(2025, 6, 12) to 4, // Thursday, the reference image's selected day
            OrbitCalendarDate(2000, 1, 1) to 6, // Saturday, a leap year through the /400 rule
            OrbitCalendarDate(1900, 3, 1) to 4, // Thursday, a year the /100 rule makes non-leap
            OrbitCalendarDate(2024, 2, 29) to 4, // Thursday, an actual leap day
            OrbitCalendarDate(2027, 9, 21) to 2, // Tuesday, the light reference image
            OrbitCalendarDate(1970, 1, 1) to 4, // Thursday, the epoch
        )
        known.forEach { (date, expected) ->
            assertEquals(expected, date.dayOfWeek, "weekday of $date")
        }
    }

    @Test
    fun `february length follows the full leap year rule`() {
        assertEquals(29, OrbitCalendarDate.daysInMonth(2024, 2), "2024 divisible by 4")
        assertEquals(28, OrbitCalendarDate.daysInMonth(2025, 2), "2025 not divisible by 4")
        assertEquals(28, OrbitCalendarDate.daysInMonth(1900, 2), "1900 divisible by 100")
        assertEquals(29, OrbitCalendarDate.daysInMonth(2000, 2), "2000 divisible by 400")
    }

    @Test
    fun `an invalid month is rejected rather than silently clamped`() {
        assertFailsWith<IllegalArgumentException> { OrbitCalendarDate.daysInMonth(2025, 13) }
        assertFailsWith<IllegalArgumentException> { OrbitCalendarDate.daysInMonth(2025, 0) }
    }

    @Test
    fun `paging months rolls the year over in both directions`() {
        val june = OrbitYearMonth(2025, 6)
        assertEquals(OrbitYearMonth(2025, 7), june.plusMonths(1))
        assertEquals(OrbitYearMonth(2026, 1), june.plusMonths(7), "forward across December")
        assertEquals(OrbitYearMonth(2024, 12), june.plusMonths(-6), "backward across January")
        assertEquals(OrbitYearMonth(2024, 6), june.plusMonths(-12))
        assertEquals(OrbitYearMonth(2030, 6), june.plusMonths(60))
    }

    @Test
    fun `paging a full year in single steps returns to the same month`() {
        var cursor = OrbitYearMonth(2025, 1)
        repeat(12) { cursor = cursor.plusMonths(1) }
        assertEquals(OrbitYearMonth(2026, 1), cursor)
    }

    @Test
    fun `dates are ordered chronologically rather than lexically`() {
        val dates = listOf(
            OrbitCalendarDate(2025, 12, 1),
            OrbitCalendarDate(2025, 2, 28),
            OrbitCalendarDate(2026, 1, 1),
            OrbitCalendarDate(2025, 2, 3),
        ).sorted()

        assertEquals(
            listOf(
                OrbitCalendarDate(2025, 2, 3),
                OrbitCalendarDate(2025, 2, 28),
                OrbitCalendarDate(2025, 12, 1),
                OrbitCalendarDate(2026, 1, 1),
            ),
            dates,
        )
    }

    @Test
    fun `only today and later are selectable`() {
        val bounds = OrbitCalendarBounds(today = OrbitCalendarDate(2025, 6, 12))

        assertTrue(bounds.isSelectable(OrbitCalendarDate(2025, 6, 12)), "today itself")
        assertTrue(bounds.isSelectable(OrbitCalendarDate(2025, 6, 13)))
        assertTrue(bounds.isSelectable(OrbitCalendarDate(2030, 1, 1)))
        assertFalse(bounds.isSelectable(OrbitCalendarDate(2025, 6, 11)), "yesterday")
        assertFalse(bounds.isSelectable(OrbitCalendarDate(2024, 12, 31)))
    }

    @Test
    fun `the far future is bounded so paging cannot run away`() {
        val bounds = OrbitCalendarBounds(today = OrbitCalendarDate(2025, 6, 12))
        assertFalse(
            bounds.isSelectable(OrbitCalendarDate(2099, 1, 1)),
            "a date past the horizon must not be selectable",
        )
        assertTrue(bounds.isBeyondEnd(OrbitYearMonth(2099, 1)))
        assertFalse(bounds.isBeyondEnd(OrbitYearMonth(2026, 1)))
    }

    @Test
    fun `a month wholly before today is recognised so previous can be disabled`() {
        val bounds = OrbitCalendarBounds(today = OrbitCalendarDate(2025, 6, 12))
        assertTrue(bounds.isEntirelyPast(OrbitYearMonth(2025, 5)))
        assertFalse(
            bounds.isEntirelyPast(OrbitYearMonth(2025, 6)),
            "the current month is half selectable, so it is not past",
        )
    }
}

/** The 12-hour clock, whose only interesting cases are the two the naive formula gets wrong. */
class TimeOfDayTest {

    @Test
    fun `midnight and noon read as twelve rather than zero`() {
        assertEquals("12:00 AM", OrbitTimeOfDay(0, 0).format12Hour())
        assertEquals("12:00 PM", OrbitTimeOfDay(12, 0).format12Hour())
        assertEquals("12:30 AM", OrbitTimeOfDay(0, 30).format12Hour())
    }

    @Test
    fun `the meridiem flips at noon and not at one`() {
        assertEquals("AM", OrbitTimeOfDay(11, 59).meridiem)
        assertEquals("PM", OrbitTimeOfDay(12, 0).meridiem, "noon is PM")
        assertEquals("PM", OrbitTimeOfDay(23, 59).meridiem)
    }

    @Test
    fun `afternoon hours count down from twelve`() {
        assertEquals("1:00 PM", OrbitTimeOfDay(13, 0).format12Hour())
        assertEquals("11:45 PM", OrbitTimeOfDay(23, 45).format12Hour())
    }

    @Test
    fun `minutes are zero padded but hours are not`() {
        assertEquals("9:05 AM", OrbitTimeOfDay(9, 5).format12Hour())
    }

    @Test
    fun `an out of range hour or minute is rejected at construction`() {
        assertFailsWith<IllegalArgumentException> { OrbitTimeOfDay(24, 0) }
        assertFailsWith<IllegalArgumentException> { OrbitTimeOfDay(-1, 0) }
        assertFailsWith<IllegalArgumentException> { OrbitTimeOfDay(12, 60) }
    }

    @Test
    fun `slots are inclusive of both ends`() {
        val slots = OrbitTimeOfDay.slots(
            from = OrbitTimeOfDay(9, 0),
            until = OrbitTimeOfDay(10, 0),
            stepMinutes = 30,
        )
        assertEquals(
            listOf("9:00 AM", "9:30 AM", "10:00 AM"),
            slots.map { it.format12Hour() },
        )
    }

    @Test
    fun `slots roll across the hour boundary correctly`() {
        val slots = OrbitTimeOfDay.slots(
            from = OrbitTimeOfDay(11, 30),
            until = OrbitTimeOfDay(12, 30),
            stepMinutes = 15,
        )
        assertEquals(
            listOf("11:30 AM", "11:45 AM", "12:00 PM", "12:15 PM", "12:30 PM"),
            slots.map { it.format12Hour() },
        )
    }

    @Test
    fun `the working day default covers nine to six in quarter hours`() {
        val slots = OrbitTimeOfDay.workingDay()
        assertEquals(37, slots.size, "09:00 to 18:00 inclusive at 15 minutes")
        assertEquals("9:00 AM", slots.first().format12Hour())
        assertEquals("6:00 PM", slots.last().format12Hour())
    }
}

/** The display formats the field and summary line read back to the user. */
class DateFormatTest {

    @Test
    fun `the slashed format is day first and zero padded`() {
        assertEquals("05/09/2027", OrbitCalendarDate(2027, 9, 5).formatSlashed())
        assertEquals("21/09/2027", OrbitCalendarDate(2027, 9, 21).formatSlashed())
    }

    @Test
    fun `the medium format abbreviates the month and does not pad the day`() {
        assertEquals("5 Sep 2027", OrbitCalendarDate(2027, 9, 5).formatMedium())
        assertEquals("12 Jun 2025", OrbitCalendarDate(2025, 6, 12).formatMedium())
    }

    @Test
    fun `a selection reads back as both halves`() {
        val selection = OrbitDateTimeSelection(
            date = OrbitCalendarDate(2025, 6, 12),
            time = OrbitTimeOfDay(10, 0),
        )
        assertEquals("Thursday, 12/06/2025 · 10:00 AM", selection.format())
        assertEquals("12 Jun 2025 at 10:00 AM", selection.formatSentence())
    }
}
