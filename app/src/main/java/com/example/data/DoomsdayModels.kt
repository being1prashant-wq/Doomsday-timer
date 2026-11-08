package com.example.data

enum class GlassPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val glassOpacity: Float,
    val borderOpacity: Float,
    val blurIntensity: Float,
    val textOpacity: Float,
    val backgroundTransparency: Float,
    val textColorHex: Long,
    val accentColorHex: Long,
    val surfaceColorHex: Long
) {
    DARK_GLASS(
        id = "DARK_GLASS",
        title = "Dark Glass",
        subtitle = "Smoked obsidian with subtle silver highlights (Default)",
        glassOpacity = 0.68f,
        borderOpacity = 0.24f,
        blurIntensity = 18f,
        textOpacity = 0.98f,
        backgroundTransparency = 0.15f,
        textColorHex = 0xFFF8FAFC,
        accentColorHex = 0xFFCBD5E1,
        surfaceColorHex = 0xFF12151E
    ),
    PURE_GLASS(
        id = "PURE_GLASS",
        title = "Pure Glass",
        subtitle = "Crystal high-translucency specular layer",
        glassOpacity = 0.26f,
        borderOpacity = 0.42f,
        blurIntensity = 24f,
        textOpacity = 1.0f,
        backgroundTransparency = 0.65f,
        textColorHex = 0xFFFFFFFF,
        accentColorHex = 0xFFE2E8F0,
        surfaceColorHex = 0xFF1E2430
    ),
    FROSTED_GLASS(
        id = "FROSTED_GLASS",
        title = "Frosted Glass",
        subtitle = "Dense diffused matte silica surface",
        glassOpacity = 0.52f,
        borderOpacity = 0.35f,
        blurIntensity = 30f,
        textOpacity = 0.95f,
        backgroundTransparency = 0.35f,
        textColorHex = 0xFFF1F5F9,
        accentColorHex = 0xFF94A3B8,
        surfaceColorHex = 0xFF1B202B
    ),
    TRANSPARENT_MINIMAL(
        id = "TRANSPARENT_MINIMAL",
        title = "Transparent Minimal",
        subtitle = "Zero-fill floating hairline frame",
        glassOpacity = 0.06f,
        borderOpacity = 0.16f,
        blurIntensity = 4f,
        textOpacity = 0.92f,
        backgroundTransparency = 0.94f,
        textColorHex = 0xFFF8FAFC,
        accentColorHex = 0xFF94A3B8,
        surfaceColorHex = 0xFF0A0C10
    ),
    MIDNIGHT(
        id = "MIDNIGHT",
        title = "Midnight",
        subtitle = "Deep OLED void with razor-sharp contrast",
        glassOpacity = 0.88f,
        borderOpacity = 0.14f,
        blurIntensity = 10f,
        textOpacity = 1.0f,
        backgroundTransparency = 0.05f,
        textColorHex = 0xFFFFFFFF,
        accentColorHex = 0xFF64748B,
        surfaceColorHex = 0xFF050608
    ),
    SILVER_GLASS(
        id = "SILVER_GLASS",
        title = "Silver Glass",
        subtitle = "Metallic titanium sheen with luminous rim",
        glassOpacity = 0.44f,
        borderOpacity = 0.55f,
        blurIntensity = 22f,
        textOpacity = 1.0f,
        backgroundTransparency = 0.45f,
        textColorHex = 0xFFFFFFFF,
        accentColorHex = 0xFFE2E8F0,
        surfaceColorHex = 0xFF232936
    );

    companion object {
        fun fromId(id: String): GlassPreset =
            entries.firstOrNull { it.id == id } ?: DARK_GLASS
    }
}

enum class DigitAnimationStyle(
    val id: String,
    val title: String,
    val description: String
) {
    FLIP_CLOCK(
        id = "FLIP_CLOCK",
        title = "Flip Clock",
        description = "Digits rotate vertically like a split-flap mechanical clock (Default)"
    ),
    ANALOG_WHITE_STICKS(
        id = "ANALOG_WHITE_STICKS",
        title = "Analog Dark BG + White Sticks",
        description = "Precision dark chronometer dial with crisp white stick markers & digital readout"
    ),
    SLIDE_UP(
        id = "SLIDE_UP",
        title = "Slide Up",
        description = "Incoming digit glides smoothly upward into position"
    ),
    SLIDE_DOWN(
        id = "SLIDE_DOWN",
        title = "Slide Down",
        description = "Incoming digit descends cleanly from above"
    ),
    FADE(
        id = "FADE",
        title = "Fade",
        description = "Soft atmospheric crossfade between outgoing and incoming numerals"
    ),
    SCALE(
        id = "SCALE",
        title = "Scale",
        description = "Digits contract and expand with depth focus"
    ),
    ROLLING(
        id = "ROLLING",
        title = "Rolling Odometer",
        description = "Continuous vertical odometer wheel rotation"
    ),
    SLOT_MACHINE(
        id = "SLOT_MACHINE",
        title = "Slot Machine",
        description = "High-velocity vertical reel snap with overshoot"
    ),
    BLUR_TRANSITION(
        id = "BLUR_TRANSITION",
        title = "Blur Transition",
        description = "Optical defocus and sharpen morph between digits"
    ),
    MECHANICAL_CLOCK(
        id = "MECHANICAL_CLOCK",
        title = "Mechanical Clock",
        description = "Escapement gear tick with crisp micro-recoil"
    ),
    MINIMAL_INSTANT(
        id = "MINIMAL_INSTANT",
        title = "Minimal Instant",
        description = "Zero-latency precision digital cut"
    ),
    BOUNCE(
        id = "BOUNCE",
        title = "Bounce",
        description = "Elastic spring-physics arrival transition"
    ),
    TYPEWRITER(
        id = "TYPEWRITER",
        title = "Typewriter",
        description = "Staggered character strike replacement with cursor flash"
    );

    companion object {
        fun fromId(id: String): DigitAnimationStyle =
            entries.firstOrNull { it.id == id } ?: FLIP_CLOCK
    }
}

