package com.orbitai.erp.core.designsystem.component.datetime

import androidx.compose.runtime.Immutable

/**
 * A calendar day, with the arithmetic a month grid needs and nothing else.
 *
 * ### Why this exists instead of `kotlinx.datetime.LocalDate`
 *
 * `:core:designsystem` has no dependencies beyond Compose itself — deliberately, so that a component
 * library cannot drag a date library, a serialiser or an HTTP client into every consumer. Adding
 * `kotlinx-datetime` here to render a 7x6 grid of integers would spend that boundary on arithmetic
 * that fits in forty lines.
 *
 * So this is not a date *type* in the domain sense. It has no time zone, no instant, no notion of
 * "now", and it cannot tell you what today is — see [OrbitCalendarBounds] for why that is a feature.
 * Feature modules that do own `kotlinx-datetime` convert at the boundary:
 *
 * ```
 * val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
 * OrbitCalendarDate(today.year, today.monthNumber, today.dayOfMonth)
 * ```
 *
 * @param month 1-based, so January is 1. Matches how every calendar UI and every human writes it,
 *   and mismatching `java.util.Calendar`'s 0-based months is worth it to avoid off-by-one bugs in
 *   call sites that are reading a date off a screen.
 */
@Immutable
data class OrbitCalendarDate(
    val year: Int,
    val month: Int,
    val day: Int,
) : Comparable<OrbitCalendarDate> {

    /** Ordering is chronological, which makes range checks `start <= day && day <= end`. */
    override fun compareTo(other: OrbitCalendarDate): Int {
        year.compareTo(other.year).let { if (it != 0) return it }
        month.compareTo(other.month).let { if (it != 0) return it }
        return day.compareTo(other.day)
    }

    /** The month this day falls in, for grid navigation. */
    val yearMonth: OrbitYearMonth get() = OrbitYearMonth(year, month)

    /**
     * Day of the week, `0` = Sunday through `6` = Saturday.
     *
     * Sakamoto's method. Chosen over Zeller's because it needs no special-casing of January and
     * February beyond one decrement, and over counting days from an epoch because that overflows
     * quietly on far-future years while this does not.
     */
    val dayOfWeek: Int
        get() {
            val offsets = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
            var y = year
            if (month < 3) y -= 1
            return (y + y / 4 - y / 100 + y / 400 + offsets[month - 1] + day).mod(7)
        }

    /** `dd/MM/yyyy`, zero-padded. The numeric portion of what the field displays. */
    fun formatSlashed(): String =
        "${pad2(day)}/${pad2(month)}/$year"

    /** `Thursday, 12/06/2025` — weekday plus slashed date for field display. */
    fun formatWithWeekday(): String =
        "${OrbitWeekdayNames.full(dayOfWeek)}, ${formatSlashed()}"

    /** `12 Jun 2025` — for summary lines, where a slashed date reads as a serial number. */
    fun formatMedium(): String =
        "$day ${OrbitMonthNames.short(month)} $year"

    companion object {
        fun isLeapYear(year: Int): Boolean =
            year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

        fun daysInMonth(year: Int, month: Int): Int = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> throw IllegalArgumentException("month must be 1..12 but was $month")
        }
    }
}

/**
 * A month in a year — the unit a calendar grid is paged by.
 *
 * Separate from [OrbitCalendarDate] because "which month am I looking at" and "which day is selected"
 * are independent pieces of state. Collapsing them into a nullable selected date means paging the
 * grid either mutates the selection or is impossible before the user has picked anything.
 */
@Immutable
data class OrbitYearMonth(val year: Int, val month: Int) : Comparable<OrbitYearMonth> {

    override fun compareTo(other: OrbitYearMonth): Int {
        year.compareTo(other.year).let { if (it != 0) return it }
        return month.compareTo(other.month)
    }

    val lengthInDays: Int get() = OrbitCalendarDate.daysInMonth(year, month)

    /** Day of the week the 1st falls on, `0` = Sunday. Sets the grid's leading blank count. */
    val firstDayOfWeek: Int get() = OrbitCalendarDate(year, month, 1).dayOfWeek

    fun day(day: Int): OrbitCalendarDate = OrbitCalendarDate(year, month, day)

    /** Negative [months] steps backwards. Handles year rollover in both directions. */
    fun plusMonths(months: Int): OrbitYearMonth {
        // Shift to a 0-based absolute month count so the rollover is one division rather than a
        // loop with two boundary cases.
        val absolute = year * 12 + (month - 1) + months
        return OrbitYearMonth(absolute.floorDiv(12), absolute.mod(12) + 1)
    }

    fun label(): String = "${OrbitMonthNames.full(month)} $year"
}

/**
 * The selectable window: today, and nothing before it.
 *
 * ### Why today is a parameter rather than a clock read
 *
 * The design system cannot read a clock — it has no date library and no time zone — but even if it
 * could, taking today as data is the better shape. It makes "the day before the boundary is
 * unselectable" a test that runs in microseconds without mocking a clock, it lets a screen freeze the
 * calendar for a scripted demo, and it means a picker inside a form that opened yesterday does not
 * silently change which days are legal while the user is looking at it.
 *
 * @param today the first selectable day.
 * @param lastSelectableYear how far forward paging may go. A calendar with no upper bound lets a
 *   fat-fingered `next` land the user in the year 3000 with no way back but 12,000 taps.
 */
@Immutable
data class OrbitCalendarBounds(
    val today: OrbitCalendarDate,
    val lastSelectableYear: Int = today.year + DefaultYearsAhead,
) {
    /** Present and future only, which is the rule for scheduling work that has not happened yet. */
    fun isSelectable(date: OrbitCalendarDate): Boolean =
        date >= today && date.year <= lastSelectableYear

    /** True when [month] contains no selectable day at all, so `previous` should be disabled. */
    fun isEntirelyPast(month: OrbitYearMonth): Boolean =
        month < today.yearMonth

    fun isBeyondEnd(month: OrbitYearMonth): Boolean =
        month.year > lastSelectableYear

    private companion object {
        /** Seventeen years ahead — ends at 2043 when today is 2026, excluding 2044 and 2045. */
        const val DefaultYearsAhead = 17
    }
}

/**
 * Month names, in English only.
 *
 * Not localised, and that is a known gap rather than an oversight: the rest of the design system
 * takes its strings from the call site for exactly this reason, but a month name is derived from a
 * number rather than passed in, so there is nowhere for a caller to put a translation. When the app
 * grows a second locale this becomes a parameter on the picker.
 */
internal object OrbitMonthNames {
    private val full = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    )

    fun full(month: Int): String = full[month - 1]

    /** First three letters, which is the conventional abbreviation for all twelve in English. */
    fun short(month: Int): String = full[month - 1].take(3)
}

/**
 * Weekday captions, starting Sunday to match [OrbitCalendarDate.dayOfWeek].
 *
 * Two letters, not three. Seven columns of "Wed" need more width than seven columns of two-digit
 * numbers, so three-letter captions set the column width for the whole grid and the numbers end up
 * swimming in it — on a phone the header ran wider than the cells and the labels stopped sitting over
 * their columns. Two letters are narrower than "30", which puts the numbers back in charge of the
 * grid's proportions.
 */
internal val OrbitWeekdayLabels = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")

internal object OrbitWeekdayNames {
    private val full = listOf(
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
    )

    fun full(dayOfWeek: Int): String = full[dayOfWeek]
}

internal fun pad2(value: Int): String = if (value < 10) "0$value" else value.toString()
