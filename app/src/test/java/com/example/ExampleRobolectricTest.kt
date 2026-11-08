package com.example

import com.example.data.DigitAnimationStyle
import com.example.data.DoomsdaySettings
import com.example.data.GlassPreset
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun `default settings match dark glass and flip clock`() {
        val defaults = DoomsdaySettings()
        assertEquals(GlassPreset.DARK_GLASS, defaults.preset)
        assertEquals(DigitAnimationStyle.FLIP_CLOCK, defaults.animationStyle)
    }
}
