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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AnimationDirection
import com.example.data.DigitAnimationStyle
import com.example.data.DoomsdaySettings
import com.example.data.EasingCurveOption
import com.example.domain.CountdownSnapshot
import com.example.ui.components.GlassmorphicCountdownCard
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.SilverAccent
import com.example.ui.theme.SilverMuted
import com.example.ui.theme.SilverPrimary
import java.util.Locale

@Composable
fun AnimationsScreen(
    snapshot: CountdownSnapshot,
    settings: DoomsdaySettings,
    onSelectAnimation: (DigitAnimationStyle) -> Unit,
    onUpdateSpeed: (Float) -> Unit,
    onUpdateDuration: (Int) -> Unit,
    onUpdateDirection: (AnimationDirection) -> Unit,
    onUpdateEasing: (EasingCurveOption) -> Unit,
    onUpdateIntensity: (Float) -> Unit,
    onUpdateIndividualDigit: (Boolean) -> Unit
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
                .widthIn(max = 600.dp)
        ) {
            Text(
                text = "DIGIT ANIMATION ENGINE",
                style = MaterialTheme.typography.headlineMedium.copy(
                    letterSpacing = 3.sp,
                    color = SilverPrimary,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "12 digital transitions + Analog Dark BG with White Sticks chronometer",
                style = MaterialTheme.typography.bodyMedium.copy(color = SilverMuted)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Live Interactive Animation Sandbox Preview
            GlassmorphicCountdownCard(
                snapshot = snapshot,
                settings = settings,
                compactMode = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("animation_live_preview")
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionHeader("1. SELECT ANIMATION STYLE (13 MODES)")
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DigitAnimationStyle.entries.forEach { style ->
                    val selected = settings.animationStyle == style
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (selected) Color.White.copy(alpha = 0.13f)
                                else Color(0xFF0D1017).copy(alpha = 0.85f)
                            )
                            .border(
                                width = if (selected) 1.5.dp else 0.75.dp,
                                color = if (selected) SilverPrimary else Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectAnimation(style) }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("anim_option_${style.id.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = style.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SilverPrimary,
                                        fontSize = 14.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = style.description,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = SilverMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selected) EmeraldActive else Color.White.copy(alpha = 0.15f)
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("2. MOTION PHYSICS & TIMING")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                ParameterSliderRow(
                    label = "Animation Speed Multiplier",
                    valueText = String.format(Locale.US, "%.2fx", settings.animationSpeedMultiplier),
                    value = settings.animationSpeedMultiplier,
                    valueRange = 0.4f..2.5f,
                    onValueChange = onUpdateSpeed
                )

                ParameterSliderRow(
                    label = "Animation Duration",
                    valueText = "${settings.animationDurationMs} ms",
                    value = settings.animationDurationMs.toFloat(),
                    valueRange = 120f..850f,
                    onValueChange = { onUpdateDuration(it.toInt()) }
                )

                ParameterSliderRow(
                    label = "Transition Intensity",
                    valueText = "${(settings.transitionIntensity * 100).toInt()}%",
                    value = settings.transitionIntensity,
                    valueRange = 0.25f..1.4f,
                    onValueChange = onUpdateIntensity
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Animation Direction",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SilverPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnimationDirection.entries.forEach { dir ->
                        SelectablePillButton(
                            label = dir.label,
                            active = settings.animationDirection == dir,
                            onClick = { onUpdateDirection(dir) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Easing Curve",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SilverPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    EasingCurveOption.entries.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { curve ->
                                SelectablePillButton(
                                    label = curve.label,
                                    active = settings.easingCurve == curve,
                                    onClick = { onUpdateEasing(curve) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (settings.individualDigitAnimation) {
                                "Individual Digit Mode (59 → 58)"
                            } else {
                                "Entire Number Block Mode"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SilverPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Animate only the changing digit vs. the full 2-digit unit",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SilverMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Switch(
                        checked = settings.individualDigitAnimation,
                        onCheckedChange = onUpdateIndividualDigit,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = SilverPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Technical transparency note on Android Home Widgets
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0B0E14))
                    .border(0.5.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Technical Info",
                        tint = SilverAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Battery & Surface Architecture: All 13 custom 60fps digit and analog-stick animations run live inside the Doomsday studio preview. For home-screen RemoteViews widgets, Android restricts per-second custom frame animations to protect battery life; widgets use synchronized system updates and native chronometer timing.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SilverMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