enum class AnimationDirection(val id: String, val label: String) {
    DOWN("DOWN", "Downward"),
    UP("UP", "Upward"),
    ALTERNATE("ALTERNATE", "Alternating");

    companion object {
        fun fromId(id: String): AnimationDirection =
            entries.firstOrNull { it.id == id } ?: DOWN
    }
}

enum class EasingCurveOption(val id: String, val label: String) {
    FAST_OUT_SLOW_IN("FAST_OUT_SLOW_IN", "Smooth (FastOutSlowIn)"),
    EASE_IN_OUT_CUBIC("EASE_IN_OUT_CUBIC", "Horological Cubic"),
    SPRING_PHYSICS("SPRING_PHYSICS", "Elastic Spring"),
    LINEAR("LINEAR", "Linear Mechanical");

    companion object {
        fun fromId(id: String): EasingCurveOption =
            entries.firstOrNull { it.id == id } ?: FAST_OUT_SLOW_IN
    }
}

enum class HorizontalAlignOption(val id: String, val label: String) {
    LEFT("LEFT", "Left"),
    CENTER("CENTER", "Center"),
    RIGHT("RIGHT", "Right");

    companion object {
        fun fromId(id: String): HorizontalAlignOption =
            entries.firstOrNull { it.id == id } ?: CENTER
    }
}

enum class VerticalAlignOption(val id: String, val label: String) {
    TOP("TOP", "Top"),
    CENTER("CENTER", "Middle"),
    BOTTOM("BOTTOM", "Bottom");

    companion object {
        fun fromId(id: String): VerticalAlignOption =
            entries.firstOrNull { it.id == id } ?: CENTER
    }
}

enum class WidgetLayoutType(val id: String, val label: String) {
    COMPACT("COMPACT", "Compact (M | D | H | M | S)"),
    LARGE("LARGE", "Large (Full Unit Labels)");

    companion object {
        fun fromId(id: String): WidgetLayoutType =
            entries.firstOrNull { it.id == id } ?: COMPACT
    }
}

data class DoomsdaySettings(
    // Glassmorphism customization
    val preset: GlassPreset = GlassPreset.DARK_GLASS,
    val fontSizeSp: Float = 28f,
    val fontWeightVal: Int = 700,
    val textOpacity: Float = 0.98f,
    val glassOpacity: Float = 0.68f,
    val borderOpacity: Float = 0.24f,
    val blurIntensity: Float = 18f,
    val letterSpacingEm: Float = 0.04f,
    val numberSpacingDp: Float = 6f,
    val horizontalAlignment: HorizontalAlignOption = HorizontalAlignOption.CENTER,
    val verticalAlignment: VerticalAlignOption = VerticalAlignOption.CENTER,
    val widgetWidthDp: Float = 340f,
    val widgetHeightDp: Float = 156f,
    val cornerRadiusDp: Float = 24f,
    val textColorHex: Long = 0xFFF8FAFC,
    val backgroundTransparency: Float = 0.15f,
    val showAnalogSticksOverlay: Boolean = false,

    // Digit Animation Engine
    val animationStyle: DigitAnimationStyle = DigitAnimationStyle.FLIP_CLOCK,
    val animationSpeedMultiplier: Float = 1.0f,
    val animationDurationMs: Int = 420,
    val animationDirection: AnimationDirection = AnimationDirection.DOWN,
    val easingCurve: EasingCurveOption = EasingCurveOption.FAST_OUT_SLOW_IN,
    val transitionIntensity: Float = 0.85f,
    val individualDigitAnimation: Boolean = true,

    // Widget & Lock Screen
    val selectedWidgetPreview: WidgetLayoutType = WidgetLayoutType.COMPACT,
    val lockScreenNotificationEnabled: Boolean = true,
    val negativeCountdownEnabled: Boolean = true,
    val showSilverHighlights: Boolean = true
)
