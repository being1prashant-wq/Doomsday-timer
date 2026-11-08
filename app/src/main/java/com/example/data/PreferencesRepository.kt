package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.doomsdayDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "doomsday_preferences"
)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val PRESET = stringPreferencesKey("preset")
        val FONT_SIZE_SP = floatPreferencesKey("font_size_sp")
        val FONT_WEIGHT = intPreferencesKey("font_weight")
        val TEXT_OPACITY = floatPreferencesKey("text_opacity")
        val GLASS_OPACITY = floatPreferencesKey("glass_opacity")
        val BORDER_OPACITY = floatPreferencesKey("border_opacity")
        val BLUR_INTENSITY = floatPreferencesKey("blur_intensity")
        val LETTER_SPACING = floatPreferencesKey("letter_spacing")
        val NUMBER_SPACING = floatPreferencesKey("number_spacing")
        val HORIZONTAL_ALIGN = stringPreferencesKey("horizontal_align")
        val VERTICAL_ALIGN = stringPreferencesKey("vertical_align")
        val WIDGET_WIDTH_DP = floatPreferencesKey("widget_width_dp")
        val WIDGET_HEIGHT_DP = floatPreferencesKey("widget_height_dp")
        val CORNER_RADIUS_DP = floatPreferencesKey("corner_radius_dp")
        val TEXT_COLOR_HEX = longPreferencesKey("text_color_hex")
        val BG_TRANSPARENCY = floatPreferencesKey("bg_transparency")
        val SHOW_ANALOG_STICKS = booleanPreferencesKey("show_analog_sticks")

        val ANIM_STYLE = stringPreferencesKey("anim_style")
        val ANIM_SPEED = floatPreferencesKey("anim_speed")
        val ANIM_DURATION = intPreferencesKey("anim_duration")
        val ANIM_DIRECTION = stringPreferencesKey("anim_direction")
        val EASING_CURVE = stringPreferencesKey("easing_curve")
        val TRANSITION_INTENSITY = floatPreferencesKey("transition_intensity")
        val INDIVIDUAL_DIGIT = booleanPreferencesKey("individual_digit")

        val WIDGET_PREVIEW_TYPE = stringPreferencesKey("widget_preview_type")
        val LOCK_SCREEN_NOTIF_ENABLED = booleanPreferencesKey("lock_screen_notif_enabled")
        val NEGATIVE_COUNTDOWN_ENABLED = booleanPreferencesKey("negative_countdown_enabled")
        val SHOW_SILVER_HIGHLIGHTS = booleanPreferencesKey("show_silver_highlights")
    }

    val settingsFlow: Flow<DoomsdaySettings> = context.doomsdayDataStore.data.map { prefs ->
        val preset = GlassPreset.fromId(prefs[Keys.PRESET] ?: GlassPreset.DARK_GLASS.id)
        DoomsdaySettings(
            preset = preset,
            fontSizeSp = prefs[Keys.FONT_SIZE_SP] ?: 28f,
            fontWeightVal = prefs[Keys.FONT_WEIGHT] ?: 700,
            textOpacity = prefs[Keys.TEXT_OPACITY] ?: preset.textOpacity,
            glassOpacity = prefs[Keys.GLASS_OPACITY] ?: preset.glassOpacity,
            borderOpacity = prefs[Keys.BORDER_OPACITY] ?: preset.borderOpacity,
            blurIntensity = prefs[Keys.BLUR_INTENSITY] ?: preset.blurIntensity,
            letterSpacingEm = prefs[Keys.LETTER_SPACING] ?: 0.04f,
            numberSpacingDp = prefs[Keys.NUMBER_SPACING] ?: 6f,
            horizontalAlignment = HorizontalAlignOption.fromId(
                prefs[Keys.HORIZONTAL_ALIGN] ?: HorizontalAlignOption.CENTER.id
            ),
            verticalAlignment = VerticalAlignOption.fromId(
                prefs[Keys.VERTICAL_ALIGN] ?: VerticalAlignOption.CENTER.id
            ),
            widgetWidthDp = prefs[Keys.WIDGET_WIDTH_DP] ?: 340f,
            widgetHeightDp = prefs[Keys.WIDGET_HEIGHT_DP] ?: 156f,
            cornerRadiusDp = prefs[Keys.CORNER_RADIUS_DP] ?: 24f,
            textColorHex = prefs[Keys.TEXT_COLOR_HEX] ?: preset.textColorHex,
            backgroundTransparency = prefs[Keys.BG_TRANSPARENCY] ?: preset.backgroundTransparency,
            showAnalogSticksOverlay = prefs[Keys.SHOW_ANALOG_STICKS] ?: false,
            animationStyle = DigitAnimationStyle.fromId(
                prefs[Keys.ANIM_STYLE] ?: DigitAnimationStyle.FLIP_CLOCK.id
            ),
            animationSpeedMultiplier = prefs[Keys.ANIM_SPEED] ?: 1.0f,
            animationDurationMs = prefs[Keys.ANIM_DURATION] ?: 420,
            animationDirection = AnimationDirection.fromId(
                prefs[Keys.ANIM_DIRECTION] ?: AnimationDirection.DOWN.id
            ),
            easingCurve = EasingCurveOption.fromId(
                prefs[Keys.EASING_CURVE] ?: EasingCurveOption.FAST_OUT_SLOW_IN.id
            ),
            transitionIntensity = prefs[Keys.TRANSITION_INTENSITY] ?: 0.85f,
            individualDigitAnimation = prefs[Keys.INDIVIDUAL_DIGIT] ?: true,
            selectedWidgetPreview = WidgetLayoutType.fromId(
                prefs[Keys.WIDGET_PREVIEW_TYPE] ?: WidgetLayoutType.COMPACT.id
            ),
            lockScreenNotificationEnabled = prefs[Keys.LOCK_SCREEN_NOTIF_ENABLED] ?: true,
            negativeCountdownEnabled = prefs[Keys.NEGATIVE_COUNTDOWN_ENABLED] ?: true,
            showSilverHighlights = prefs[Keys.SHOW_SILVER_HIGHLIGHTS] ?: true
        )
    }

    suspend fun getSnapshot(): DoomsdaySettings = settingsFlow.first()

    suspend fun applyPreset(preset: GlassPreset) {
        context.doomsdayDataStore.edit { prefs ->
            prefs[Keys.PRESET] = preset.id
            prefs[Keys.TEXT_OPACITY] = preset.textOpacity
            prefs[Keys.GLASS_OPACITY] = preset.glassOpacity
            prefs[Keys.BORDER_OPACITY] = preset.borderOpacity
            prefs[Keys.BLUR_INTENSITY] = preset.blurIntensity
            prefs[Keys.TEXT_COLOR_HEX] = preset.textColorHex
            prefs[Keys.BG_TRANSPARENCY] = preset.backgroundTransparency
        }
    }

    suspend fun updateCustomization(transform: (DoomsdaySettings) -> DoomsdaySettings) {
        val current = getSnapshot()
        val updated = transform(current)
        saveFullSettings(updated)
    }

    suspend fun resetToDefaults() {
        context.doomsdayDataStore.edit { it.clear() }
    }

    private suspend fun saveFullSettings(s: DoomsdaySettings) {
        context.doomsdayDataStore.edit { prefs ->
            prefs[Keys.PRESET] = s.preset.id
            prefs[Keys.FONT_SIZE_SP] = s.fontSizeSp
            prefs[Keys.FONT_WEIGHT] = s.fontWeightVal
            prefs[Keys.TEXT_OPACITY] = s.textOpacity
            prefs[Keys.GLASS_OPACITY] = s.glassOpacity
            prefs[Keys.BORDER_OPACITY] = s.borderOpacity
            prefs[Keys.BLUR_INTENSITY] = s.blurIntensity
            prefs[Keys.LETTER_SPACING] = s.letterSpacingEm
            prefs[Keys.NUMBER_SPACING] = s.numberSpacingDp
            prefs[Keys.HORIZONTAL_ALIGN] = s.horizontalAlignment.id
            prefs[Keys.VERTICAL_ALIGN] = s.verticalAlignment.id
            prefs[Keys.WIDGET_WIDTH_DP] = s.widgetWidthDp
            prefs[Keys.WIDGET_HEIGHT_DP] = s.widgetHeightDp
            prefs[Keys.CORNER_RADIUS_DP] = s.cornerRadiusDp
            prefs[Keys.TEXT_COLOR_HEX] = s.textColorHex
            prefs[Keys.BG_TRANSPARENCY] = s.backgroundTransparency
            prefs[Keys.SHOW_ANALOG_STICKS] = s.showAnalogSticksOverlay

            prefs[Keys.ANIM_STYLE] = s.animationStyle.id
            prefs[Keys.ANIM_SPEED] = s.animationSpeedMultiplier
            prefs[Keys.ANIM_DURATION] = s.animationDurationMs
            prefs[Keys.ANIM_DIRECTION] = s.animationDirection.id
            prefs[Keys.EASING_CURVE] = s.easingCurve.id
            prefs[Keys.TRANSITION_INTENSITY] = s.transitionIntensity
            prefs[Keys.INDIVIDUAL_DIGIT] = s.individualDigitAnimation

            prefs[Keys.WIDGET_PREVIEW_TYPE] = s.selectedWidgetPreview.id
            prefs[Keys.LOCK_SCREEN_NOTIF_ENABLED] = s.lockScreenNotificationEnabled
            prefs[Keys.NEGATIVE_COUNTDOWN_ENABLED] = s.negativeCountdownEnabled
            prefs[Keys.SHOW_SILVER_HIGHLIGHTS] = s.showSilverHighlights
        }
    }
}
