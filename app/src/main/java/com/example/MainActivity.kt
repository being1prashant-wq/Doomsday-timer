package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.AutoAwesomeMotion
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LockClock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DoomsdayTab
import com.example.ui.DoomsdayViewModel
import com.example.ui.screens.AnimationsScreen
import com.example.ui.screens.CustomizeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LockScreenIntegrationScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WidgetsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SilverMuted
import com.example.ui.theme.SilverPrimary
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DoomsdayApp()
            }
        }
    }
}

@Composable
fun DoomsdayApp(
    viewModel: DoomsdayViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()
    val snapshot by viewModel.countdownSnapshot.collectAsStateWithLifecycle()
    val simulateNegative by viewModel.simulateNegativeMode.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    LaunchedEffect(bannerMessage) {
        if (bannerMessage != null) {
            delay(2500L)
            viewModel.clearStatusBanner()
        }
    }

    // BackHandler returns to Home when on any secondary studio screen
    if (currentTab != DoomsdayTab.HOME) {
        BackHandler {
            viewModel.selectTab(DoomsdayTab.HOME)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Subtle architectural obsidian glass background image with low opacity for depth
        Image(
            painter = painterResource(id = R.drawable.img_obsidian_glass_bg_1794154253148),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.22f)
        )

        // Deep Obsidian radial atmosphere overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ObsidianBlack.copy(alpha = 0.72f),
                            ObsidianBlack.copy(alpha = 0.90f),
                            ObsidianBlack.copy(alpha = 0.98f)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                DoomsdayBottomNavigationBar(
                    currentTab = currentTab,
                    onSelectTab = viewModel::selectTab
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    DoomsdayTab.HOME -> {
                        HomeScreen(
                            snapshot = snapshot,
                            settings = settings,
                            simulateNegativeMode = simulateNegative,
                            onToggleSimulateNegative = viewModel::toggleSimulateNegativeMode,
                            onToggleAnalogOverlay = { show ->
                                viewModel.updateSettings { it.copy(showAnalogSticksOverlay = show) }
                            },
                            onNavigateToTab = viewModel::selectTab
                        )
                    }

                    DoomsdayTab.CUSTOMIZE -> {
                        CustomizeScreen(
                            snapshot = snapshot,
                            settings = settings,
                            onSelectPreset = viewModel::applyGlassPreset,
                            onUpdateSettings = viewModel::updateSettings
                        )
                    }

                    DoomsdayTab.ANIMATIONS -> {
                        AnimationsScreen(
                            snapshot = snapshot,
                            settings = settings,
                            onSelectAnimation = viewModel::setAnimationStyle,
                            onUpdateSpeed = viewModel::setAnimationSpeed,
                            onUpdateDuration = viewModel::setAnimationDuration,
                            onUpdateDirection = viewModel::setAnimationDirection,
                            onUpdateEasing = viewModel::setEasingCurve,
                            onUpdateIntensity = viewModel::setTransitionIntensity,
                            onUpdateIndividualDigit = viewModel::setIndividualDigitAnimation
                        )
                    }

                    DoomsdayTab.WIDGETS -> {
                        WidgetsScreen(
                            snapshot = snapshot,
                            settings = settings,
                            onSelectWidgetType = viewModel::setSelectedWidgetPreview,
                            onUpdateSettings = viewModel::updateSettings,
                            onRequestPinWidget = viewModel::requestPinWidget
                        )
                    }

                    DoomsdayTab.LOCK_SCREEN -> {
                        LockScreenIntegrationScreen(
                            snapshot = snapshot,
                            settings = settings,
                            onToggleLockScreenNotification = viewModel::setLockScreenNotificationEnabled
                        )
                    }

                    DoomsdayTab.SETTINGS -> {
                        SettingsScreen(
                            snapshot = snapshot,
                            settings = settings,
                            simulateNegativeMode = simulateNegative,
                            onToggleSimulateNegative = viewModel::toggleSimulateNegativeMode,
                            onUpdateSettings = viewModel::updateSettings,
                            onResetDefaults = viewModel::resetAllToDefaults
                        )
                    }
                }

                // Floating glass feedback toast banner
                AnimatedVisibility(
                    visible = bannerMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp, start = 24.dp, end = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF1A202C).copy(alpha = 0.94f))
                            .border(1.dp, Color.White.copy(alpha = 0.24f), RoundedCornerShape(50))
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = bannerMessage.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SilverPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

private data class NavItemSpec(
    val tab: DoomsdayTab,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
private fun DoomsdayBottomNavigationBar(
    currentTab: DoomsdayTab,
    onSelectTab: (DoomsdayTab) -> Unit
) {
    val items = listOf(
        NavItemSpec(DoomsdayTab.HOME, Icons.Filled.Home, Icons.Outlined.Home),
        NavItemSpec(DoomsdayTab.CUSTOMIZE, Icons.Filled.Palette, Icons.Outlined.Palette),
        NavItemSpec(DoomsdayTab.ANIMATIONS, Icons.Filled.AutoAwesomeMotion, Icons.Outlined.AutoAwesomeMotion),
        NavItemSpec(DoomsdayTab.WIDGETS, Icons.Filled.Widgets, Icons.Outlined.Widgets),
        NavItemSpec(DoomsdayTab.LOCK_SCREEN, Icons.Filled.LockClock, Icons.Outlined.LockClock),
        NavItemSpec(DoomsdayTab.SETTINGS, Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF07090E).copy(alpha = 0.94f))
            .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.14f))
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { spec ->
                val selected = currentTab == spec.tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selected) Color.White.copy(alpha = 0.12f) else Color.Transparent
                        )
                        .clickable { onSelectTab(spec.tab) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("bottom_nav_${spec.tab.route}")
                ) {
                    Icon(
                        imageVector = if (selected) spec.selectedIcon else spec.unselectedIcon,
                        contentDescription = spec.tab.label,
                        tint = if (selected) SilverPrimary else SilverMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = spec.tab.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            letterSpacing = 0.2.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) SilverPrimary else SilverMuted
                        )
                    )
                }
            }
        }
    }
}
