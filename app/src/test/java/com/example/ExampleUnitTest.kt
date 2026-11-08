package com.example

import com.example.domain.CountdownCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class ExampleUnitTest {

    @Test
    fun countdownCalculator_exactMonthDayHourMinuteSecond_beforeTarget() {
        // 08 Nov 2026, 11:00:59 AM IST -> Target: 18 Dec 2026, 00:00:00 AM IST
        // Remaining: 1 Month (Nov 8 to Dec 8), 9 Days (Dec 8 11:00:59 to Dec 17 11:00:59),
        // 12 Hours (11:00:59 to 23:00:59), 59 Minutes (23:00:59 to 23:59:59), 01 Second
        val current = ZonedDateTime.of(
            2026, 11, 8, 11, 0, 59, 0, CountdownCalculator.IST_ZONE
        )
        val snapshot = CountdownCalculator.calculateBetween(current)

        assertFalse(snapshot.isNegative)
        assertEquals(1, snapshot.months)
        assertEquals(9, snapshot.days)
        assertEquals(12, snapshot.hours)
        assertEquals(59, snapshot.minutes)
        assertEquals(1, snapshot.seconds)
        assertEquals("01 : 09 : 12 : 59 : 01", snapshot.formattedColonString)
    }

    @Test
    fun countdownCalculator_targetDateReached_zeroSnapshot() {
        val atTarget = ZonedDateTime.of(
            2026, 12, 18, 0, 0, 0, 0, CountdownCalculator.IST_ZONE
        )
        val snapshot = CountdownCalculator.calculateBetween(atTarget)

        assertFalse(snapshot.isNegative)
        assertEquals(0, snapshot.months)
        assertEquals(0, snapshot.days)
        assertEquals(0, snapshot.hours)
        assertEquals(0, snapshot.minutes)
        assertEquals(0, snapshot.seconds)
        assertEquals("00 : 00 : 00 : 00 : 00", snapshot.formattedColonString)
    }

    @Test
    fun countdownCalculator_negativeElapsedMode_afterTarget() {
        val afterTarget = ZonedDateTime.of(
            2026, 12, 18, 0, 0, 1, 0, CountdownCalculator.IST_ZONE
        )
        val snapshot = CountdownCalculator.calculateBetween(afterTarget)

        assertTrue(snapshot.isNegative)
        assertEquals(0, snapshot.months)
        assertEquals(0, snapshot.days)
        assertEquals(0, snapshot.hours)
        assertEquals(0, snapshot.minutes)
        assertEquals(1, snapshot.seconds)
        assertEquals("-00 : 00 : 00 : 00 : 01", snapshot.formattedColonString)
    }

    @Test
    fun countdownCalculator_respectsDifferentDeviceTimeZone() {
        // Same instant expressed in UTC (2026-12-17 18:29:59 UTC == 2026-12-17 23:59:59 IST)
        val utcCurrent = ZonedDateTime.of(
            2026, 12, 17, 18, 29, 59, 0, ZoneId.of("UTC")
        )
        val snapshot = CountdownCalculator.calculateBetween(utcCurrent)

        assertFalse(snapshot.isNegative)
        assertEquals(0, snapshot.months)
        assertEquals(0, snapshot.days)
        assertEquals(0, snapshot.hours)
        assertEquals(0, snapshot.minutes)
        assertEquals(1, snapshot.seconds)
        assertEquals("00 : 00 : 00 : 00 : 01", snapshot.formattedColonString)
    }
}
