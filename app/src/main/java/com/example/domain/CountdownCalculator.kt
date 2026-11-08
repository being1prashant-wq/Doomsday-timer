package com.example.domain

import java.time.Duration
import java.time.Instant
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

data class CountdownSnapshot(
    val months: Int,
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val isNegative: Boolean,
    val totalRemainingMillis: Long,
    val formattedColonString: String,
    val formattedCompactUnits: String,
    val formattedLargeRowOne: String,
    val formattedLargeRowTwo: String,
    val targetEpochMillis: Long
) {
    val monthsStr: String get() = String.format(Locale.US, "%02d", months)
    val daysStr: String get() = String.format(Locale.US, "%02d", days)
    val hoursStr: String get() = String.format(Locale.US, "%02d", hours)
    val minutesStr: String get() = String.format(Locale.US, "%02d", minutes)
    val secondsStr: String get() = String.format(Locale.US, "%02d", seconds)
    val signPrefix: String get() = if (isNegative) "-" else ""
}

object CountdownCalculator {
    val IST_ZONE: ZoneId = ZoneId.of("Asia/Kolkata")

    /**
     * Target timestamp: 18 December 2026, 12:00:00 AM (00:00:00 Asia/Kolkata IST)
     */
    val TARGET_DATE_IST: ZonedDateTime = ZonedDateTime.of(
        2026,
        12,
        18,
        0,
        0,
        0,
        0,
        IST_ZONE
    )

    val TARGET_EPOCH_MILLIS: Long = TARGET_DATE_IST.toInstant().toEpochMilli()

    /**
     * Performs true calendar-aware calculation between [nowInstant] and 18 Dec 2026 00:00 IST.
     * Never uses a 30-day fixed month approximation.
     * Supports negative/elapsed running countdown mode once the target timestamp passes.
     */
    fun calculate(nowMillis: Long = System.currentTimeMillis()): CountdownSnapshot {
        val nowInstant = Instant.ofEpochMilli(nowMillis)
        val nowIst = ZonedDateTime.ofInstant(nowInstant, IST_ZONE)
        return calculateBetween(nowIst, TARGET_DATE_IST)
    }

    fun calculateBetween(
        currentZoned: ZonedDateTime,
        targetZoned: ZonedDateTime = TARGET_DATE_IST
    ): CountdownSnapshot {
        val currentInTargetZone = currentZoned.withZoneSameInstant(targetZoned.zone)
        val isNegative = currentInTargetZone.isAfter(targetZoned)

        val start = if (isNegative) targetZoned else currentInTargetZone
        val end = if (isNegative) currentInTargetZone else targetZoned

        // Step 1: Calendar-aware Period calculation for Years, Months, and Days
        var dateCursor = start.toLocalDate()
        var endDate = end.toLocalDate()

        // If the time-of-day of 'end' is earlier than 'start', borrow 1 day so time delta is positive
        val startTime = start.toLocalTime()
        val endTime = end.toLocalTime()
        if (endTime.isBefore(startTime)) {
            endDate = endDate.minusDays(1)
        }

        val period = if (endDate.isBefore(dateCursor)) {
            Period.ZERO
        } else {
            Period.between(dateCursor, endDate)
        }

        val totalMonths = period.years * 12 + period.months
        val days = period.days

        // Advance start by the exact calendar months and days to compute remaining H:M:S
        val advancedDateTime = start
            .plusMonths(totalMonths.toLong())
            .plusDays(days.toLong())

        val timeDuration = Duration.between(advancedDateTime, end).abs()
        val totalSeconds = timeDuration.seconds
        val hours = ((totalSeconds / 3600) % 24).toInt()
        val minutes = ((totalSeconds % 3600) / 60).toInt()
        val seconds = (totalSeconds % 60).toInt()

        val sign = if (isNegative) "-" else ""
        val mStr = String.format(Locale.US, "%02d", totalMonths)
        val dStr = String.format(Locale.US, "%02d", days)
        val hStr = String.format(Locale.US, "%02d", hours)
        val minStr = String.format(Locale.US, "%02d", minutes)
        val sStr = String.format(Locale.US, "%02d", seconds)

        val formattedColon = "$sign$mStr : $dStr : $hStr : $minStr : $sStr"
        val monthUnit = if (totalMonths == 1) "MONTH" else "MONTHS"
        val dayUnit = if (days == 1) "DAY" else "DAYS"
        val hourUnit = if (hours == 1) "HOUR" else "HOURS"
        val minUnit = if (minutes == 1) "MINUTE" else "MINUTES"
        val secUnit = if (seconds == 1) "SECOND" else "SECONDS"

        val largeRowOne = "$sign$mStr $monthUnit : $dStr $dayUnit"
        val largeRowTwo = "$hStr $hourUnit : $minStr $minUnit : $sStr $secUnit"

        val rawDeltaMillis = targetZoned.toInstant().toEpochMilli() -
            currentInTargetZone.toInstant().toEpochMilli()

        return CountdownSnapshot(
            months = totalMonths,
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            isNegative = isNegative,
            totalRemainingMillis = rawDeltaMillis,
            formattedColonString = formattedColon,
            formattedCompactUnits = "M | D | H | M | S",
            formattedLargeRowOne = largeRowOne,
            formattedLargeRowTwo = largeRowTwo,
            targetEpochMillis = targetZoned.toInstant().toEpochMilli()
        )
    }
}
