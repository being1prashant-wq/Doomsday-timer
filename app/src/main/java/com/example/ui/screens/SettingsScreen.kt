package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.DoomsdaySettings
import com.example.domain.CountdownSnapshot
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NegativeCrimson
import com.example.ui.theme.SilverAccent
import com.example.ui.theme.SilverMuted
import com.example.ui.theme.SilverPrimary
import java.time.ZoneId

@Composable
fun SettingsScreen(
    snapshot: CountdownSnapshot,
    settings: DoomsdaySettings,
    simulateNegativeMode: Boolean,
    onToggleSimulateNegative: (Boolean) -> Unit,
    onUpdateSettings: ((DoomsdaySettings) -> DoomsdaySettings) -> Unit,
    onResetDefaults: () -> Unit
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
                text = "SETTINGS & DIAGNOSTICS",
                style = MaterialTheme.typography.headlineMedium.copy(
                    letterSpacing = 3.sp,
                    color = SilverPrimary,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "Target timestamp verification, negative countdown behavior, and privacy audit",
                style = MaterialTheme.typography.bodyMedium.copy(color = SilverMuted)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Section 1: Target Timestamp & Timezone Audit
            SectionHeader("1. HOROLOGICAL TARGET SPECIFICATION")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                DiagRow("Target Event", "DOOMSDAY")
                DiagRow("Target Date", "18 December 2026")
                DiagRow("Target Time", "12:00:00 AM (00:00:00)")
                DiagRow("Reference Time Zone", "Asia/Kolkata (IST / UTC+05:30)")
                DiagRow("Device Time Zone", ZoneId.systemDefault().id)
                DiagRow("Calculation Engine", "Calendar-Aware (Period + Duration)")
                DiagRow("Current Output", snapshot.formattedColonString)
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Section 2: Negative / Running Countdown Behavior
            SectionHeader("2. NEGATIVE / ELAPSED COUNTDOWN MODE")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Continue Running After Target Passes",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SilverPrimary
                            )
                        )
                        Text(
                            text = "Automatically counts elapsed time (-00 : 00 : 00 : 00 : 01) indefinitely after 18 Dec 2026 00:00 IST.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SilverMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Switch(
                        checked = settings.negativeCountdownEnabled,
                        onCheckedChange = { enabled ->
                            onUpdateSettings { it.copy(negativeCountdownEnabled = enabled) }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = SilverPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Test Negative Elapsed Countdown Now",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SilverPrimary
                            )
                        )
                        Text(
                            text = "Simulates post-target elapsed time (-00 : 00 : 00 : 00 : 01) in live preview",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SilverMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Switch(
                        checked = simulateNegativeMode,
                        onCheckedChange = onToggleSimulateNegative,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NegativeCrimson
                        ),
                        modifier = Modifier.testTag("settings_simulate_negative_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Section 3: Battery, Offline & Permission Audit
            SectionHeader("3. BATTERY & PERMISSION MINIMALISM")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BatteryChargingFull,
                        contentDescription = "Battery Safe",
                        tint = EmeraldActive,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Zero Wake Locks & 100% Offline Operation",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SilverPrimary
                        )
                    )
                }
                Text(
                    text = "• No background CPU loops or foreground services.\n• Automatically restores widgets and lock-screen display on device reboot (RECEIVE_BOOT_COMPLETED) and recalculates on time/timezone shifts.\n• Requests zero intrusive permissions (no location, camera, microphone, storage, contacts, or accessibility).",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SilverMuted,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Section 4: Reset Factory Defaults
            SectionHeader("4. FACTORY RESET")
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onResetDefaults,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1F2430),
                    contentColor = SilverPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                    .testTag("reset_defaults_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.RestartAlt,
                    contentDescription = "Reset",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset All Customization & Animations to Default",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SilverPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DiagRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = SilverMuted,
                fontSize = 12.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                color = SilverPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )
    }
}
