package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.DoomsdaySettings
import com.example.domain.CountdownSnapshot
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.SilverAccent
import com.example.ui.theme.SilverMuted
import com.example.ui.theme.SilverPrimary
import com.example.ui.theme.SpaceGroteskFontFamily

@Composable
fun LockScreenIntegrationScreen(
    snapshot: CountdownSnapshot,
    settings: DoomsdaySettings,
    onToggleLockScreenNotification: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var hasNotifPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotifPermission = granted
        if (granted) {
            onToggleLockScreenNotification(true)
        }
    }

    val manufacturer = Build.MANUFACTURER.uppercase()
    val sdkInt = Build.VERSION.SDK_INT

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
                text = "LOCK SCREEN INTEGRATION",
                style = MaterialTheme.typography.headlineMedium.copy(
                    letterSpacing = 3.sp,
                    color = SilverPrimary,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "Official zero-root lock-screen visibility across all Android devices",
                style = MaterialTheme.typography.bodyMedium.copy(color = SilverMuted)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Simulated Lock Screen Live Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF141822),
                                Color(0xFF07090D)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(22.dp))
                    .padding(18.dp)
                    .testTag("lockscreen_preview_card")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Lock Screen Preview",
                            tint = SilverAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "LOCK SCREEN LIVE NOTIFICATION PREVIEW",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SilverAccent,
                                fontSize = 9.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated Notification Card on Lock Screen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1A1F2C).copy(alpha = 0.88f))
                            .border(0.75.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "DOOMSDAY • 18 DEC 2026 IST",
                                    style = TextStyle(
                                        fontFamily = SpaceGroteskFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.4.sp,
                                        color = SilverAccent
                                    )
                                )
                                Text(
                                    text = "PUBLIC • SILENT",
                                    style = TextStyle(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 9.sp,
                                        color = EmeraldActive
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = snapshot.formattedColonString,
                                style = TextStyle(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    color = SilverPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Month | Days | Hours | Minutes | Seconds",
                                style = TextStyle(
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontSize = 11.sp,
                                    color = SilverMuted
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Solution: Persistent Lock Screen Countdown Notification (Default Enabled)
            SectionHeader("1. PERSISTENT LOCK-SCREEN LIVE DISPLAY (RECOMMENDED)")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lock Screen Live Countdown",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SilverPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Uses Android VISIBILITY_PUBLIC ongoing chronometer surface so the countdown stays visible on any lock screen without root or wake locks.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SilverMuted,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = settings.lockScreenNotificationEnabled && hasNotifPermission,
                        onCheckedChange = { wantEnabled ->
                            if (wantEnabled && !hasNotifPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onToggleLockScreenNotification(wantEnabled)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = EmeraldActive
                        ),
                        modifier = Modifier.testTag("lockscreen_notif_switch")
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotifPermission) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SilverPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("grant_notif_permission_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsActive,
                            contentDescription = "Grant Notification Permission",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Grant Notification Permission for Lock Screen",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent().apply {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            } else {
                                action = Settings.ACTION_SETTINGS
                            }
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        runCatching { context.startActivity(intent) }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_system_lockscreen_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "System Settings",
                        tint = SilverPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Verify 'Show on Lock Screen' in Android Settings",
                        color = SilverPrimary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Device Capability Inspector & Honest Lock-Screen Widget Guidance
            SectionHeader("2. DEVICE LOCK-SCREEN WIDGET COMPATIBILITY")
            Spacer(modifier = Modifier.height(10.dp))

            StudioCardContainer {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhoneAndroid,
                        contentDescription = "Device Detected",
                        tint = SilverPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Detected Device: ${Build.MANUFACTURER} ${Build.MODEL} (API $sdkInt)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SilverPrimary
                            )
                        )
                        Text(
                            text = "Keyguard Widget Category: Configured (home_screen | keyguard)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = EmeraldActive,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Important Technical Reality: Ordinary Android home-screen widgets do NOT automatically appear on the lock screen on standard smartphones. Doomsday includes both keyguard widget declarations and the Persistent Lock-Screen Live Notification so you are guaranteed lock-screen visibility on every phone:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SilverMuted,
                        lineHeight = 18.sp
                    )
                )

                val compatibilityNotes = listOf(
                    "• All Android Phones (Universal): Enable the Persistent Lock-Screen Countdown toggle above and ensure Settings → Notifications → Lock screen notifications is set to 'Show alerting and silent notifications'.",
                    "• Samsung Galaxy (One UI / LockStar): Open Settings → Lock screen → Widgets (or Good Lock LockStar) → Add Doomsday Compact or Large widget directly to the lock screen.",
                    "• Google Pixel Tablets & Android 15+ Hub Mode: Swipe right on the lock screen to open Glanceable Hub → Long-press → Customize Widgets → Select Doomsday."
                )

                compatibilityNotes.forEach { note ->
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SilverPrimary.copy(alpha = 0.88f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Security & Privacy Assurance Card
            StudioCardContainer {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VerifiedUser,
                        contentDescription = "Zero Root",
                        tint = EmeraldActive,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "100% Official Android APIs • No Root, No Custom ROM, No Accessibility Hacks",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SilverPrimary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
