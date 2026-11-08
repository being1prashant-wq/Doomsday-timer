package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesomeMotion
import androidx.compose.material.icons.outlined.LockClock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DigitAnimationStyle
import com.example.data.DoomsdaySettings
import com.example.domain.CountdownSnapshot
import com.example.ui.DoomsdayTab
import com.example.ui.components.GlassmorphicCountdownCard
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NegativeCrimson
import com.example.ui.theme.SilverAccent
import com.example.ui.theme.SilverMuted
import com.example.ui.theme.SilverPrimary
import com.example.ui.theme.SpaceGroteskFontFamily

@Composable
fun HomeScreen(
    snapshot: CountdownSnapshot,
    settings: DoomsdaySettings,
    simulateNegativeMode: Boolean,
    onToggleSimulateNegative: (Boolean) -> Unit,
    onToggleAnalogOverlay: (Boolean) -> Unit,
    onNavigateToTab: (DoomsdayTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Minimalist Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DOOMSDAY",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            letterSpacing = 4.sp,
                            color = SilverPrimary
                        )
                    )
                    Text(
                        text = "TARGET • 18 DEC 2026 • 12:00:00 AM IST",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SilverMuted,
                            letterSpacing = 1.4.sp
                        )
                    )
                }

                // Active Status Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.06f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (snapshot.isNegative) NegativeCrimson else EmeraldActive)
                    )
                    Text(
                        text = if (snapshot.isNegative) "ELAPSED" else "LIVE IST",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SilverPrimary,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Primary Live Glassmorphism Countdown Display
            GlassmorphicCountdownCard(
                snapshot = snapshot,
                settings = settings,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_live_countdown_card")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Mode Toggle Strip: Analog Dark BG + White Sticks & Elapsed Simulation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val isAnalogActive =
                    settings.animationStyle == DigitAnimationStyle.ANALOG_WHITE_STICKS || settings.showAnalogSticksOverlay

                QuickTogglePill(
                    title = "Analog White Sticks",
                    subtitle = if (isAnalogActive) "Active on Dial" else "Tap to show dial",
                    active = isAnalogActive,
                    onClick = { onToggleAnalogOverlay(!isAnalogActive) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("toggle_analog_sticks_button")
                )

                QuickTogglePill(
                    title = "Elapsed Mode Preview",
                    subtitle = if (simulateNegativeMode) "-00 : 00 : 00 : 01" else "Test Post-Target",
                    active = simulateNegativeMode,
                    onClick = { onToggleSimulateNegative(!simulateNegativeMode) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("toggle_negative_preview_button")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Active Configuration Summary Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0C0E14).copy(alpha = 0.85f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ConfigMetricItem(
                        label = "PRESET",
                        value = settings.preset.title
                    )
                    ConfigMetricItem(
                        label = "ANIMATION",
                        value = settings.animationStyle.title
                    )
                    ConfigMetricItem(
                        label = "LOCK SCREEN",
                        value = if (settings.lockScreenNotificationEnabled) "Active" else "Off"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Grid: Customize, Animations, Widget Setup, Lock Screen
            Text(
                text = "STUDIO & SYSTEM SURFACES",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SilverMuted,
                    letterSpacing = 1.8.sp
                ),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeActionCard(
                    title = "Customize Glass",
                    subtitle = "6 presets, blur, opacity & typography",
                    icon = Icons.Outlined.Palette,
                    onClick = { onNavigateToTab(DoomsdayTab.CUSTOMIZE) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_customize_card")
                )
                HomeActionCard(
                    title = "Digit Animations",
                    subtitle = "Flip Clock, Analog Sticks & 11 transitions",
                    icon = Icons.Outlined.AutoAwesomeMotion,
                    onClick = { onNavigateToTab(DoomsdayTab.ANIMATIONS) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_animations_card")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeActionCard(
                    title = "Home Widgets",
                    subtitle = "Compact & Large resizable widgets",
                    icon = Icons.Outlined.Widgets,
                    onClick = { onNavigateToTab(DoomsdayTab.WIDGETS) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_widgets_card")
                )
                HomeActionCard(
                    title = "Lock Screen",
                    subtitle = "Live lock-screen display & setup",
                    icon = Icons.Outlined.LockClock,
                    onClick = { onNavigateToTab(DoomsdayTab.LOCK_SCREEN) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_lockscreen_card")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Widget & Lock-Screen Compatibility Briefing Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF131620).copy(alpha = 0.72f),
                                Color(0xFF090B10).copy(alpha = 0.85f)
                            )
                        )
                    )
                    .border(0.5.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = "Precision Info",
                            tint = SilverAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "CALENDAR-ACCURATE IST ENGINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SilverAccent,
                                letterSpacing = 1.4.sp
                            )
                        )
                    }
                    Text(
                        text = "Calculates real calendar months (28/29/30/31 days) and remaining days, hours, minutes, and seconds until 18 December 2026, 00:00 IST. Automatically adapts to device reboots, time-zone shifts, and continues into negative elapsed time once reached.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SilverMuted,
                            lineHeight = 19.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickTogglePill(
    title: String,
    subtitle: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (active) Color.White.copy(alpha = 0.12f)
                else Color(0xFF0D0F15).copy(alpha = 0.75f)
            )
            .border(
                width = 1.dp,
                color = if (active) SilverPrimary.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.10f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SilverPrimary,
                        fontSize = 12.sp
                    )
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (active) EmeraldActive else Color.White.copy(alpha = 0.2f))
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SilverMuted,
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMonoFontFamily
                )
            )
        }
    }
}

@Composable
private fun ConfigMetricItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = SilverMuted,
                fontSize = 9.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = SilverPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF151923).copy(alpha = 0.78f),
                        Color(0xFF0B0D13).copy(alpha = 0.90f)
                    )
                )
            )
            .border(0.75.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.07f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = SilverPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SilverPrimary,
                    fontSize = 14.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SilverMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            )
        }
    }
}
