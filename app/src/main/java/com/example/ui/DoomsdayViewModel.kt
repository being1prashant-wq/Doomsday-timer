package com.example.ui

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AnimationDirection
import com.example.data.DigitAnimationStyle
import com.example.data.DoomsdaySettings
import com.example.data.EasingCurveOption
import com.example.data.GlassPreset
import com.example.data.HorizontalAlignOption
import com.example.data.PreferencesRepository
import com.example.data.VerticalAlignOption
import com.example.data.WidgetLayoutType
import com.example.domain.CountdownCalculator
import com.example.domain.CountdownSnapshot
import com.example.widget.CompactWidgetProvider
import com.example.widget.DoomsdaySystemUpdater
import com.example.widget.LargeWidgetProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

enum class DoomsdayTab(val route: String, val label: String) {
    HOME("home", "Home"),
    CUSTOMIZE("customize", "Customize"),
    ANIMATIONS("animations", "Animations"),
    WIDGETS("widgets", "Widgets"),
    LOCK_SCREEN("lock_screen", "Lock Screen"),
    SETTINGS("settings", "Settings")
}

class DoomsdayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PreferencesRepository(application)

    val settingsState: StateFlow<DoomsdaySettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DoomsdaySettings()
    )

    private val _currentTab = MutableStateFlow(DoomsdayTab.HOME)
    val currentTab: StateFlow<DoomsdayTab> = _currentTab.asStateFlow()

    // Simulation mode so user can also test "Target Reached / Negative Elapsed Mode" live in-app
    private val _simulateNegativeMode = MutableStateFlow(false)
    val simulateNegativeMode: StateFlow<Boolean> = _simulateNegativeMode.asStateFlow()

    private val _countdownSnapshot = MutableStateFlow(CountdownCalculator.calculate())
    val countdownSnapshot: StateFlow<CountdownSnapshot> = _countdownSnapshot.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    init {
        startPrecisionSecondTicker()
        viewModelScope.launch {
            DoomsdaySystemUpdater.refreshAllNow(getApplication())
        }
    }

    private fun startPrecisionSecondTicker() {
        viewModelScope.launch {
            var simulatedElapsedSeconds = 1L
            while (isActive) {
                val now = System.currentTimeMillis()
                if (_simulateNegativeMode.value) {
                    val simulatedInstant = Instant.ofEpochMilli(
                        CountdownCalculator.TARGET_EPOCH_MILLIS + (simulatedElapsedSeconds * 1000L)
                    )
                    val simulatedZoned = ZonedDateTime.ofInstant(
                        simulatedInstant,
                        ZoneId.systemDefault()
                    )
                    _countdownSnapshot.value = CountdownCalculator.calculateBetween(simulatedZoned)
                    simulatedElapsedSeconds++
                } else {
                    simulatedElapsedSeconds = 1L
                    _countdownSnapshot.value = CountdownCalculator.calculate(now)
                }

                // Align strictly to next system second boundary for zero drift
                val sleepMs = (1000L - (System.currentTimeMillis() % 1000L)).coerceIn(50L, 1000L)
                delay(sleepMs)
            }
        }
    }

    fun selectTab(tab: DoomsdayTab) {
        _currentTab.value = tab
    }

    fun toggleSimulateNegativeMode(enabled: Boolean) {
        _simulateNegativeMode.value = enabled
        _countdownSnapshot.value = if (enabled) {
            val simulatedInstant = Instant.ofEpochMilli(CountdownCalculator.TARGET_EPOCH_MILLIS + 1000L)
            CountdownCalculator.calculateBetween(
                ZonedDateTime.ofInstant(simulatedInstant, ZoneId.systemDefault())
            )
        } else {
            CountdownCalculator.calculate()
        }
    }

    fun clearStatusBanner() {
        _statusBannerMessage.value = null
    }

    fun applyGlassPreset(preset: GlassPreset) {
        viewModelScope.launch {
            repository.applyPreset(preset)
            DoomsdaySystemUpdater.refreshAllNow(getApplication())
            showStatus("${preset.title} preset applied")
        }
    }

    fun updateSettings(transform: (DoomsdaySettings) -> DoomsdaySettings) {
        viewModelScope.launch {
            repository.updateCustomization(transform)
            DoomsdaySystemUpdater.refreshAllNow(getApplication())
        }
    }

    fun setAnimationStyle(style: DigitAnimationStyle) {
        updateSettings { it.copy(animationStyle = style) }
    }

    fun setAnimationSpeed(speed: Float) {
        updateSettings { it.copy(animationSpeedMultiplier = speed) }
    }

    fun setAnimationDuration(durationMs: Int) {
        updateSettings { it.copy(animationDurationMs = durationMs) }
    }

    fun setAnimationDirection(direction: AnimationDirection) {
        updateSettings { it.copy(animationDirection = direction) }
    }

    fun setEasingCurve(easing: EasingCurveOption) {
        updateSettings { it.copy(easingCurve = easing) }
    }

    fun setTransitionIntensity(intensity: Float) {
        updateSettings { it.copy(transitionIntensity = intensity) }
    }

    fun setIndividualDigitAnimation(individual: Boolean) {
        updateSettings { it.copy(individualDigitAnimation = individual) }
    }

    fun setSelectedWidgetPreview(type: WidgetLayoutType) {
        updateSettings { it.copy(selectedWidgetPreview = type) }
    }

    fun setLockScreenNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateCustomization { it.copy(lockScreenNotificationEnabled = enabled) }
            DoomsdaySystemUpdater.refreshAllNow(getApplication())
            showStatus(
                if (enabled) "Persistent lock-screen countdown enabled"
                else "Lock-screen countdown notification disabled"
            )
        }
    }

    fun requestPinWidget(type: WidgetLayoutType) {
        val context = getApplication<Application>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val provider = when (type) {
                    WidgetLayoutType.COMPACT -> ComponentName(context, CompactWidgetProvider::class.java)
                    WidgetLayoutType.LARGE -> ComponentName(context, LargeWidgetProvider::class.java)
                }
                val pinned = appWidgetManager.requestPinAppWidget(provider, null, null)
                if (pinned) {
                    showStatus("Confirm widget placement in system prompt")
                    return
                }
            }
        }
        showStatus("Long-press your Home Screen → Widgets → Doomsday")
    }

    fun resetAllToDefaults() {
        viewModelScope.launch {
            repository.resetToDefaults()
            _simulateNegativeMode.value = false
            DoomsdaySystemUpdater.refreshAllNow(getApplication())
            showStatus("Restored factory Dark Glass & Flip Clock defaults")
        }
    }

    private fun showStatus(msg: String) {
        _statusBannerMessage.value = msg
    }
}
