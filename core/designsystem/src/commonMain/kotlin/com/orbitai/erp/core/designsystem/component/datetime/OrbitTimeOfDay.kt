package com.orbitai.erp.core.designsystem.component.datetime

import androidx.compose.runtime.Immutable

/**
 * A time of day, stored in 24-hour form and displayed in 12-hour form.
 *
 * ### Why the storage and the display disagree
 *
 * Storing `hour` as 0..23 with a separate AM/PM flag would make two fields able to contradict each
 * other — `hour = 15, meridiem = AM` is representable and meaningless. Keeping one 24-hour number and
 * deriving the meridiem on the way out makes that state unreachable rather than merely discouraged.
 *
 * The conversion has one trap, and it is the reason this is a type rather than a string format call:
 * midnight and noon. Hour 0 displays as **12 AM** and hour 12 as **12 PM**, so a naive `hour % 12`
 * renders both as "0", and a naive `if (hour > 12) hour - 12` renders midnight as "0 AM". Both are
 * wrong in a way that only shows up twice a day.
 */
@Immutable
data class OrbitTimeOfDay(
    val hour: Int,
    val minute: Int,
) : Comparable<OrbitTimeOfDay> {

    init {
        require(hour in 0..23) { "hour must be 0..23 but was $hour" }
        require(minute in 0..59) { "minute must be 0..59 but was $minute" }
    }

    override fun compareTo(other: OrbitTimeOfDay): Int =
        minutesFromMidnight.compareTo(other.minutesFromMidnight)

    val minutesFromMidnight: Int get() = hour * 60 + minute

    val isAfternoon: Boolean get() = hour >= 12

    /** `AM` or `PM`. */
    val meridiem: String get() = if (isAfternoon) "PM" else "AM"

    /** 1..12, with both midnight and noon reading as 12. */
    val hour12: Int
        get() = when (val h = hour % 12) {
            0 -> 12
            else -> h
        }

    /** `10:00 AM` — the form the field displays. */
    fun format12Hour(): String = "$hour12:${pad2(minute)} $meridiem"

    companion object {
        /**
         * Every [stepMinutes] slot from [from] up to and including [until].
         *
         * Generated rather than hard-coded so a caller can offer 15-minute site-visit slots and
         * 60-minute delivery windows from one component. Walking minutes-from-midnight rather than
         * incrementing an hour/minute pair keeps the rollover in one place.
         */
        fun slots(
            from: OrbitTimeOfDay,
            until: OrbitTimeOfDay,
            stepMinutes: Int,
        ): List<OrbitTimeOfDay> {
            require(stepMinutes > 0) { "stepMinutes must be positive but was $stepMinutes" }
            require(from <= until) { "from ($from) must not be after until ($until)" }

            val slots = mutableListOf<OrbitTimeOfDay>()
            var cursor = from.minutesFromMidnight
            val end = until.minutesFromMidnight
            while (cursor <= end) {
                slots += OrbitTimeOfDay(hour = cursor / 60, minute = cursor % 60)
                cursor += stepMinutes
            }
            return slots
        }

        /**
         * A working day in quarter-hour slots, 09:00 to 18:00.
         *
         * The default because it is the shape of nearly every scheduling decision in this app — a
         * site visit, an inspection, a delivery window — and a picker that opens on a plausible list
         * is faster to judge than one that opens on midnight.
         */
        fun workingDay(): List<OrbitTimeOfDay> = slots(
            from = OrbitTimeOfDay(9, 0),
            until = OrbitTimeOfDay(18, 0),
            stepMinutes = 15,
        )
    }
}

/**
 * A date and a time together — what a single picker returns once both halves are chosen.
 *
 * Both are non-null. A half-made selection is represented by the picker holding a null
 * [OrbitDateTimeSelection] rather than by this type carrying nullable fields, so a caller that has a
 * value cannot be handed one that is missing its time.
 */
@Immutable
data class OrbitDateTimeSelection(
    val date: OrbitCalendarDate,
    val time: OrbitTimeOfDay,
) {
    /** `Thursday, 12/06/2025 · 10:00 AM` — the field's display value. */
    fun format(): String = "${date.formatWithWeekday()} · ${time.format12Hour()}"

    /** `12 Jun 2025 at 10:00 AM` — for the confirmation sentence under a picker. */
    fun formatSentence(): String = "${date.formatMedium()} at ${time.format12Hour()}"
}
